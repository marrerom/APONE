package tudelft.dds.irep.services;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.glassdoor.planout4j.Namespace;
import com.glassdoor.planout4j.NamespaceConfig;
import com.google.common.base.*;
import com.google.common.collect.ImmutableMap;

import tudelft.dds.irep.utils.RunningExperiments;

@Path("/run")
public class Run {
	
	@Context ServletContext context;
	
	@Path("/getParams")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public String getParams(@FormParam("idrun") String idrun, @FormParam("idunit") String idunit){
		try {
		RunningExperiments re = (RunningExperiments)context.getAttribute("RunningExperiments");
		Preconditions.checkArgument(re.exist(idrun));
		NamespaceConfig nsConfig = re.getConf(idrun);
		String unitName = nsConfig.unit;
		Namespace ns = new Namespace(nsConfig, ImmutableMap.of(unitName, idunit), null);
		return ns.getParams().toString();
		//TODO: query params to override
		//TODO: return json using JAXB
		} catch (Exception e) {
			e.printStackTrace();
			throw new javax.ws.rs.InternalServerErrorException(e);
		}
	}

}
