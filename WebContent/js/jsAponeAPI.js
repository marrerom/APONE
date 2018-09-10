/**
 * JavaScript Library to interact with APONE to define and manage experiments, users and events. 
 * For a library to interact with APONE from the clients running the experiments, see module jsApone instead.
 * @file 
 *  
 * @author Mónica Marrero
 * @version 0.1
 * @license MIT License
 * @copyright 2018 marrerom
 */

/**
 * JS APONE API Module returns the functions needed to interact with APONE in the definition and management of experiments, users and events. 
 * The use of this module makes easier the development of new interfaces for the management of APONE.
 * 
 * @module jsAponeAPI
 * @public
 * @param {string} aponeURL - URL where APONE is hosted
 * @returns {object} Interface to interact with APONE in the management of experiments and events. 
 * @example 
 * aponeAPI = jsAponeAPI("http://localhost:8080/APONE");
 * aponeAPI.newExperiment(experimentJson, function(xhttp){console.log("Success");}, function(xhttp){console.log("Error");});
 * aponeAPI.startExperiment(idExperiment, function(xhttp){console.log("Success");}, function(xhttp){console.log("Error");});
 */

var jsAponeGUI = function(aponeURL) {
	
	/** @var {object} aponeInterface - Interface returned 
	 * @private
	 */		
	var aponeInterface = {
			newExperiment: newExperiment, 
			newConfiguration: newConfiguration,
			getExperiment: getExperiment,
			getExperiments: getExperiments,
			startExperiment: startExperiment,
			stopExperiment: stopExperiment,
			deleteExperiment: deleteExperiment,
			getEvent: getEvent,
			getEvents: getEvents,
			getEventsCSV: getEventsCSV,
			getEventsJSON: getEventsJSON,
			deleteEvent: deleteEvent,
			monitorExperiments: monitorExperiments,
			monitorEventExperiment: monitorEventExperiment,
			monitorUsers: monitorUsers,
			getEventNames: getEventNames, 
			getAggregationTypes: getAggregationTypes, 
			getAggregatedData: getAggregatedData, 
			getAuthenticationURL: getAuthenticationURL,
			assignExperiment: assignExperiment,
			newUser: newUser,
			getUsers: getUsers,
			deleteUser: deleteUser
	}
	
	/**
	 * Returns XMLHttpRequest with credentials activated 
	 * 
	 * @function getXMLHttpRequest
	 * @private
	 * @memberof module:jsApone
	 * @returns XMLHttpRequest
	 */ 
	function getXMLHttpRequest() {
		if (window.XMLHttpRequest) { 	// code for modern browsers
			xmlhttp = new XMLHttpRequest();
		} else {                        // code for old IE browsers
			xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		xmlhttp.withCredentials = true;
		return xmlhttp;
	}

	/**
	 * Callback used in AJAX calls
	 *  
	 * @function callbackAJAX
	 * @memberof module:jsApone
	 * @private
	 */ 
	function callbackAJAX(xhttp, cbSuccess, cbError){
		if (xhttp.readyState === 4) {
			if (xhttp.status === 200 || xhttp.status === 204) {
				if (cbSuccess) cbSuccess();
			} else {
				if (cbError) cbError();
			}
		}
	}

	/*
	 * EXPERIMENT MANAGEMENT BLOCK
	 */

//	var newexpURL =  "service/experiment/new/experiment";
	function newExperiment(inputJson, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/experiment/new/experiment", true);
		xhttp. setRequestHeader("Content-Type", "application/json");
		xhttp.send(inputJson); 
	}

//	var newconfURL =  "service/experiment/new/configuration";
	function newConfiguration(inputJson, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/experiment/new/configuration", true);
		xhttp. setRequestHeader("Content-Type", "application/json");
		xhttp.send(inputJson); 
	}

//	var expsearchURL =  "service/experiment/search";
	//TODO: input as json instead of object
	function getExperiments(inputJson, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/experiment/search", true);
		xhttp. setRequestHeader("Content-Type", "application/json");
		xhttp.responseType = "json";
		xhttp.send(inputJson); 
	}
	
//	var startURL =  "service/experiment/start";
	function startExperiment(idExp, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('PUT', aponeURL+"/service/experiment/start", true);
		xhttp. setRequestHeader("Content-Type", "text/plain");
		xhttp.send(idExp); 
	}
		
//	var stopURL =  "service/experiment/stop";
	function stopExperiment(idExp, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('PUT', aponeURL+"/service/experiment/stop", true);
		xhttp. setRequestHeader("Content-Type", "text/plain");
		xhttp.send(idExp); 
	}
	
//	var removeURL =  "service/experiment/delete";
	function deleteExperiment(idExp, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/experiment/delete", true);
		xhttp. setRequestHeader("Content-Type", "text/plain");
		xhttp.send(idExp); 
	}
	
//	var getExperimentURL =  "service/experiment/get";
	function getExperiment(idExp, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/experiment/get/"+idExp, true);
		xhttp.responseType = "json";
		xhttp.send(); 
	}
	


	
	/*
	 * EVENT MANAGEMENT BLOCK
	 */

//	var getEventURL =  "service/event/get";
	function getEvent(idEvent, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/event/get/"+idEvent, true);
		xhttp.responseType = "json";
		xhttp.send(); 
	}

//	var eventSearchURL =  "service/event/search"; 
	//TODO: receives json instead of object!
	function getEvents(inputJson, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/event/search", true);
		xhttp. setRequestHeader("Content-Type", "application/json");
		xhttp.responseType = "json";
		xhttp.send(inputJson); 
	}
	
//	var removeEventURL =  "service/event/delete";
	function deleteEvent(idEvent, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/event/delete", true);
		xhttp. setRequestHeader("Content-Type", "text/plain");
		xhttp.send(idEvent); 
	}
	
//	var getCSVURL =  "service/event/getCSV";
	//TODO: input as json instead of object!
	function getEventsCSV(eventList, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/event/getCSV", true);
		xhttp. setRequestHeader("Content-Type", "application/json");
		xhttp.send(eventList); 
	}

//	var getJSONURL =  "service/event/getJSON";
	//TODO: input as json instead of object!
	function getEventsJSON(eventList, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/event/getisCompletedJSON", true);
		xhttp. setRequestHeader("Content-Type", "application/json");
		xhttp.send(eventList); 
	}

	/*
	 * MONITORING BLOCK
	 */

//	var monitorURL =  "service/experiment/monitor";
	function monitorExperiments(cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/experiment/monitor", true);
		xhttp.responseType = "json";
		xhttp.send(); 
	}
	
//	var monitorSubtreatURL =  "service/experiment/monitor/subtreatments";
	function monitorEventExperiment(idExp, ename, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/experiment/monitor/subtreatments/"+idExp+"/"+ename, true);
		xhttp.responseType = "json";
		xhttp.send(); 
	}
	
//	var monitorUsersURL =  "service/user/monitoring";
	function monitorUsers(cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/user/monitoring", true);
		xhttp.responseType = "json";
		xhttp.send(); 
	}

//	var getEnamesURL = "service/event/enames";
	function getEventNames(idExp, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/event/enames/"+idExp, true);
		xhttp.responseType = "json";
		xhttp.send(); 
	}
	
//	var getAggregationTypesURL = "service/event/aggregationTypes";
	function getAggregationTypes(cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/event/aggregationTypes", true);
		xhttp.responseType = "json";
		xhttp.send(); 
	}
	
//	var getAggregationURL = "service/event/aggregate";
	//TODO: inputJson is json, not object
	function getAggregatedData(inputJson, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/event/aggregate", true);
		xhttp. setRequestHeader("Content-Type", "application/json");
		xhttp.responseType = "json";
		xhttp.send(inputJson); 
	}

	
	/*
	 * USER MANAGEMENT BLOCK
	 */

//	var getUserURL =  "service/user/authenticatedUser";
	function getAuthenticationURL(){
		return aponeURL +"/service/user/authenticatedUser";
	}

//	var assignmentURL =  "service/user/assignexp";
	function assignExperiment(cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/user/assignexp", true);
		xhttp.responseType = "text";
		xhttp.send(); 
	}

//	var adminUsersURL =  "service/user/admin";
	function getUsers(cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('GET', aponeURL+"/service/user/admin", true);
		xhttp.responseType = "json";
		xhttp.send(); 
	}

//	var deleteUserURL =  "service/user/delete";
	function deleteUser(idUser, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('PUT', aponeURL+"/service/user/delete/"+idUser, true);
		xhttp.send(); 
	}
	
//	var newUserURL = "service/user/add";
	function newUser(inputJson, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, cbSuccess, cbError);
			};
		
		xhttp.open('POST', aponeURL+"/service/user/add", true);
		xhttp. setRequestHeader("Content-Type", "application/json");
		xhttp.send(inputJson); 
	}

	
	return aponeInterface;
}