package it.unisannio.assd.project.kafkaStreams;

import java.util.regex.Pattern;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.ProcessorSupplier;

import it.unisannio.assd.project.util.ECSMessageProcessorSupplier;
import it.unisannio.assd.project.util.ICSMessageProcessorSupplier;

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
		ProcessorSupplier<String, String> ps = null;
		if (inputTopic.contains("ics"))
			ps = new ICSMessageProcessorSupplier();
		else if (inputTopic.contains("ecs"))
			ps = new ECSMessageProcessorSupplier();
				
		final Topology topology = getTopology()
				.addSource(sourceName, topicPattern)
				.addProcessor(processorName, ps, sourceName)
				.addSink(sinkName, outputTopic, processorName);
		KafkaStreams streams = startApp(topology, getConfig());
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}

}
