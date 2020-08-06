package it.unisannio.assd.project.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CustomDateSerializer extends StdSerializer<Long> {
	
	private static final Logger log = LoggerFactory.getLogger(CustomDateSerializer.class);

	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
 
    public CustomDateSerializer() {
        this(null);
    }
 
    public CustomDateSerializer(Class<Long> vc) {
        super(vc);
    }
    
    @Override
    public void serialize (Long value, JsonGenerator gen, SerializerProvider arg2)
      throws IOException, JsonProcessingException {
    	log.info("###### Debug: " + formatter.format(new Date(value)));
    	gen.writeString(formatter.format(new Date(value)));
    }
    
}
