package it.unisannio.assd.project.util;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import it.unisannio.assd.project.domain.ExplicitCrowdSensingMessage;

public class ECSMessageProcessorSupplier implements ProcessorSupplier<String, String> {

	private static final Logger log = LoggerFactory.getLogger(ECSMessageProcessorSupplier.class);
	
	public class ECSMessageProcessor implements Processor<String, String> {

		private ProcessorContext context;

		@Override
		public void init(ProcessorContext context) {
			this.context = context;
		}

		@Override
		public void process(String key, String value) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				ExplicitCrowdSensingMessage message = objectMapper.readValue(value, ExplicitCrowdSensingMessage.class);
				log.info(message.toString());
				FilterProvider filters = new SimpleFilterProvider().addFilter(
						"parametersFilterECS", 
						SimpleBeanPropertyFilter.serializeAllExcept("longitude", "latitude"));
				String location = "{ type : 'Point',"
						+ "coordinates : [ "
						+ message.getLongitude() + " , "
						+ message.getLatitude() + "] }";
				String result = objectMapper.writer(filters).withAttribute("location", location).writeValueAsString(message);
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
		return new ECSMessageProcessor();
	}

}
