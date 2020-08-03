package it.unisannio.assd.project.util;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import it.unisannio.assd.project.domain.ImplicitCrowdSensingMessage;

public class ICSMessageProcessorSupplier implements ProcessorSupplier<String, String> {

	public class ICSMessageProcessor implements Processor<String, String> {
		
		private ProcessorContext context;
		
		@Override
		public void init(ProcessorContext context) {
			this.context = context;
		}

		@Override
		public void process(String key, String value) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				ImplicitCrowdSensingMessage message = objectMapper.readValue(value, ImplicitCrowdSensingMessage.class);
				message.calculateProximityIndex();
				FilterProvider filters = new SimpleFilterProvider().addFilter(
			        "parametersFilter", 
			        SimpleBeanPropertyFilter.serializeAllExcept("rssi", "txPower"));
			    String result = new ObjectMapper().writer(filters).writeValueAsString(message);
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
		return new ICSMessageProcessor();
	}

}
