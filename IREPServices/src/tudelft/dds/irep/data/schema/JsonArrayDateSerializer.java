package tudelft.dds.irep.data.schema;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonArrayDateSerializer extends JsonSerializer<Date[]> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(JsonDateSerializer.timestampFormat);

	@Override
	public void serialize(Date[] datearray, JsonGenerator gen, SerializerProvider provider)	throws IOException, JsonProcessingException {
		gen.writeStartArray();
        for (Date date : datearray) {
        	(new JsonDateSerializer()).serialize(date, gen, provider);
		 }
		 gen.writeEndArray();
	}

}


