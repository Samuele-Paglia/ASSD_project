package it.unisannio.assd.project.util;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import it.unisannio.assd.project.domain.ImplicitCrowdSensingMessage;

public class MessageProcessorSupplier implements ProcessorSupplier<String, String> {

	public class ICSMessageProcessor implements Processor<String, String> {
		
		private ProcessorContext context;
		
		@Override
		public void init(ProcessorContext context) {
			this.context = context;
		}

		@Override
		public void process(String key, String value) {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectWriter w = objectMapper.writer();
			
			try {
				ImplicitCrowdSensingMessage message = objectMapper.readValue(value, ImplicitCrowdSensingMessage.class);
				System.out.println(message.getProximityIndex());
				message.calculateProximityIndex();
				System.out.println(message.getProximityIndex());
				FilterProvider filters 
			      = new SimpleFilterProvider().addFilter(
			        "myFilter", 
			        SimpleBeanPropertyFilter.serializeAllExcept("rssi", "txPower"));
			 
			    String result = new ObjectMapper()
			      .writer(filters)
			      .writeValueAsString(message);
			    System.out.println(result);
				context.forward(key, result);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
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
