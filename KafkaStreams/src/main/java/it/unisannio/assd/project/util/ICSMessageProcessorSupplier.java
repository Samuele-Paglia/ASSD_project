package it.unisannio.assd.project.util;

import java.text.SimpleDateFormat;

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
		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		
		@Override
		public void init(ProcessorContext context) {
			this.context = context;
		}
		
		private double calculateProximityIndex(int rssi, int txPower) {
			return rssi * txPower;
		}
		
		@Override
		public void process(String key, String value) {
			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode jsonNode = null;
//			try {
//				jsonNode = objectMapper.readTree(value);
//			} catch (JsonProcessingException e) {
//				e.printStackTrace();
//			}
//			int rssi = jsonNode.get("rssi").asInt();
//			int txPower = jsonNode.get("txPower").asInt();
//			ArrayList<String> fieldsToRemove = new ArrayList<String>();
//			fieldsToRemove.add("rssi");
//			fieldsToRemove.add("txPower");
//			String date = dateFormatter.format(new Date(jsonNode.get("timestamp").asLong()));
//			((ObjectNode) jsonNode).put("proximityIndex", calculateProximityIndex(rssi, txPower))
//										.put("timestamp", date)
//										.remove(fieldsToRemove);
//			context.forward(key, jsonNode.toString());
			try {
				ImplicitCrowdSensingMessage message = objectMapper.readValue(value, ImplicitCrowdSensingMessage.class);
//				objectMapper.addMixIn(POJO.class, ImplicitCrowdSensingMessage.class);
				FilterProvider filters = new SimpleFilterProvider().addFilter(
			        "parametersFilterICS", 
			        SimpleBeanPropertyFilter.serializeAllExcept("rssi", "txPower"));
				ObjectMapper anotherObjectMapper = new ObjectMapper();
				String result = anotherObjectMapper.writer(filters).withAttribute("proximityIndex", "3").writeValueAsString(message);
			    System.out.println(result);
//				String result = new ObjectMapper().writer(filters).writeValueAsString(message);
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
