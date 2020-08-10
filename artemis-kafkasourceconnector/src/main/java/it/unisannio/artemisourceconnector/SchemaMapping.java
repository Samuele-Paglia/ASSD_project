/*
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package it.unisannio.artemisourceconnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import it.unisannio.domain.ExplicitCrowdSensingMessage;
import it.unisannio.domain.GenericMessage;
import it.unisannio.domain.ImplicitCrowdSensingMessage;
import it.unisannio.domain.Message;

/**
 * A mapping from a result set into a {@link Schema}. This mapping contains an array of {@link
 * FieldSetter} functions (one for each column in the result set), and the caller should iterate
 * over these and call the function with the result set.
 *
 * <p>This mapping contains the functions that should be called for each row
 * in the result set and these are exposed to users of this class via the {@link FieldSetter}
 * function.
 */
public final class SchemaMapping {
//  private static final Logger log = LoggerFactory.getLogger(SchemaMapping.class);
  /**
   * Convert the result set into a {@link Schema}.
   *
   * @param schemaName the name of the schema; may be null
   * @return the schema mapping; never null
   * @throws Exception if there is a problem accessing the result set metadata
   */
  public static SchemaMapping create(String schemaName) throws Exception {
    SchemaBuilder builder = SchemaBuilder.struct().name(schemaName);
    if (schemaName.contains("ics")) {
    	builder.field("uuidReceiver", Schema.STRING_SCHEMA);
    	builder.field("uuidSender", Schema.STRING_SCHEMA);
    	builder.field("rssi", Schema.INT32_SCHEMA);
    	builder.field("txPower", Schema.INT32_SCHEMA);
    	builder.field("timestamp", Schema.INT64_SCHEMA);
    } else if (schemaName.contains("ecs")) {
    	builder.field("id", Schema.STRING_SCHEMA);
    	builder.field("type", Schema.STRING_SCHEMA);
    	builder.field("longitude", Schema.FLOAT64_SCHEMA);
    	builder.field("latitude", Schema.FLOAT64_SCHEMA);
    	builder.field("timestamp", Schema.INT64_SCHEMA);
    } else
    	builder.field("payload", Schema.STRING_SCHEMA);
    Schema schema = builder.build();
    return new SchemaMapping(schema);
  }

  private final Schema schema;
  private final List<FieldSetter> fieldSetters;

  private SchemaMapping(Schema schema) {
    assert schema != null;
    this.schema = schema;
    List<FieldSetter> fieldSetters = new ArrayList<>();
    List<Field> fields = schema.fields();
    for (Field field : fields)
    	fieldSetters.add(new FieldSetter(field));
    this.fieldSetters = Collections.unmodifiableList(fieldSetters);
  }

  public Schema schema() {
    return schema;
  }

  /**
   * Get the {@link FieldSetter} functions, which contain one for each result set column whose
   * values are to be mapped/converted and then set on the corresponding {@link Field} in supplied
   * {@link Struct} objects.
   *
   * @return the array of {@link FieldSetter} instances; never null and never empty
   */
  List<FieldSetter> fieldSetters() {
    return fieldSetters;
  }

  @Override
  public String toString() {
    return "Mapping for " + schema.name();
  }

  public static final class FieldSetter {
    private final Field field;

    private FieldSetter(Field field) {
      this.field = field;
    }

    /**
     * Get the {@link Field} that this setter function sets.
     *
     * @return the field; never null
     */
    public Field field() {
      return field;
    }

    /**
     *
     * corresponding {@link #field() field} on the supplied {@link Struct}.
     *
     * @param struct    the struct whose field is to be set with the converted value from the result
     *                  set; may not be null
     * @param m the clientMessage to be processed; may not be null
     * @throws Exception if there is an error accessing the result set
     * @throws IOException  if there is an error accessing a streaming value from the result set
     */
    void setField(Struct struct, Message m) throws Exception, IOException {
    	if (m instanceof ImplicitCrowdSensingMessage) {
    		switch (field.name()) {
	    		case "uuidReceiver":
	    			struct.put(field, ((ImplicitCrowdSensingMessage) m).getUuidReceiver());
	    	        break;
	    		case "uuidSender":
	    			struct.put(field, ((ImplicitCrowdSensingMessage) m).getUuidSender());
	    			break;
	    		case "rssi":
	    			struct.put(field, ((ImplicitCrowdSensingMessage) m).getRssi());
	    			break;
	    		case "txPower":
	    			struct.put(field, ((ImplicitCrowdSensingMessage) m).getTxPower());
	    			break;
	    		case "timestamp":
	    			struct.put(field, ((ImplicitCrowdSensingMessage) m).getTimestamp());
	    			break;
    		}
    	} else if (m instanceof ExplicitCrowdSensingMessage) {
    		switch (field.name()) {
	    		case "id":
	    			struct.put(field, ((ExplicitCrowdSensingMessage) m).getId());
	    			break;
	    		case "type":
	    			struct.put(field, ((ExplicitCrowdSensingMessage) m).getType());
	    			break;
	    		case "longitude":
	    			struct.put(field, ((ExplicitCrowdSensingMessage) m).getLongitude());
	    			break;
	    		case "latitude":
	    			struct.put(field, ((ExplicitCrowdSensingMessage) m).getLatitude());
	    			break;
	    		case "timestamp":
	    			struct.put(field, ((ExplicitCrowdSensingMessage) m).getTimestamp());
	    			break;
    		}
    	} else
    		struct.put(field.name(), ((GenericMessage) m).getPayload());
    }

    @Override
    public String toString() {
      return field.name();
    }
  }
}