package tudelft.dds.irep.experiment;

import java.util.Date;
import com.glassdoor.planout4j.NamespaceConfig;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.messaging.EventMonitoringConsumer;
import tudelft.dds.irep.messaging.EventRegisterConsumer;

public class RunningExpInfo {
		private String idconfig;
		private NamespaceConfig conf;
		private Status status;
		private EventRegisterConsumer regConsumer;
		private EventMonitoringConsumer monConsumer;
		private Date dateToEnd;
		private Integer maxExposures; //TODO: change name, it is max completed units instead of exposures
		private Date lastStarted;
		private String experimenter;
		

		public RunningExpInfo(String idconfig, NamespaceConfig conf, Status status, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer, Date dateToEnd, Integer maxExposures, Date lastStarted, String experimenter) {
			this.idconfig = idconfig;
			this.conf = conf;
			this.status = status;
			this.regConsumer = regConsumer;
			this.monConsumer = monConsumer;
			this.dateToEnd = dateToEnd;
			this.maxExposures = maxExposures;
			this.lastStarted = lastStarted;
			this.experimenter = experimenter;
		}
		
		public String getIdconfig() {
			return idconfig;
		}

		public NamespaceConfig getConf() {
			return conf;
		}

		public Status getStatus() {
			return status;
		}

		public EventRegisterConsumer getRegConsumer() {
			return regConsumer;
		}

		public EventMonitoringConsumer getMonConsumer() {
			return monConsumer;
		}

		public Date getDateToEnd() {
			return dateToEnd;
		}

		public Integer getMaxExposures() {
			return maxExposures;
		}
	
		public Date getLastStarted() {
			return lastStarted;
		}

		public String getExperimenter() {
			return experimenter;
		}

		public void setExperimenter(String experimenter) {
			this.experimenter = experimenter;
		}


}
