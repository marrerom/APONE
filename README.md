# IREPlatform
Experimental Platform for Information Retrieval

## Introduction

A/B experiments
PlanOut language
Services and GUI over planout4j

## Installation

Tomcat
RabbitMQ
MongoDB

## User guide

1. Define Experiment

	* New Experiment
	* New configuration from an existing experiment (distribution of treatments, date to end, 	max exposures). It creates a new experiment

2. Start Experiment

3. From client

	* Get parameters
	[host]/IREPServices/service/experiment/getParams  
	@Consumes:  
		@FormDataParam("idconfig") id experiment  
		@FormDataParam("idunit") keyword used in planout that identifies unique users/sessions/etc.   
		@FormDataParam("timestamp"): timestamp of the request  
	@Produces: {param:value, ...} 
	
	Automatically register an event called "exposure"
	
	* Register an event   
	[host]/IREPServices/service/event/register  
	@Consumes:  
		@FormDataParam("idconfig"): Id experiment  
		@FormDataParam("timestamp"): timestamp of the request  
		@FormDataParam("unitid"): id of the  user/session/etc. used in the definition in planout language  
		@FormDataParam("binary") : if the value to register is binary  
		@FormDataParam("ename"): type of event  
		@FormDataParam("evalue"): value to register (Stream)  
