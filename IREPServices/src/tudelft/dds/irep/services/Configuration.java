package tudelft.dds.irep.services;

import java.io.StringReader;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.compiler.YAMLConfigParser;
import com.glassdoor.planout4j.config.ValidationException;

import jersey.repackaged.com.google.common.collect.Maps;
import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.utils.RunningExperiments;

@Path("/configuration")
public class Configuration {
	
	@Context ServletContext context;
	
	@Path("/start")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public String startExperiment(@FormParam("idexp") String idexp){
		try {
			String yaml = ((Database) context.getAttribute("DBManager")).GetYAML(idexp);
			NamespaceConfig nsConf = new YAMLConfigParser().parseAndValidate(new StringReader(yaml), String.valueOf(yaml.hashCode()));
			String idrun = String.valueOf(nsConf.hashCode());
			((RunningExperiments) context.getAttribute("RunningExperiments")).put(Maps.immutableEntry(idrun, nsConf));
			//TODO: check if idexp exist in database, save data
			
			return idrun;
		} catch (ValidationException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}

		
		
	}

}
