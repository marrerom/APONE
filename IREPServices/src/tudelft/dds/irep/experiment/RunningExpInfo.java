package tudelft.dds.irep.experiment;

import java.util.Date;
import com.glassdoor.planout4j.NamespaceConfig;
import tudelft.dds.irep.data.schema.Status;
import tudelft.dds.irep.messaging.EventMonitoringConsumer;
import tudelft.dds.irep.messaging.EventRegisterConsumer;

public class RunningExpInfo {
		public NamespaceConfig conf;
		public Status status;
		public EventRegisterConsumer regConsumer;
		public EventMonitoringConsumer monConsumer;
		public Date dateToEnd;
		public Integer maxExposures;
		
		public RunningExpInfo(NamespaceConfig conf, Status status, EventRegisterConsumer regConsumer, EventMonitoringConsumer monConsumer, Date dateToEnd, Integer maxExposures) {
			this.conf = conf;
			this.status = status;
			this.regConsumer = regConsumer;
			this.monConsumer = monConsumer;
			this.dateToEnd = dateToEnd;
			this.maxExposures = maxExposures;
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
	
}
