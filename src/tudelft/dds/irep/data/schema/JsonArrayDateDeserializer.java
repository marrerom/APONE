package tudelft.dds.irep.data.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonArrayDateDeserializer extends JsonDeserializer<Date[]> {
	
    @Override
    public Date[] deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException  {
    	if (jp.getCurrentToken().equals(JsonToken.START_ARRAY)) {
			List<Date> result = new ArrayList<Date>();
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				result.add((new JsonDateDeserializer()).deserialize(jp, ctxt));
			}
			Date[] resdate = new Date[result.size()];
			resdate = result.toArray(resdate);
			return resdate;
    	}
        return null;
    }

}
