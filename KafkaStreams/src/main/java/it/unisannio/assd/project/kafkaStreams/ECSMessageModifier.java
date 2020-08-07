package it.unisannio.assd.project.kafkaStreams;

import java.util.regex.Pattern;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import it.unisannio.assd.project.util.ECSMessageProcessorSupplier;

public class ECSMessageModifier extends JSONmessageModifier {

	public static void main(String[] args) {
		bootstrapServers = args.length > 0 ? args[0] : bootstrapServers;
		inputTopic = args.length > 1 ? args[1] : inputTopic;
		outputTopic = args.length > 2 ? args[2] : outputTopic;
		System.out.println(args[0] + " --- " + args[1] + " --- " + args[2]);
		Pattern topicPattern = Pattern.compile(".*" + inputTopic);
		final Topology topologyECS = getTopology()
				.addSource("sourceProcessorECS", topicPattern)
				.addProcessor("processorECS", new ECSMessageProcessorSupplier(), "sourceProcessorECS")
				.addSink("sinkProcessorECS", outputTopic, "processorECS");
		KafkaStreams streamsECS = startApp(topologyECS, config);
		Runtime.getRuntime().addShutdownHook(new Thread(streamsECS::close));
	}

}
