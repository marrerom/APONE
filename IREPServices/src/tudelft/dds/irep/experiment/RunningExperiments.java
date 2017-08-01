package tudelft.dds.irep.experiment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.glassdoor.planout4j.NamespaceConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.messaging.EventMonitoringConsumer;
import tudelft.dds.irep.messaging.EventRegisterConsumer;

class ExpInfo {
	public NamespaceConfig conf;
	public Status status;
	public EventRegisterConsumer regConsumer;
	public EventMonitoringConsumer monConsumer;
	
	public ExpInfo(NamespaceConfig conf, Status status, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer) {
		this.conf = conf;
		this.status = status;
		this.regConsumer = regConsumer;
		this.monConsumer = monConsumer;
	}
}

public class RunningExperiments {
	
	private Map<String, ExpInfo> idconfConfig;
	

	public RunningExperiments() {
		idconfConfig = new HashMap<String, ExpInfo>();
	}
	
	public Status getStatus(String idconf) {
		ExpInfo ei = getExpInfo(idconf);
		if (ei != null) {
			return ei.status;
		}
		return Status.OFF;
	}
	
	public void setExperiment(String idconf, NamespaceConfig conf, Status status, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer) throws IOException, TimeoutException {
		if (status == Status.OFF)
			remove(idconf);
		else {
			ExpInfo exp = getExpInfo(idconf);
			if (status == Status.PAUSED) {
				put(idconf, conf, status, exp.regConsumer, exp.monConsumer);
			} else if (status == Status.ON){
				put(idconf, conf, status, regConsumer, monConsumer);
			}
		}
	}
	
	public NamespaceConfig getNsConfig(String idconf) {
		ExpInfo ei = getExpInfo(idconf);
		if (ei != null)
			return ei.conf;
		return null;
	}
	
	public EventMonitoringConsumer getEventMonitoringConsumer(String idconf) {
		ExpInfo ei = getExpInfo(idconf);
		if (ei != null)
			return ei.monConsumer;
		return null;
	}
	
	
	public EventRegisterConsumer getEventRegisterConsumer(String idconf) {
		ExpInfo ei = getExpInfo(idconf);
		if (ei != null)
			return ei.regConsumer;
		return null;
	}


	private ExpInfo getExpInfo(String idconf){
		return idconfConfig.get(idconf);
	}
	
	private ExpInfo put(String idconf, NamespaceConfig conf, Status status, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer){
		return idconfConfig.put(idconf,new ExpInfo(conf,status, regConsumer, monConsumer));
	}
	
	private ExpInfo remove(String idconf) throws IOException, TimeoutException {
		ExpInfo exp = getExpInfo(idconf);
		return idconfConfig.remove(idconf);
	}
	

	

}
