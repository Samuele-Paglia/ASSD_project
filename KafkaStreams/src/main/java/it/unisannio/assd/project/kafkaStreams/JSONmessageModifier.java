package it.unisannio.assd.project.kafkaStreams;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

public class JSONmessageModifier {
	
	static String bootstrapServers = "localhost:9092";
	static String inputTopic = "input-topic";
	static String outputTopic = "output-topic";
	static Properties config = getConfig();
	
	private static Properties getConfig(){
        Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "json-message-modifier-example");
		streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "json-message-modifier-example-client");
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		streamsConfiguration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
		streamsConfiguration.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
        return streamsConfiguration;        
    }
	
	public static Topology getTopology() {
		final StreamsBuilder builder = new StreamsBuilder();
		return builder.build();
	}
	
	public static KafkaStreams startApp(Topology topology, Properties config){
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
        return streams;
    }

}
