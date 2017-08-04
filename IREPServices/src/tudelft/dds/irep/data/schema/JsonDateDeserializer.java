package tudelft.dds.irep.data.schema;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import tudelft.dds.irep.utils.Utils;

public class JsonDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException  {
        if (jp.getCurrentToken().equals(JsonToken.VALUE_STRING))
			try {
				return Utils.getDate(jp.getText().toString());
			} catch (ParseException e) {
				e.printStackTrace();
				throw new IOException(e);
			}
        return null;
    }

}
