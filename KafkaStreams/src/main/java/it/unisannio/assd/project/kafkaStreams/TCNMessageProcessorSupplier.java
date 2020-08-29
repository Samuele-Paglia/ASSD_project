package it.unisannio.assd.project.kafkaStreams;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unisannio.assd.project.domain.TCNReport;

public class TCNMessageProcessorSupplier implements ProcessorSupplier<String, String> {
	
public class TCNMessageProcessor implements Processor<String, String> {
		
		private ProcessorContext context;
		
		@Override
		public void init(ProcessorContext context) {
			this.context = context;
		}

		@Override
		public void process(String key, String value) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				String hexString = objectMapper.readTree(value).get("payload").asText();
				TCNReport report = new TCNReport(hexString);
				String result = objectMapper.writeValueAsString(report);
				// TODO: Delete
			    System.out.println(result);
			    context.forward(key, result);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public Processor<String, String> get() {
		return new TCNMessageProcessor();
	}

}
