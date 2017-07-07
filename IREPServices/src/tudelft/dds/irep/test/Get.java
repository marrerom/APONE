package tudelft.dds.irep.test;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.glassdoor.planout4j.Namespace;
import com.glassdoor.planout4j.NamespaceConfig;
import com.google.common.collect.ImmutableMap;

import javax.ws.rs.GET;

@Path("get")
public class Get {
	
	@GET
	public String get(){
		return "Hello World";
	}
	
	
//	@GET
//	public String getParam(@QueryParam("param") String param, @QueryParam("unitid") String unitid, @QueryParam("confhash") Integer confhash){
//		try{
//		com.google.common.base.Preconditions.checkArgument(ConfigRepository.exist(confhash), "No conf running object found");
//		NamespaceConfig nsConf = ConfigRepository.getConf(confhash);
//		Namespace ns = new Namespace(nsConf, ImmutableMap.of("user_guid", unitid), null);
//		String paramValue = ns.getParam(param, null);
//		com.google.common.base.Preconditions.checkNotNull(paramValue, "Param "+param+" does not exist");
//		return paramValue; 
//		} catch (Exception e){
//			throw new javax.ws.rs.BadRequestException(e);
//		}
//	}
	

}
