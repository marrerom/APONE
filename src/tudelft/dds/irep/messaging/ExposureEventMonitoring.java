package tudelft.dds.irep.messaging;

import java.util.Collection;

import tudelft.dds.irep.data.schema.JEvent;

public class ExposureEventMonitoring extends EventMonitoring {
	
	public ExposureEventMonitoring() {
		super();
	}
		
	public ExposureEventMonitoring(Collection<JEvent> events) {
		super(events);
	}

}
