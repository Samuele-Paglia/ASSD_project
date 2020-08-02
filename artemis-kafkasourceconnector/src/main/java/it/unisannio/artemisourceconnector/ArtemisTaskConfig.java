package it.unisannio.artemisourceconnector;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;

public class ArtemisTaskConfig extends ArtemisConnectorConfig {

	  public static final String QUEUE_CONFIG = "queues";
	  private static final String QUEUE_DOC = "List of queues for this task to watch for changes.";

	  static ConfigDef config = baseConfigDef().define(QUEUE_CONFIG, Type.LIST, Importance.HIGH, QUEUE_DOC);

	  public ArtemisTaskConfig(Map<String, String> props) {
	    super(config, props);
	  }
}