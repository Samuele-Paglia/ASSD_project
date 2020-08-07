package it.unisannio.assd.project.kafkaStreams;

import java.util.regex.Pattern;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import it.unisannio.assd.project.util.ECSMessageProcessorSupplier;

public class MessageModifier extends JSONmessageModifier {

	public static void main(String[] args) {
		bootstrapServers = args.length > 0 ? args[0] : bootstrapServers;
		inputTopic = args.length > 1 ? args[1] : inputTopic;
		outputTopic = args.length > 2 ? args[2] : outputTopic;
		// TODO: Delete
		System.out.println(args[0] + " --- " + args[1] + " --- " + args[2]);
		Pattern topicPattern = Pattern.compile(".*" + inputTopic);
		String sourceName = "sourceProcessor_" + inputTopic;
		String processorName = "processor_" + inputTopic;
		String sinkName = "sinkProcessor_" + inputTopic;
		final Topology topologyECS = getTopology()
				.addSource(sourceName, topicPattern)
				.addProcessor(processorName, new ECSMessageProcessorSupplier(), sourceName)
				.addSink(sinkName, outputTopic, processorName);
		KafkaStreams streamsECS = startApp(topologyECS, getConfig());
		Runtime.getRuntime().addShutdownHook(new Thread(streamsECS::close));
	}

}
