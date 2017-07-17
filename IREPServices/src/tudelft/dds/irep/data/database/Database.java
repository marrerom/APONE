package tudelft.dds.irep.data.database;

import tudelft.dds.irep.data.schema.Experiment;

public interface Database {
	
	public String AddExperiment(Experiment experiment );
	
	public void close();

}
