package it.unisannio.assd.project.kafkaStreams;

import java.util.regex.Pattern;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unisannio.assd.project.util.ECSMessageProcessorSupplier;
import it.unisannio.assd.project.util.ICSMessageProcessorSupplier;
import it.unisannio.assd.project.util.TopicCreator;

public class MessageModifier extends JSONmessageModifier {

	private static final Logger log = LoggerFactory.getLogger(MessageModifier.class);
	
	public static void main(String[] args) {
		bootstrapServers = args.length > 0 ? args[0] : bootstrapServers;
		inputTopic = args.length > 1 ? args[1] : inputTopic;
		outputTopic = args.length > 2 ? args[2] : outputTopic;
		int numPartitions = args.length > 3 ? Integer.parseInt(args[3]) : 3;
		short replicationFactor = args.length > 4 ? Short.parseShort(args[4]) : (short) 3;
		log.info("Creating topic {}", outputTopic);
		TopicCreator.setBootstrapServers(bootstrapServers);
		TopicCreator.createTopic(outputTopic, numPartitions, replicationFactor); 
		Pattern topicPattern = Pattern.compile(".*" + inputTopic);
		String sourceName = "sourceProcessor_" + inputTopic;
		String processorName = "processor_" + inputTopic;
		String sinkName = "sinkProcessor_" + inputTopic;
		ProcessorSupplier<String, String> ps = null;
		if (inputTopic.contains("ics"))
			ps = new ICSMessageProcessorSupplier();
		else if (inputTopic.contains("ecs"))
			ps = new ECSMessageProcessorSupplier();	
		final Topology topology = getTopology()
				.addSource(sourceName, topicPattern)
				.addProcessor(processorName, ps, sourceName)
				.addSink(sinkName, outputTopic, processorName);
		log.info("Starting Kafka Streams Application...\n"
				+ "\t- Bootstrap server: {}"
				+ "\t- Source topics: all topics ending with {}"
				+ "\t- Destination topic: {}",
				bootstrapServers, inputTopic, outputTopic);
		KafkaStreams streams = startApp(topology, getConfig());
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}

}
