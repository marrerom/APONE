package tudelft.dds.irep.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.compiler.PlanoutDSLCompiler;
import com.glassdoor.planout4j.compiler.YAMLConfigParser;
import com.glassdoor.planout4j.config.ValidationException;
import com.google.common.base.Preconditions;

import jersey.repackaged.com.google.common.collect.Maps;
import tudelft.dds.irep.data.database.Database;
import tudelft.dds.irep.data.schema.JConfiguration;
import tudelft.dds.irep.data.schema.JDistribution;
import tudelft.dds.irep.data.schema.JExperiment;
import tudelft.dds.irep.data.schema.JTreatment;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.utils.JsonValidator;
import tudelft.dds.irep.utils.RunningExperiments;

@Path("/configuration")
public class Configuration {
	
	@Context ServletContext context;
	
	
	private void run(JConfiguration conf, NamespaceConfig ns) {
		Database db = (Database) context.getAttribute("DBManager");
		RunningExperiments re = ((RunningExperiments) context.getAttribute("RunningExperiments"));
		if (!re.exist(conf.get_Id())) {
			re.put(Maps.immutableEntry(conf.get_Id(), ns));
			db.addExpConfigDateStart(conf);
		}
		db.setExpConfigRunStatus(conf, Status.ON);
	}
	
	private void stop(JConfiguration conf, NamespaceConfig ns) {
		Database db = (Database) context.getAttribute("DBManager");
		if (((RunningExperiments) context.getAttribute("RunningExperiments")).remove(conf.get_Id()) != null) {
			db.addExpConfigDateEnd(conf);
		}
		db.setExpConfigRunStatus(conf, Status.OFF);
	}

	
	
	@Path("/start")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public String startExperiment(@FormParam("idexp") String idexp, @FormParam("configuration") InputStream configuration){
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jnode = mapper.readTree(configuration);
			JConfiguration conf = mapper.convertValue(jnode, JConfiguration.class);
			JsonValidator jval = (JsonValidator) context.getAttribute("JsonValidator");
			ProcessingReport pr = jval.validate(conf,jnode, context);
			Preconditions.checkArgument(pr.isSuccess(), pr.toString());
			JDistribution[] distribution = conf.getDistr();
			Database db = (Database) context.getAttribute("DBManager");
			String idrun = db.addExpConfig(idexp, conf);
			
			//TODO: create namespace
			String yaml = ((Database) context.getAttribute("DBManager")).GetYAML(idexp);
			NamespaceConfig ns = new YAMLConfigParser().parseAndValidate(new StringReader(yaml), String.valueOf(yaml.hashCode()));
			
			run(conf, ns);
			return idrun;
		} catch (ValidationException | IOException e) {
			e.printStackTrace();
			throw new javax.ws.rs.BadRequestException(e);
		}

		
		
	}

}
