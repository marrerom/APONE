package tudelft.dds.irep.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNodeReader;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.cfg.ValidationConfigurationBuilder;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.library.DraftV4Library;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import tudelft.dds.irep.data.schema.JCommon;

public class JsonValidator {
	
	private Map<Class<? extends JCommon>, JsonSchema> schemas;
	
	public JsonValidator() {
		schemas = new HashMap<Class<? extends JCommon>, JsonSchema>();
	}
	
	private JsonSchema getSchema(ServletContext sc, JCommon obj) throws FileNotFoundException, IOException, ProcessingException {
		InputStream schemaStream = sc.getResourceAsStream(obj.getSchemaPath());
		JsonNode schema = new JsonNodeReader().fromInputStream(schemaStream);
		String rootElement = obj.getRootElement();
		if (rootElement != null)
			schema = schema.findValue(rootElement);
		
		return JsonSchemaFactory.byDefault().getJsonSchema(schema);
	}
	
	public ProcessingReport validate(JCommon obj, JsonNode jnode, ServletContext sc) throws FileNotFoundException, IOException, ProcessingException {
		synchronized(this) {
			if (!schemas.containsKey(obj.getClass())){
				schemas.put(obj.getClass(), getSchema(sc, obj));
			}
		}
		try {
		return schemas.get(obj.getClass()).validate(jnode);
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

}
