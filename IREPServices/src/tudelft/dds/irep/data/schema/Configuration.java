package tudelft.dds.irep.data.schema;

import java.util.Date;

public class Configuration {
	private String idrun;
	private String name;
	private String experimenter;
	private String description;
	private String controller_code;
	private Date date_started;
	private Date date_ended;
	private Status run;
	private int segments;
	private Date date_to_end;
	private int max_exposures;
	private Distribution[] distr;
	private Status test;
	
	public int getSegments() {
		return segments;
	}
	public void setSegments(int segments) {
		this.segments = segments;
	}
	public Date getDate_to_end() {
		return date_to_end;
	}
	public void setDate_to_end(Date date_to_end) {
		this.date_to_end = date_to_end;
	}
	public int getMax_exposures() {
		return max_exposures;
	}
	public void setMax_exposures(int max_exposures) {
		this.max_exposures = max_exposures;
	}
	public Distribution[] getDistr() {
		return distr;
	}
	public void setDistr(Distribution[] distr) {
		this.distr = distr;
	}
	public Status getTest() {
		return test;
	}
	public void setTest(Status test) {
		this.test = test;
	}

	
	public String getIdrun() {
		return idrun;
	}
	public void setIdrun(String idrun) {
		this.idrun = idrun;
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
	public String getController_code() {
		return controller_code;
	}
	public void setController_code(String controller_code) {
		this.controller_code = controller_code;
	}
	public Date getDate_started() {
		return date_started;
	}
	public void setDate_started(Date date_started) {
		this.date_started = date_started;
	}
	public Date getDate_ended() {
		return date_ended;
	}
	public void setDate_ended(Date date_ended) {
		this.date_ended = date_ended;
	}
	public Status getRun() {
		return run;
	}
	public void setRun(Status run) {
		this.run = run;
	} 

	
}
