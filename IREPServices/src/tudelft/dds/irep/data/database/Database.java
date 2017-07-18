package tudelft.dds.irep.data.database;

import tudelft.dds.irep.data.schema.Experiment;

public interface Database {
	
	public String AddExperiment(Experiment experiment );
	
	public String GetYAML(String idexp);
	
	public void close();

}
