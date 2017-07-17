package tudelft.dds.irep.data.schema;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class Experiment {
	
	private String idexp;
	private String name;
	private String experimenter;
	private String description;
	private String unit;
	private String control_treatment;
	private Treatment[] treatment;
	
	private Configuration[] config;

	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getControl_treatment() {
		return control_treatment;
	}
	public void setControl_treatment(String control_treatment) {
		this.control_treatment = control_treatment;
	}
	public Treatment[] getTreatment() {
		return treatment;
	}
	public void setTreatment(Treatment[] treatment) {
		this.treatment = treatment;
	}
	public String getIdexp() {
		return idexp;
	}
	public void setIdexp(String idexp) {
		this.idexp = idexp;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExperimenter() {
		return experimenter;
	}
	public void setExperimenter(String experimenter) {
		this.experimenter = experimenter;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Configuration[] getRun() {
		return config;
	}
	public void setRun(Configuration[] run) {
		this.config = run;
	}

	
}
