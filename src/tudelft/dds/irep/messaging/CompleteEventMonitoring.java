package tudelft.dds.irep.messaging;

import java.util.Collection;

import tudelft.dds.irep.data.schema.JEvent;

public class CompleteEventMonitoring extends EventMonitoring{
	public CompleteEventMonitoring() {
		super();
	}
	
	public CompleteEventMonitoring(Collection<JEvent> events) {
		super(events);
	}

}
