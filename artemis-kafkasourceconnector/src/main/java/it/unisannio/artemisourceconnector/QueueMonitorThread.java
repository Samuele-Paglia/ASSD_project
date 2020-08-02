package it.unisannio.artemisourceconnector;

import it.unisannio.util.ArtemisProvider;
import it.unisannio.util.ClientSessionProvider;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class QueueMonitorThread extends Thread {
    private static final Logger log = LoggerFactory.getLogger(QueueMonitorThread.class);

    private final ArtemisProvider provider;
    private final ClientSessionProvider clientsessionProvider;
    private final ConnectorContext context;
    private final CountDownLatch shutdownLatch;
    private final long pollMs;
    private List<String> queues;

    public QueueMonitorThread(ArtemisProvider provider, ClientSessionProvider clientsessionProvider, ConnectorContext context, long pollMs) {
        this.provider = provider;
        this.clientsessionProvider = clientsessionProvider;
        this.context = context;
        this.shutdownLatch = new CountDownLatch(1);
        this.pollMs = pollMs;
        this.queues = null;
    }

    @Override
    public void run() {
        log.info("Starting thread to monitor queues.");
        while (shutdownLatch.getCount() > 0) {
            try {
                if (updateQueues()) {
                    context.requestTaskReconfiguration();
                }
            } catch (Exception e) {
                context.raiseError(e);
                throw e;
            }

            try {
                log.debug("Waiting {} ms to check for changed.", pollMs);
                boolean shuttingDown = shutdownLatch.await(pollMs, TimeUnit.MILLISECONDS);
                if (shuttingDown) {
                    return;
                }
            } catch (InterruptedException e) {
                log.error("Unexpected InterruptedException, ignoring: ", e);
            }
        }
    }

    public synchronized List<String> queues() {
        //TODO: Timeout should probably be user-configurable or class-level constant
        final long timeout = 10000L;
        long started = System.currentTimeMillis();
        long now = started;
        while (queues == null && now - started < timeout) {
            try {
                wait(timeout - (now - started));
            } catch (InterruptedException e) {
                // Ignore
            }
            now = System.currentTimeMillis();
        }
        if (queues == null) {
            throw new ConnectException("Queues could not be updated quickly enough.");
        }
        /*if (!duplicates.isEmpty()) {
            throw new ConnectException("Duplicate queue names" + duplicates.values());
        }*/
        return queues;
    }

    public void shutdown() {
        log.info("Shutting down thread monitoring queues.");
        shutdownLatch.countDown();
    }

    private synchronized boolean updateQueues() {
        log.info("Starting to update queues...");
        final List<String> queues;
        try {
            queues = provider.queueIds(clientsessionProvider.getClientSession());
            log.info("Got the following queues: {}", queues);
        } catch (Exception e) {
            log.error("Error while trying to get updated queues list, ignoring and waiting for next queue poll" + " interval", e);
            clientsessionProvider.close();
            return false;
        }

        if (!queues.equals(this.queues)) {
            final List<String> previousQueues = this.queues;
            this.queues = queues;
            notifyAll();
            // Only return true if the table list wasn't previously null, i.e. if this was not the first table lookup
            return previousQueues != null;
        }

        return false;
    }
}