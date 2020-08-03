package it.unisannio.assd.project.kafkaStream;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import it.unisannio.assd.project.util.ICSMessageProcessorSupplier;

public class ICSMessageModifier extends JSONmessageModifier{

	public static void main(String[] args) {
		bootstrapServers = args.length > 0 ? args[0] : bootstrapServers;
		inputTopic = args.length > 1 ? args[1] : inputTopic;
		outputTopic = args.length > 2 ? args[2] : outputTopic;
		final Topology topologyICS = getTopology()
				.addSource("sourceProcessor", inputTopic)
				.addProcessor("processor", new ICSMessageProcessorSupplier(), "sourceProcessor")
				.addSink("sinkProcessor", outputTopic, "processor");
		KafkaStreams streamsICS = startApp(topologyICS, config);
		Runtime.getRuntime().addShutdownHook(new Thread(streamsICS::close));
	}

}
