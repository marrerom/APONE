package tudelft.dds.irep.data;

public interface Database {
	
	public String AddExperiment(String expname, String experimenter, String description, String yaml);
	
	public String GetYAML(String idexp);
	
	public void close();

}
