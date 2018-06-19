/**
 * JavaScript Library to make it easier to interact with APONE from the client-side of the website exposed to the users participating in the experiment.
 * @file 
 *  
 * @author Mónica Marrero
 * @version 0.1
 * @license MIT License
 * @copyright 2017 marrerom
 */

/**
 * JS APONE Module (jsApone) returns the functions the client needs to interact with APONE.
 * When the module is created, the information of the experimental unit (if not set by the user), the variant assigned and the values of the variables in PlanOut scripts (if exist) are set.
 * This is done synchronously if JS Promises are not supported, and asynchronously if they are supported.
 * An event 'exposure' for that experimental unit is automatically registered in APONE.
 * 
 * @module jsApone
 * @public
 * @param {string} aponeURL - URL where APONE is hosted
 * @param {string} idExperiment - Identifier of the experiment running in APONE
 * @param {*} [idUnit] - Identifier of the experimental unit. If not included, cookies are used instead.
 * @param {object} [overrides] - Only if PlanOut scripts are used in the experiment: values of the variables in the scripts we want to override before calculating the value of the rest of them.
 * @returns {object} Interface to interact with APONE from the client. 
 * @example 
 * myExperiment = jsApone("http://localhost:8080/APONE", "5a7894f5da0ed16e95180701");
 * myExperiment.getExperimentalConditions(function(expCond){console.log(expCond);});
 * function registerEvents(){
 *	myExperiment.registerString("myEventName", "myEventValue", function(info){console.log("string event registered")}, function(status){console.log(status);});
 *	myExperiment.registerJSON("myEventName", {myvar1:"value1", myvar2:2}, function(info){console.log("JSON event registered")}, function(status){console.log(status);});
 * }
 * myExperiment.isCompleted(function(completed){if (!completed) registerEvents();});
 */
