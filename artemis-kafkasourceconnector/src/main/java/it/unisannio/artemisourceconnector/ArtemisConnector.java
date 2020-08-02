package it.unisannio.artemisourceconnector;

import it.unisannio.util.ArtemisProvider;
import it.unisannio.util.CachedClientSessionProvider;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ArtemisConnector extends SourceConnector {
	private static Logger log = LoggerFactory.getLogger(ArtemisConnector.class);
	private static final long MAX_TIMEOUT = 10000L;
	private ArtemisConnectorConfig config;
	private Map<String, String> configProperties;
	private CachedClientSessionProvider cachedConnectionProvider;
	private ArtemisProvider provider;
	private QueueMonitorThread queueMonitorThread;

	@Override
	public String version() {
		return VersionUtil.getVersion();
	}

	@Override
	public void start(Map<String, String> properties) {
		 log.info("Starting Artemis Source Connector");
		 try {
			 configProperties = properties;
			 config = new ArtemisConnectorConfig(configProperties);
		 } catch (ConfigException e) {
			 throw new ConnectException("Couldn't start ArtemisConnector due to configuration error",e);
	     }

	     final int maxConnectionAttempts = config.getInt(ArtemisConnectorConfig.CONNECTION_ATTEMPTS_CONFIG);
	     final long connectionRetryBackoff = config.getLong(ArtemisConnectorConfig.CONNECTION_BACKOFF_CONFIG);
		try {
			provider = new ArtemisProvider(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cachedConnectionProvider = clientSessionProvider(maxConnectionAttempts, connectionRetryBackoff);

	     cachedConnectionProvider.getClientSession();

	     long queuePollMs = config.getLong(ArtemisConnectorConfig.QUEUE_POLL_INTERVAL_MS_CONFIG);

		 queueMonitorThread = new QueueMonitorThread(provider, cachedConnectionProvider, context, queuePollMs);
		 queueMonitorThread.start();
	}

	protected CachedClientSessionProvider clientSessionProvider(int maxConnAttempts, long retryBackoff) {
		return new CachedClientSessionProvider(provider, maxConnAttempts, retryBackoff);
	}

	@Override
	public Class<? extends Task> taskClass() {
		return ArtemisTask.class;
	}

  	@Override
  	public List<Map<String, String>> taskConfigs(int maxTasks) {
		List<Map<String, String>> taskConfigs;
		List<String> currentQueues = queueMonitorThread.queues();
		if (currentQueues.isEmpty()) {
			taskConfigs = Collections.emptyList();
			log.warn("No tasks will be run because no queues were found");
		} else {
			int numGroups = Math.min(currentQueues.size(), maxTasks);
			List<List<String>> tablesGrouped = ConnectorUtils.groupPartitions(currentQueues, numGroups);
			taskConfigs = new ArrayList<>(tablesGrouped.size());
			for (List<String> taskTables : tablesGrouped) {
				Map<String, String> taskProps = new HashMap<>(configProperties);
				StringBuilder builder = new StringBuilder();
				for(int i = 0; i < taskTables.size(); i++){
						builder.append(taskTables.get(i));
						if(i != taskTables.size()-1)
							builder.append(",");
				}
				taskProps.put(ArtemisTaskConfig.QUEUE_CONFIG, builder.toString());
				taskConfigs.add(taskProps);
			}
		}
		return taskConfigs;
	}

  	@Override
  	public void stop() {
		log.info("Stopping queue monitoring thread");
		queueMonitorThread.shutdown();
		try {
			queueMonitorThread.join(MAX_TIMEOUT);
		} catch (InterruptedException e) {
			// Ignore, shouldn't be interrupted
		} finally {
			try {
				cachedConnectionProvider.close();
			} finally {
				try {
					if (provider != null) {
						provider.close();
					}
				} catch (Throwable t) {
					log.warn("Error while closing the {} provider: ", provider, t);
				} finally {
					provider = null;
				}
			}
		}
	}

  	@Override
  	public ConfigDef config() {
    	return ArtemisConnectorConfig.CONFIG_DEF;
 	}
}
