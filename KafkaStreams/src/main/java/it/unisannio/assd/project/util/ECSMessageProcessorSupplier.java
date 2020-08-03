package it.unisannio.assd.project.util;

import java.util.ArrayList;

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
			try {
				JsonNode jsonNode = objectMapper.readTree(value);
				String type = "'Point'";
				String locationAttribute = "{ type : " + type + ", coordinates : [ "
						+ jsonNode.get("long") + " , " + jsonNode.get("lat") + "] }";
				ArrayList<String> fieldsToRemove = new ArrayList<String>();
				fieldsToRemove.add("long");
				fieldsToRemove.add("lat");
				((ObjectNode) jsonNode).put("location", locationAttribute).remove(fieldsToRemove);
				context.forward(key, jsonNode.toString());
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
