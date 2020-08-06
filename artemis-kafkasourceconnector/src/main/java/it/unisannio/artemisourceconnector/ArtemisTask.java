package it.unisannio.artemisourceconnector;

import it.unisannio.util.ArtemisProvider;
import it.unisannio.util.CachedClientSessionProvider;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArtemisTask extends SourceTask {
  private static final Logger log = LoggerFactory.getLogger(ArtemisTask.class);

  private ArtemisTaskConfig config;
  private CachedClientSessionProvider cachedClientSessionProvider;
  private ArtemisProvider provider;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private PriorityQueue<Dequeuer> dequeuers = new PriorityQueue<>();
  private Time time;

  public ArtemisTask() {
    this.time = new SystemTime();
  }

  public ArtemisTask(Time time) {
    this.time = time;
  }

  @Override
  public String version() {
    return VersionUtil.getVersion();
  }

  @Override
  public void start(Map<String, String> properties) {
    log.info("Starting Artemis source task");
    try {
      config = new ArtemisTaskConfig(properties);
    } catch (ConfigException e) {
      throw new ConnectException("Couldn't start ArtemisTask due to configuration error", e);
    }

    final int maxConnAttempts = config.getInt(ArtemisConnectorConfig.CONNECTION_ATTEMPTS_CONFIG);
    final long retryBackoff = config.getLong(ArtemisConnectorConfig.CONNECTION_BACKOFF_CONFIG);

    try {
      provider = new ArtemisProvider(config);
    } catch (Exception e) {
      e.printStackTrace();
    }

    cachedClientSessionProvider = clientSessionProvider(maxConnAttempts, retryBackoff);

    List<String> queues = config.getList(ArtemisTaskConfig.QUEUE_CONFIG);
    if (queues.isEmpty())
      throw new ConnectException("Invalid configuration: each ArtemisTask must have at least one queue assigned to it");

    String mode = config.getString(ArtemisTaskConfig.MODE_CONFIG);

    for (String queue: queues) {
      String topicPrefix = config.getString(ArtemisTaskConfig.TOPIC_PREFIX_CONFIG);
      if (mode.equals(ArtemisTaskConfig.MODE_BULK)) {
        int maxMsgs =config.getInt(ArtemisTaskConfig.BATCH_MAX_MSGS_CONFIG);
        dequeuers.add(new BulkDequeuer(provider, queue, topicPrefix, maxMsgs));
      }
    }

    running.set(true);
    log.info("Started Artemis source task");
  }

  protected CachedClientSessionProvider clientSessionProvider(int maxConnAttempts, long retryBackoff) {
    return new CachedClientSessionProvider(provider, maxConnAttempts, retryBackoff);
  }

  @Override
  public List<SourceRecord> poll() {
    log.info("Polling for new data...");

    while (running.get()) {
      final Dequeuer dequeuer = dequeuers.peek();

      assert dequeuer != null;
      if (!dequeuer.querying()) {
        // If not in the middle of an update, wait for next update time
        final long nextUpdate = dequeuer.getLastUpdate() + config.getInt(ArtemisTaskConfig.POLL_INTERVAL_MS_CONFIG);
        final long now = time.milliseconds();
        final long sleepMs = Math.min(nextUpdate - now, 100);
        if (sleepMs > 0) {
          log.info("Waiting {} ms to poll {} next", nextUpdate - now, dequeuer.toString());
          time.sleep(sleepMs);
          continue; // Re-check stop flag before continuing
        }
      }

      final List<SourceRecord> results = new ArrayList<>();
      try {
        log.info("Checking for next block of results from {}", dequeuer.toString());
        dequeuer.maybeStartQuery(cachedClientSessionProvider.getClientSession());
        int batchMaxMsgs = config.getInt(ArtemisTaskConfig.BATCH_MAX_MSGS_CONFIG);
        boolean hadNext = true;
        while (results.size() < batchMaxMsgs && (hadNext = dequeuer.next()))
          results.add(dequeuer.extractRecord());
        if (!hadNext)
          // If we finished processing the results from the current query, we can reset and send the dequeuer to the tail of the queue
          resetAndRequeueHead(dequeuer);
        if (results.isEmpty()) {
          log.info("No updates for {}", dequeuer.toString());
          continue;
        }

        log.info("Returning {} records for {}", results.size(), dequeuer.toString());
        return results;
      } catch (Exception e) {
        log.error("Failed to run query for queue {}: {}", dequeuer.toString(), e);
        resetAndRequeueHead(dequeuer);
        return null;
      } catch (Throwable t) {
        resetAndRequeueHead(dequeuer);
        // This task has failed, so close any resources (may be reopened if needed) before throwing
        closeResources();
        throw t;
      }
    }

    // Only in case of shutdown
    final Dequeuer querier = dequeuers.peek();
    if (querier != null)
      resetAndRequeueHead(querier);
    closeResources();
    return null;
  }

  private void resetAndRequeueHead(Dequeuer expectedHead) {
    log.info("Resetting dequeuer {}", expectedHead.toString());
    Dequeuer removedDequeuer = dequeuers.poll();
    assert removedDequeuer == expectedHead;
    expectedHead.reset(time.milliseconds());
    dequeuers.add(expectedHead);
    cachedClientSessionProvider.close();
  }


  @Override
  public void stop() throws ConnectException {
    log.info("Stopping Artemis source task");
    running.set(false);
    // All resources are closed at the end of 'poll()' when no longer running or if there is an error
  }

  protected void closeResources() {
    log.info("Closing resources for Artemis source task");
    try {
      if (cachedClientSessionProvider != null)
        cachedClientSessionProvider.close();
    } catch (Throwable t) {
      log.warn("Error while closing the connections", t);
    } finally {
      cachedClientSessionProvider = null;
      try {
        if (provider != null)
          provider.close();
      } catch (Throwable t) {
        log.warn("Error while closing the {} provider: ", "artemis", t);
      } finally {
        provider = null;
      }
    }
  }
}