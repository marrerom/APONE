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
import tudelft.dds.irep.utils.PausedExperiments;
import tudelft.dds.irep.utils.RunningExperiments;

@Path("/configuration")
public class Configuration {
	
	@Context ServletContext context;
	
	private NamespaceConfig createNamespace(JExperiment exp, JConfiguration config) {
		
	}
	
	
	private void run(JConfiguration conf, NamespaceConfig ns) {
		Database db = (Database) context.getAttribute("DBManager");
		RunningExperiments re = ((RunningExperiments) context.getAttribute("RunningExperiments"));
		Status st = re.getStatus(conf.get_Id());
		if (st != Status.ON) {
			db.addExpConfigDateStart(conf);
		}
		re.setExperiment(conf.get_Id(), ns, Status.ON);
		db.setExpConfigRunStatus(conf, Status.ON);
	}
	
	private void stop(JConfiguration conf, NamespaceConfig ns) {
		Database db = (Database) context.getAttribute("DBManager");
		RunningExperiments re = ((RunningExperiments) context.getAttribute("RunningExperiments"));
		if (re.getStatus(conf.get_Id()) == Status.ON) {
			db.addExpConfigDateEnd(conf);
		}
		re.setExperiment(conf.get_Id(), ns, Status.OFF); 
		db.setExpConfigRunStatus(conf, Status.OFF);
	}

	private void pause(JConfiguration conf, NamespaceConfig ns) {
		Database db = (Database) context.getAttribute("DBManager");
		RunningExperiments re = ((RunningExperiments) context.getAttribute("RunningExperiments"));
		if (re.getStatus(conf.get_Id()) == Status.ON) {
			db.addExpConfigDateEnd(conf);
		}
		re.setExperiment(conf.get_Id(), ns, Status.PAUSED);
		db.setExpConfigRunStatus(conf, Status.PAUSED);
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
