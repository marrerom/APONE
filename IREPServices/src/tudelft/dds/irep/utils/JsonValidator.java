package tudelft.dds.irep.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNodeReader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import tudelft.dds.irep.data.schema.Common;

public class JsonValidator {
	
	private Map<Class<? extends Common>, JsonSchema> schemas = new HashMap<Class<? extends Common>, JsonSchema>();
	
	private JsonSchema getSchema(ServletContext sc, Common obj) throws FileNotFoundException, IOException, ProcessingException {
		InputStream schemaStream = sc.getResourceAsStream(obj.getSchemaPath());
		JsonNode schema = new JsonNodeReader().fromInputStream(schemaStream);
		return JsonSchemaFactory.byDefault().getJsonSchema(schema);
	}
	
	public ProcessingReport validate(Common obj, JsonNode jnode, ServletContext sc) throws FileNotFoundException, IOException, ProcessingException {
		if (!schemas.containsKey(this.getClass())){
			schemas.put(obj.getClass(), getSchema(sc, obj));
		}
		return schemas.get(obj.getClass()).validate(jnode);
	}
	


}
