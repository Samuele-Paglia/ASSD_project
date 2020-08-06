package it.unisannio.assd.project.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ECSMessageProcessorSupplier implements ProcessorSupplier<String, String> {

	public class ECSMessageProcessor implements Processor<String, String> {

		private ProcessorContext context;

		@Override
		public void init(ProcessorContext context) {
			this.context = context;
		}

		@Override
		public void process(String key, String value) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = null;
			try {
				jsonNode = objectMapper.readTree(value);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			String locationAttribute = "{ type : 'Point',"
										+ "coordinates : [ "
											+ jsonNode.get("longitude") + " , "
											+ jsonNode.get("latitude") + "] }";
			ArrayList<String> fieldsToRemove = new ArrayList<String>();
			fieldsToRemove.add("longitude");
			fieldsToRemove.add("latitude");
			String date = new SimpleDateFormat("yyyy-mm-dd").format(new Date(jsonNode.get("timestamp").asLong()));
			((ObjectNode) jsonNode).put("location", locationAttribute)
										.put("timestamp", date)
										.remove(fieldsToRemove);
			context.forward(key, jsonNode.toString());
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