var jsApone = function(aponeURL, idExperiment, idUnit, overrides){
	/** @var {string} aponeURL - URL where APONE is hosted and the experiment is running 
	 *  @private
	 */
	var aponeURL = aponeURL;

	/** @var {string} idExperiment - Identifier of the experiment in APONE 
	 *  @private
	 */
	var idExperiment = idExperiment;
	
	/** @mvar {string} idUnit - Identifier of the experimental unit (e.g. identifier of the user) 
	 *  @private
	 */
	var idUnit = idUnit;
	
	/** @var {object}  variantInfo - Information of the variant: name ('_variant'), url if defined ('_url'), object with the values of the variables defined in PlanOut scripts ('params') and experimental unit ('_idunit') 
	 *  @private
	 */
	var variantInfo;

	/** @var {Promise} p - JS Promise used to set up the initial conditions 
	 *  @private
	 */
	var p;
	

	/** @var {object} pInterface - Interface returned when JS Promises are supported 
	 * @private
	 */
	var pInterface = {
		getExperimentalConditions: function(callback){p.then(function(){getExperimentalConditions(callback);});},	
		registerExposure: function(cbSuccess, cbError){p.then(function(){registerExposure(cbSuccess, cbError);});},
		registerCompleted: function(cbSuccess, cbError){p.then(function(){registerCompleted(cbSuccess, cbError);});},
		registerString: function(eName, eValue, cbSuccess, cbError){p.then(function(){registerString(eName, eValue, cbSuccess, cbError);});},
		registerJSON: function(eName, eValue, cbSuccess, cbError){p.then(function(){registerJSON(eName, eValue, cbSuccess, cbError);});},
		registerBinaryString: function(eName, eValue, cbSuccess, cbError){p.then(function(){registerBinaryString(eName, eValue, cbSuccess, cbError);});},
		registerBinaryStream: function(eName, eValue, cbSuccess, cbError){p.then(function(){registerBinaryStream(eName, eValue, cbSuccess, cbError);});},
		isCompleted: function(cbSuccess, cbError){p.then(function(){isCompleted(cbSuccess, cbError);});}
	}
	
	/** @var {object} npInterface - Interface returned when JS Promises are NOT supported 
	 * @private
	 */		
	var npInterface = {
		getExperimentalConditions: getExperimentalConditions, 
		registerExposure: registerExposure,
		registerCompleted: registerCompleted,
		registerString: registerString,
		registerJSON: registerJSON,
		registerBinaryString: registerBinaryString,
		registerBinaryStream: registerBinaryStream,
		isCompleted: isCompleted,
	}

	
	/** @class Experimental conditions set for this experiment. 
	 *  @memberof module:jsApone
	 */
	function ExperimentalConditions(){
		/** {string} - URL where APONE is hosted and the experiment is running. */
		this.aponeURL = aponeURL;
		
		/** {string} - Identifier of the experiment in APONE.*/
		this.idExperiment = idExperiment;
		
		/** {string} - Identifier of the experimental unit (e.g. identifier of the user).*/
		this.idUnit = variantInfo._idunit;
		
		/** {string} - Name of the variant assigned to to experimental unit [{@link idUnit}] in the experiment [{@link idExperiment}].*/
		this.variant = variantInfo._variant;
		
		/** {string} - URL corresponding to the variant assigned [{@link variant}] as defined in the experiment [{@link idExperiment}] */
		this.url = variantInfo._url;
		
		/** {object} - Object containing variables and their values according to the PlanOut script (if any) assigned to the variant [{@link variant}] in the experiment [{@link idExperiment}]. */
		this.variables = variantInfo.params;
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
	
	/**
	 * Get cookie 'cname'
	 * 
	 * @function getCookie
	 * @memberof module:jsApone
	 * @private
	 */
	function getCookie(cname) {
	    var name = cname + "=";
	    var decodedCookie = decodeURIComponent(document.cookie);
	    var ca = decodedCookie.split(';');
	    for(var i = 0; i < ca.length; i++) {
	        var c = ca[i];
	        while (c.charAt(0) == ' ') {
	            c = c.substring(1);
	        }
	        if (c.indexOf(name) == 0) {
	            return c.substring(name.length, c.length);
	        }
	    }
	    return null;
	}
	

	/**
	 * Sets the experimental conditions (see [{@link ExperimentalConditions}]) when the module is created. 
	 * Part of that information is included when we register an event in APONE. 
	 * That information can not be modified.
	 * 
	 * @function setVariant
	 * @memberof module:jsApone
	 * @private
	 * @param {object} [overrides] - Only if PlanOut scripts are used in the experiment: values of the variables in the scripts we want to override before it is executed for a specific experimental unit.
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives a JS object with the experimental conditions. See [{@link ExperimentalConditions}].
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 * @param {boolean} [async] - If the call to APONE is synchronous or asynchronous (note that synchronous calls are deprecated and should be avoided).
	 * @throws {Error} 
	 */
	function setVariant(overrides, cbSuccess, cbError, sync) {
		var xhttp = getXMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, function(){variantInfo = JSON.parse(xhttp.responseText); registerExposure(); if (cbSuccess) {var expCond = new ExperimentalConditions(); cbSuccess(expCond)};}, function(){throw new Error("APONE set variant error");if (cbError) {cbError(xhttp.status);};});
			};
		if (!idUnit){
			idUnit = getCookie(idExperiment);
		}
		if (idUnit){
			xhttp.open("GET", aponeURL+"/service/experiment/getparams/"+idExperiment+"/"+idUnit, !sync);
			xhttp.send();
		} else {
			xhttp.open("POST", aponeURL+"/service/experiment/getparams/", !sync); 
			xhttp.setRequestHeader("Content-Type","text/plain");
			var inputJson = new Object();
			inputJson.idconfig = idExperiment;
			if (overrides){
				inputJson.overrides = overrides;
			}
			var inputTxt = JSON.stringify(inputJson);
			xhttp.send(inputTxt);
		}
	}
	
	/**
	 * Get the experimental conditions set for this experiment and call the callback function with them. 
	 * Note that the experimental conditions can not be modified once created the module. 
	 * 
	 * @function getExperimentalConditions
	 * @memberof module:jsApone
	 * @public
	 * @param {function} callback - Callback function which receives the experimental conditions (see [{@link ExperimentalConditions}]).
	 */
	function getExperimentalConditions(callback){
		var expCond = new ExperimentalConditions();
		callback(expCond);
	}
	
	
	/**
	 * Register the 'exposure' event so the different experimental units being exposed to the experiment can be monitored in APONE. 
	 * Note: this is automatically done when the module is created.
	 * 
	 * @function registerExposure
	 * @memberof module:jsApone
	 * @public
	 * @async 
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured.
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 */
	function registerExposure(cbSuccess, cbError){
		registerString("exposure","",cbSuccess, cbError);
	}

	
	/**
	 * Register the 'completed' event so APONE can keep track of those experimental units which already completed the experiment.
	 * APONE uses this information to monitor the data registered and to prevent users from being redirected from the participating interface to experiments which they already completed.
	 * Experimenters may use the function [{@link isCompleted}] in this library to check if the current experimental unit already completed the experiment.
	 * @function registerCompleted
	 * @memberof module:jsApone
	 * @public
	 * @async 
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured.
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 */
	function registerCompleted(cbSuccess, cbError){
		registerString("completed","",cbSuccess, cbError);
	}

	/**
	 * Register an event with String contents. 
	 * 
	 * @function registerString
	 * @memberof module:jsApone
	 * @public
	 * @async 
	 * @param {string} eName - Name of the event
	 * @param {string}  eValue - Contents to be saved 
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured.
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 */
	function registerString(eName, eValue, cbSuccess, cbError){
		registerEvent("STRING", eName, eValue, cbSuccess, cbError);
	}

	/**
	 * Register an event with JSON contents. 
	 * 
	 * @function registerJSON
	 * @memberof module:jsApone
	 * @public
	 * @async 
	 * @param {string} eName - Name of the event
	 * @param {object}  eValue - Contents to be saved: the JavaScript object received will be converted to JSON.
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured.
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 */
	function registerJSON(eName, eValue, cbSuccess, cbError){
		registerEvent("JSON", eName, eValue, cbSuccess, cbError);
	}

	/**
	 * Register an event with BINARY contents (contained in a string). 
	 * 
	 * @function registerBinaryString
	 * @memberof module:jsApone
	 * @async 
	 * @param {string} eName - Name of the event
	 * @param {string}  eValue - Contents to be saved: binary string. 
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured.
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 */
	function registerBinaryString(eName, eValue, cbSuccess, cbError){
		eValue = btoa(eValue);
		registerEvent("BINARY", eName, eValue, cbSuccess, cbError);
	}
	
	/**
	 * Register an event with BINARY contents (contained in a stream). 
	 * 
	 * @function registerBinaryStream
	 * @memberof module:jsApone
	 * @public
	 * @async 
	 * @param {string} eName - Name of the event
	 * @param {stream}  eValue - Contents to be saved: binary stream (eg. file). 
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured.
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 */
	function registerBinaryStream(eName, eValue, cbSuccess, cbError){
		var xhttp = new XMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, function(){if (cbSuccess) {cbSuccess(xhttp.response);};}, function(){if (cbError) {cbError(xhttp.status);};});
			};
		
		var formData = new FormData();
		if (idUnit){
			formData.append("idunit", idUnit);
		}
		formData.append("idExperiment", idExperiment);
		formData.append("etype", "BINARY");
		formData.append("ename", eName);
		formData.append("evalue", eValue);
		if (paramValues)
			formData.append("paramvalues", paramValues);
		
		xhttp.open('POST', aponeURL+"/service/event/register", true);
		xhttp. setRequestHeader("Content-Type", "multipart/form-data");
		xhttp.send(formData); 
	}
	
	
	/**
	 * Register an event in APONE. 
	 * 
	 * @function registerEvent
	 * @memberof module:jsApone
	 * @async 
	 * @private
	 * @param {string} eType - Format of the contents to be registered: 'JSON', 'STRING' or 'BINARY'
	 * @param {string} eName - Name of the event
	 * @param {object}  eValue - Contents to be saved: the JavaScript object received will be converted to JSON.
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured.
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 */
	function registerEvent(eType, eName, eValue, cbSuccess, cbError) {
		var xhttp = getXMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, function(){if (cbSuccess) {cbSuccess(xhttp.response);};}, function(){if (cbError) {cbError(xhttp.status);};});
		};
		
		var inputJson = new Object();
		if (idUnit){
			inputJson.idunit = idUnit;
		}
		inputJson.idconfig = idExperiment;
		inputJson.etype = eType;
		inputJson.ename = eName;
		inputJson.evalue = eValue;
		inputJson.paramvalues = variantInfo.params;
		
		xhttp.open("POST", aponeURL+"/service/event/register", true);
		xhttp.setRequestHeader("Content-Type", "text/plain");   
		var inputTxt = JSON.stringify(inputJson);
		xhttp.send(inputTxt);
	}
	

	/**
	 * Check if the experiment has already been completed for the current experimental unit. 
	 * 
	 * @function isCompleted
	 * @memberof module:jsApone
	 * @public
	 * @async
	 * @param {function}  [cbSuccess] - Callback function in case of success. Receives a boolean indicating if the experiment was completed by the experimental unit.
	 * @param {function} [cbError] - Callback function in case of error. Receives the status property of XMLHttpRequest.
	 */
	function isCompleted(cbSuccess, cbError){
		var xhttp = getXMLHttpRequest();
		xhttp.onreadystatechange = function(){
			callbackAJAX(xhttp, function(){if (cbSuccess) {cbSuccess(xhttp.response === "true");};}, function(){if (cbError) {cbError(xhttp.status);};});
			};
		if (idUnit){
			xhttp.open("GET", aponeURL + "/service/user/checkcompleted/"+idExperiment+"/"+idUnit, true);
		} else {
			xhttp.open("GET", aponeURL + "/service/user/checkcompleted/"+idExperiment, true);
		}
		xhttp.send();	
	}
	

	if (!Promise){ //Promise not supported
		setVariant(overrides,null,function(){throw new Error("APONE module initialization error");},true);
		return npInterface;
	} else {
		p = new Promise(function(resolve,reject){
			setVariant(overrides, 
				function(info){
					resolve(null);
				}, 
				function(){
					reject(null);
					throw new Error("APONE module initialization error");
				});
		});
		return pInterface;
	}
		

}

