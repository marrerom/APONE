package tudelft.dds.irep.data.schema;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonDateSerializer extends JsonSerializer<Date> {

	//public static final String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	public static final String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(timestampFormat);

	@Override
	public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
		String formattedDate = dateFormat.format(date);
		gen.writeString(formattedDate);
	}

}
