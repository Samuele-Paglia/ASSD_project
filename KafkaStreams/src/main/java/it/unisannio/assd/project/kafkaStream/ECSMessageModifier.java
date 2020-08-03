package it.unisannio.assd.project.kafkaStream;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import it.unisannio.assd.project.util.ECSMessageProcessorSupplier;

public class ECSMessageModifier extends JSONmessageModifier {

	public static void main(String[] args) {
		bootstrapServers = args.length > 0 ? args[0] : bootstrapServers;
		inputTopic = args.length > 1 ? args[1] : inputTopic;
		outputTopic = args.length > 2 ? args[2] : outputTopic;
//		TODO: ECS suffix is for testing purpose
		final Topology topologyECS = getTopology()
				.addSource("sourceProcessorECS", inputTopic+"ECS")
				.addProcessor("processorECS", new ECSMessageProcessorSupplier(), "sourceProcessorECS")
				.addSink("sinkProcessorECS", outputTopic+"ECS", "processorECS");
		KafkaStreams streamsECS = startApp(topologyECS, config);
		Runtime.getRuntime().addShutdownHook(new Thread(streamsECS::close));
	}

}
