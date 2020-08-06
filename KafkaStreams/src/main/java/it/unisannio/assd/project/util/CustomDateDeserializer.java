package it.unisannio.assd.project.util;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CustomDateDeserializer extends StdDeserializer<Date> {

	private static final Logger log = LoggerFactory.getLogger(CustomDateDeserializer.class);
	
	private static final long serialVersionUID = 1L;
	
//	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
 
    public CustomDateDeserializer() {
        this(null);
    }
 
    public CustomDateDeserializer(Class<Date> vc) {
        super(vc);
    }

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		long dateInLong = Long.parseLong(p.getValueAsString());
		log.info(p.getValueAsString());
		log.info("### DEBUG: Date in long:" + dateInLong);
//		return new Date(dateInLong);
		long a = 1596731478708L;
		return new Date(a);
	}
    
}
