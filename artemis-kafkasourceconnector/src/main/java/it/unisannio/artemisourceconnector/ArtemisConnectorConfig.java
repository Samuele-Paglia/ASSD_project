package it.unisannio.artemisourceconnector;

import it.unisannio.util.EnumRecommender;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Width;
import org.apache.kafka.common.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ArtemisConnectorConfig extends AbstractConfig {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ArtemisConnectorConfig.class);

	public static final String CONNECTION_URL_CONFIG = "connection.url";
	private static final String CONNECTION_URL_DOC = "Artemis connection URL.\n For example: ``tcp://localhost:61616``";
	private static final String CONNECTION_URL_DISPLAY = "ARTEMIS URL";

	public static final String CONNECTION_USER_CONFIG = "connection.user";
	private static final String CONNECTION_USER_DOC = "ARTEMIS connection user.";
	private static final String CONNECTION_USER_DISPLAY = "ARTEMIS User";

	public static final String CONNECTION_PASSWORD_CONFIG = "connection.password";
	private static final String CONNECTION_PASSWORD_DOC = "ARTEMIS connection password.";
	private static final String CONNECTION_PASSWORD_DISPLAY = "ARTEMIS Password";

	public static final String CONNECTION_ATTEMPTS_CONFIG = "connection.attempts";
	private static final String CONNECTION_ATTEMPTS_DOC = "Maximum number of attempts to retrieve a valid ARTEMIS connection. "
     			+ "Must be a positive integer.";
	private static final String CONNECTION_ATTEMPTS_DISPLAY = "ARTEMIS connection attempts";
	public static final int CONNECTION_ATTEMPTS_DEFAULT = 3;

	public static final String CONNECTION_BACKOFF_CONFIG = "connection.backoff.ms";
	private static final String CONNECTION_BACKOFF_DOC
		= "Backoff time in milliseconds between connection attempts.";
	private static final String CONNECTION_BACKOFF_DISPLAY
		= "ARTEMIS connection backoff in milliseconds";
	public static final long CONNECTION_BACKOFF_DEFAULT = 10000L;

	public static final String POLL_INTERVAL_MS_CONFIG = "poll.interval.ms";
	private static final String POLL_INTERVAL_MS_DOC = "Frequency in ms to poll for new data in each queue .";
	public static final int POLL_INTERVAL_MS_DEFAULT = 5000;
	private static final String POLL_INTERVAL_MS_DISPLAY = "Poll Interval (ms)";

	public static final String BATCH_MAX_MSGS_CONFIG = "batch.max.msgs";
	private static final String BATCH_MAX_MSGS_DOC =
			"Maximum number of messages to include in a single batch when polling for new data. This "
					+ "setting can be used to limit the amount of data buffered internally in the connector.";
	public static final int BATCH_MAX_MSGS_DEFAULT = 100;
	private static final String BATCH_MAX_MSGS_DISPLAY = "Max Messages Per Batch";

	public static final String NUMERIC_PRECISION_MAPPING_CONFIG = "numeric.precision.mapping";
	private static final String NUMERIC_PRECISION_MAPPING_DOC =
			"Whether or not to attempt mapping NUMERIC values by precision to integral types. This "
					+ "option is now deprecated. A future version may remove it completely. Please use "
					+ "``numeric.mapping`` instead.";
	public static final boolean NUMERIC_PRECISION_MAPPING_DEFAULT = false;
	public static final String NUMERIC_MAPPING_CONFIG = "numeric.mapping";
	private static final String NUMERIC_PRECISION_MAPPING_DISPLAY = "Map Numeric Values By Precision (deprecated)";

	private static final String NUMERIC_MAPPING_DOC =
			"Map NUMERIC values by precision and optionally scale to integral or decimal types. Use "
					+ "``none`` if all NUMERIC columns are to be represented by Connect's DECIMAL logical "
					+ "type. Use ``best_fit`` if NUMERIC columns should be cast to Connect's INT8, INT16, "
					+ "INT32, INT64, or FLOAT64 based upon the column's precision and scale. Or use "
					+ "``precision_only`` to map NUMERIC columns based only on the column's precision "
					+ "assuming that column's scale is 0. The ``none`` option is the default, but may lead "
					+ "to serialization issues with Avro since Connect's DECIMAL type is mapped to its "
					+ "binary representation, and ``best_fit`` will often be preferred since it maps to the"
					+ " most appropriate primitive type.";
	public static final String NUMERIC_MAPPING_DEFAULT = null;
	private static final String NUMERIC_MAPPING_DISPLAY = "Map Numeric Values, Integral "
			+ "or Decimal, By Precision and Scale";

	private static final EnumRecommender NUMERIC_MAPPING_RECOMMENDER =
			EnumRecommender.in(NumericMapping.values());

	public static final String MODE_CONFIG = "mode"; //TODO
	private static final String MODE_DOC =
     "The mode for updating a queue each time it is polled. Options include:\n"
     + "  * bulk - perform a bulk load of the entire queue each time it is polled\n"
     + "  * incrementing - use a strictly incrementing column on each table to "
     + "detect only new rows. Note that this will not detect modifications or "
     + "deletions of existing rows.\n"
     + "  * timestamp - use a timestamp (or timestamp-like) column to detect new and modified "
     + "rows. This assumes the column is updated with each write, and that values are "
     + "monotonically incrementing, but not necessarily unique.\n"
     + "  * timestamp+incrementing - use two columns, a timestamp column that detects new and "
     + "modified rows and a strictly incrementing column which provides a globally unique ID for "
     + "updates so each row can be assigned a unique stream offset.";
	private static final String MODE_DISPLAY = "Queue Loading Mode";
	public static final String MODE_UNSPECIFIED = "";
	public static final String MODE_BULK = "bulk";
	public static final String MODE_DEFAULT = MODE_BULK;

	public static final String QUEUE_POLL_INTERVAL_MS_CONFIG = "queue.poll.interval.ms";
	private static final String QUEUE_POLL_INTERVAL_MS_DOC =
     "Frequency in ms to poll for new or removed queues, which may result in updated task "
     + "configurations to start polling for data in added tables or stop polling for data in "
     + "removed tables.";
	public static final long QUEUE_POLL_INTERVAL_MS_DEFAULT = 60 * 1000;
	private static final String QUEUE_POLL_INTERVAL_MS_DISPLAY
     = "Metadata Change Monitoring Interval (ms)";
	
	public static final String TOPIC_PREFIX_CONFIG = "topic.prefix";
	private static final String TOPIC_PREFIX_DOC =
     "Prefix to prepend to table names to generate the name of the Kafka topic to publish data "
     + "to, or in the case of a custom query, the full name of the topic to publish to.";
	private static final String TOPIC_PREFIX_DISPLAY = "Topic Prefix";

	public static final String TIMESTAMP_DELAY_INTERVAL_MS_CONFIG = "timestamp.delay.interval.ms";
	private static final String TIMESTAMP_DELAY_INTERVAL_MS_DOC =
     "How long to wait after a row with certain timestamp appears before we include it in the "
     + "result. You may choose to add some delay to allow transactions with earlier timestamp to"
     + " complete. The first execution will fetch all available records (i.e. starting at "
     + "timestamp 0) until current time minus the delay. Every following execution will get data"
     + " from the last time we fetched until current time minus the delay.";
	public static final long TIMESTAMP_DELAY_INTERVAL_MS_DEFAULT = 0;
	private static final String TIMESTAMP_DELAY_INTERVAL_MS_DISPLAY = "Delay Interval (ms)";

	public static final String ARTEMIS_GROUP = "Artemis";
	public static final String MODE_GROUP = "Mode";
	public static final String CONNECTOR_GROUP = "Connector";
  
	public static ConfigDef baseConfigDef() {
		ConfigDef config = new ConfigDef();
		addArtemisOptions(config);
		addModeOptions(config);
		addConnectorOptions(config);
		return config;
	}
 
	private static void addArtemisOptions(ConfigDef config) {
		int orderInGroup = 0;
		config.define(
			CONNECTION_URL_CONFIG,
			Type.STRING,
			Importance.HIGH,
			CONNECTION_URL_DOC,
			ARTEMIS_GROUP,
			++orderInGroup,
			Width.LONG,
			CONNECTION_URL_DISPLAY
		).define(
			CONNECTION_USER_CONFIG,
			Type.STRING,
			null,
			Importance.HIGH,
			CONNECTION_USER_DOC,
			ARTEMIS_GROUP,
			++orderInGroup,
			Width.LONG,
			CONNECTION_USER_DISPLAY
		).define(
			CONNECTION_PASSWORD_CONFIG,
			Type.PASSWORD,
			null,
			Importance.HIGH,
			CONNECTION_PASSWORD_DOC,
			ARTEMIS_GROUP,
			++orderInGroup,
			Width.SHORT,
			CONNECTION_PASSWORD_DISPLAY
		).define(
			CONNECTION_ATTEMPTS_CONFIG,
			Type.INT,
			CONNECTION_ATTEMPTS_DEFAULT,
			ConfigDef.Range.atLeast(1),
			Importance.LOW,
			CONNECTION_ATTEMPTS_DOC,
			ARTEMIS_GROUP,
			++orderInGroup,
			Width.SHORT,
			CONNECTION_ATTEMPTS_DISPLAY
		).define(
			CONNECTION_BACKOFF_CONFIG,
			Type.LONG,
			CONNECTION_BACKOFF_DEFAULT,
			Importance.LOW,
			CONNECTION_BACKOFF_DOC,
			ARTEMIS_GROUP,
			++orderInGroup,
			Width.SHORT,
			CONNECTION_BACKOFF_DISPLAY
		).define(
			NUMERIC_PRECISION_MAPPING_CONFIG,
			Type.BOOLEAN,
			NUMERIC_PRECISION_MAPPING_DEFAULT,
			Importance.LOW,
			NUMERIC_PRECISION_MAPPING_DOC,
			ARTEMIS_GROUP,
			++orderInGroup,
			Width.SHORT,
			NUMERIC_PRECISION_MAPPING_DISPLAY
		).define(
			NUMERIC_MAPPING_CONFIG,
			Type.STRING,
			NUMERIC_MAPPING_DEFAULT,
			NUMERIC_MAPPING_RECOMMENDER,
			Importance.LOW,
			NUMERIC_MAPPING_DOC,
			ARTEMIS_GROUP,
			++orderInGroup,
			Width.SHORT,
			NUMERIC_MAPPING_DISPLAY,
			NUMERIC_MAPPING_RECOMMENDER
		);
	}

	private static void addModeOptions(ConfigDef config) {
	    int orderInGroup = 0;
	    config.define(
	        MODE_CONFIG,
	        Type.STRING,
	        MODE_UNSPECIFIED,
	        ConfigDef.ValidString.in(
	            MODE_UNSPECIFIED,
	            MODE_BULK
	        ),
	        Importance.HIGH,
	        MODE_DOC,
	        MODE_GROUP,
	        ++orderInGroup,
	        Width.MEDIUM,
	        MODE_DISPLAY
	    );
  	}

  	private static void addConnectorOptions(ConfigDef config) {
		int orderInGroup = 0;
		config.define(
			POLL_INTERVAL_MS_CONFIG,
			Type.INT,
			POLL_INTERVAL_MS_DEFAULT,
			Importance.HIGH,
			POLL_INTERVAL_MS_DOC,
			CONNECTOR_GROUP,
			++orderInGroup,
			Width.SHORT,
			POLL_INTERVAL_MS_DISPLAY
		).define(
				BATCH_MAX_MSGS_CONFIG,
			Type.INT,
				BATCH_MAX_MSGS_DEFAULT,
			Importance.LOW,
				BATCH_MAX_MSGS_DOC,
			CONNECTOR_GROUP,
			++orderInGroup,
			Width.SHORT,
			BATCH_MAX_MSGS_DISPLAY
		).define(
			QUEUE_POLL_INTERVAL_MS_CONFIG,
			Type.LONG,
			QUEUE_POLL_INTERVAL_MS_DEFAULT,
			Importance.LOW,
			QUEUE_POLL_INTERVAL_MS_DOC,
			CONNECTOR_GROUP,
			++orderInGroup,
			Width.SHORT,
			QUEUE_POLL_INTERVAL_MS_DISPLAY
		).define(
			TOPIC_PREFIX_CONFIG,
			Type.STRING,
			Importance.HIGH,
			TOPIC_PREFIX_DOC,
			CONNECTOR_GROUP,
			++orderInGroup,
			Width.MEDIUM,
			TOPIC_PREFIX_DISPLAY
		).define(
			TIMESTAMP_DELAY_INTERVAL_MS_CONFIG,
			Type.LONG,
			TIMESTAMP_DELAY_INTERVAL_MS_DEFAULT,
			Importance.HIGH,
			TIMESTAMP_DELAY_INTERVAL_MS_DOC,
			CONNECTOR_GROUP,
			++orderInGroup,
			Width.MEDIUM,
			TIMESTAMP_DELAY_INTERVAL_MS_DISPLAY
		);
  	}

  	public static final ConfigDef CONFIG_DEF = baseConfigDef();
	  
 	public ArtemisConnectorConfig(Map<String, ?> props) {
		super(CONFIG_DEF, props);
	    String mode = getString(ArtemisConnectorConfig.MODE_CONFIG);
	    if (mode.equals(ArtemisConnectorConfig.MODE_UNSPECIFIED)) {
	      throw new ConfigException("Query mode must be specified");
	    }
  	}

  	public enum NumericMapping {
		NONE,
	    PRECISION_ONLY,
	    BEST_FIT;

	    private static final Map<String, NumericMapping> reverse = new HashMap<>(values().length);
	    static {
	      for (NumericMapping val : values()) {
	        reverse.put(val.name().toLowerCase(Locale.ROOT), val);
	      }
	    }

	    public static NumericMapping get(String prop) {
	      // not adding a check for null value because the recommender/validator should catch those.
	      return reverse.get(prop.toLowerCase(Locale.ROOT));
	    }

	    public static NumericMapping get(ArtemisConnectorConfig config) {
	      String newMappingConfig = config.getString(ArtemisConnectorConfig.NUMERIC_MAPPING_CONFIG);
	      // We use 'null' as default to be able to check the old config if the new one is unset.
	      if (newMappingConfig != null) {
	        return get(config.getString(ArtemisConnectorConfig.NUMERIC_MAPPING_CONFIG));
	      }
	      if (config.getBoolean(ArtemisTaskConfig.NUMERIC_PRECISION_MAPPING_CONFIG)) {
	        return NumericMapping.PRECISION_ONLY;
	      }
	      return NumericMapping.NONE;
	    }
  	}
	  
  	protected ArtemisConnectorConfig(ConfigDef subclassConfigDef, Map<String, String> props) {
  		super(subclassConfigDef, props);
  	}

  	public static void main(String[] args) {
		  System.out.println(CONFIG_DEF.toEnrichedRst());
	  }
}