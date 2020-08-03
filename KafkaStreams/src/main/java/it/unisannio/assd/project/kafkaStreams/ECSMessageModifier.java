package it.unisannio.assd.project.kafkaStreams;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import it.unisannio.assd.project.util.ECSMessageProcessorSupplier;

public class ECSMessageModifier extends JSONmessageModifier {

	public static void main(String[] args) {
		bootstrapServers = args.length > 0 ? args[0] : bootstrapServers;
		inputTopic = args.length > 1 ? args[1] : inputTopic;
		outputTopic = args.length > 2 ? args[2] : outputTopic;
		System.out.println(args[0] + " --- " + args[1] + " --- " + args[2]);
//		TODO: ECS suffix is for testing purpose
		final Topology topologyECS = getTopology()
				.addSource("sourceProcessorECS", inputTopic)
				.addProcessor("processorECS", new ECSMessageProcessorSupplier(), "sourceProcessorECS")
				.addSink("sinkProcessorECS", outputTopic, "processorECS");
		KafkaStreams streamsECS = startApp(topologyECS, config);
		Runtime.getRuntime().addShutdownHook(new Thread(streamsECS::close));
	}

}
