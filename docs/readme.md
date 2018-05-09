<a name="module_jsApone"></a>

## jsApone ⇒ <code>object</code>
JS APONE Module (jsApone) returns the functions the client needs to interact with APONE.
When the module is created, the information of the experimental unit (if not set by the user), the variant assigned and the values of the variables in PlanOut scripts (if exist) are set.
This is done synchronously if JS Promises are not supported, and asynchronously if they are supported.
An event 'exposure' for that experimental unit is automatically registered in APONE.

**Returns**: <code>object</code> - Interface to interact with APONE from the client.  
**Access**: public  

| Param | Type | Description |
| --- | --- | --- |
| aponeURL | <code>string</code> | URL where APONE is hosted |
| idExperiment | <code>string</code> | Identifier of the experiment running in APONE |
| [idUnit] | <code>\*</code> | Identifier of the experimental unit. If not included, cookies are used instead. |
| [overrides] | <code>object</code> | Only if PlanOut scripts are used in the experiment: values of the variables in the scripts we want to override before calculating the value of the rest of them. |

**Example**  
```js
myExperiment = jsApone("http://localhost:8080/APONE", "5a7894f5da0ed16e95180701");
myExperiment.getExperimentalConditions(function(expCond){console.log(expCond);});
function registerEvents(){
	myExperiment.registerString("myEventName", "myEventValue", function(info){console.log("string event registered")}, function(status){console.log(status);});
	myExperiment.registerJSON("myEventName", {myvar1:"value1", myvar2:2}, function(info){console.log("JSON event registered")}, function(status){console.log(status);});
}
myExperiment.isCompleted(function(completed){if (!completed) registerEvents();});
```

* [jsApone](#module_jsApone) ⇒ <code>object</code>
    * [.ExperimentalConditions](#module_jsApone.ExperimentalConditions)
        * [.aponeURL](#module_jsApone.ExperimentalConditions+aponeURL)
        * [.idExperiment](#module_jsApone.ExperimentalConditions+idExperiment)
        * [.idUnit](#module_jsApone.ExperimentalConditions+idUnit)
        * [.variant](#module_jsApone.ExperimentalConditions+variant)
        * [.url](#module_jsApone.ExperimentalConditions+url)
        * [.variables](#module_jsApone.ExperimentalConditions+variables)
    * [.getExperimentalConditions(callback)](#module_jsApone.getExperimentalConditions)
    * [.registerExposure([cbSuccess], [cbError])](#module_jsApone.registerExposure)
    * [.registerCompleted([cbSuccess], [cbError])](#module_jsApone.registerCompleted)
    * [.registerString(eName, eValue, [cbSuccess], [cbError])](#module_jsApone.registerString)
    * [.registerJSON(eName, eValue, [cbSuccess], [cbError])](#module_jsApone.registerJSON)
    * [.registerBinaryString(eName, eValue, [cbSuccess], [cbError])](#module_jsApone.registerBinaryString)
    * [.registerBinaryStream(eName, eValue, [cbSuccess], [cbError])](#module_jsApone.registerBinaryStream)
    * [.isCompleted([cbSuccess], [cbError])](#module_jsApone.isCompleted)

<a name="module_jsApone.ExperimentalConditions"></a>

### jsApone.ExperimentalConditions
Experimental conditions set for this experiment.

**Kind**: static class of [<code>jsApone</code>](#module_jsApone)  

* [.ExperimentalConditions](#module_jsApone.ExperimentalConditions)
    * [.aponeURL](#module_jsApone.ExperimentalConditions+aponeURL)
    * [.idExperiment](#module_jsApone.ExperimentalConditions+idExperiment)
    * [.idUnit](#module_jsApone.ExperimentalConditions+idUnit)
    * [.variant](#module_jsApone.ExperimentalConditions+variant)
    * [.url](#module_jsApone.ExperimentalConditions+url)
    * [.variables](#module_jsApone.ExperimentalConditions+variables)

<a name="module_jsApone.ExperimentalConditions+aponeURL"></a>

#### experimentalConditions.aponeURL
{string} - URL where APONE is hosted and the experiment is running.

**Kind**: instance property of [<code>ExperimentalConditions</code>](#module_jsApone.ExperimentalConditions)  
<a name="module_jsApone.ExperimentalConditions+idExperiment"></a>

#### experimentalConditions.idExperiment
{string} - Identifier of the experiment in APONE.

**Kind**: instance property of [<code>ExperimentalConditions</code>](#module_jsApone.ExperimentalConditions)  
<a name="module_jsApone.ExperimentalConditions+idUnit"></a>

#### experimentalConditions.idUnit
{string} - Identifier of the experimental unit (e.g. identifier of the user).

**Kind**: instance property of [<code>ExperimentalConditions</code>](#module_jsApone.ExperimentalConditions)  
<a name="module_jsApone.ExperimentalConditions+variant"></a>

#### experimentalConditions.variant
{string} - Name of the variant assigned to to experimental unit [[idUnit](idUnit)] in the experiment [[idExperiment](idExperiment)].

**Kind**: instance property of [<code>ExperimentalConditions</code>](#module_jsApone.ExperimentalConditions)  
<a name="module_jsApone.ExperimentalConditions+url"></a>

#### experimentalConditions.url
{string} - URL corresponding to the variant assigned [[variant](variant)] as defined in the experiment [[idExperiment](idExperiment)]

**Kind**: instance property of [<code>ExperimentalConditions</code>](#module_jsApone.ExperimentalConditions)  
<a name="module_jsApone.ExperimentalConditions+variables"></a>

#### experimentalConditions.variables
{object} - Object containing variables and their values according to the PlanOut script (if any) assigned to the variant [[variant](variant)] in the experiment [[idExperiment](idExperiment)].

**Kind**: instance property of [<code>ExperimentalConditions</code>](#module_jsApone.ExperimentalConditions)  
<a name="module_jsApone.getExperimentalConditions"></a>

### jsApone.getExperimentalConditions(callback)
Get the experimental conditions set for this experiment and call the callback function with them. 
Note that the experimental conditions can not be modified once created the module.

**Kind**: static method of [<code>jsApone</code>](#module_jsApone)  
**Access**: public  

| Param | Type | Description |
| --- | --- | --- |
| callback | <code>function</code> | Callback function which receives the experimental conditions (see [[ExperimentalConditions](ExperimentalConditions)]). |

<a name="module_jsApone.registerExposure"></a>

### jsApone.registerExposure([cbSuccess], [cbError])
Register the 'exposure' event so the different experimental units being exposed to the experiment can be monitored in APONE. 
Note: this is automatically done when the module is created.

**Kind**: static method of [<code>jsApone</code>](#module_jsApone)  
**Access**: public  

| Param | Type | Description |
| --- | --- | --- |
| [cbSuccess] | <code>function</code> | Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured. |
| [cbError] | <code>function</code> | Callback function in case of error. Receives the status property of XMLHttpRequest. |

<a name="module_jsApone.registerCompleted"></a>

### jsApone.registerCompleted([cbSuccess], [cbError])
Register the 'completed' event so APONE can keep track of those experimental units which already completed the experiment.
APONE uses this information to monitor the data registered and to prevent users from being redirected from the participating interface to experiments which they already completed.
Experimenters may use the function [[isCompleted](isCompleted)] in this library to check if the current experimental unit already completed the experiment.

**Kind**: static method of [<code>jsApone</code>](#module_jsApone)  
**Access**: public  

| Param | Type | Description |
| --- | --- | --- |
| [cbSuccess] | <code>function</code> | Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured. |
| [cbError] | <code>function</code> | Callback function in case of error. Receives the status property of XMLHttpRequest. |

<a name="module_jsApone.registerString"></a>

### jsApone.registerString(eName, eValue, [cbSuccess], [cbError])
Register an event with String contents.

**Kind**: static method of [<code>jsApone</code>](#module_jsApone)  
**Access**: public  

| Param | Type | Description |
| --- | --- | --- |
| eName | <code>string</code> | Name of the event |
| eValue | <code>string</code> | Contents to be saved |
| [cbSuccess] | <code>function</code> | Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured. |
| [cbError] | <code>function</code> | Callback function in case of error. Receives the status property of XMLHttpRequest. |

<a name="module_jsApone.registerJSON"></a>

### jsApone.registerJSON(eName, eValue, [cbSuccess], [cbError])
Register an event with JSON contents.

**Kind**: static method of [<code>jsApone</code>](#module_jsApone)  
**Access**: public  

| Param | Type | Description |
| --- | --- | --- |
| eName | <code>string</code> | Name of the event |
| eValue | <code>object</code> | Contents to be saved: the JavaScript object received will be converted to JSON. |
| [cbSuccess] | <code>function</code> | Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured. |
| [cbError] | <code>function</code> | Callback function in case of error. Receives the status property of XMLHttpRequest. |

<a name="module_jsApone.registerBinaryString"></a>

### jsApone.registerBinaryString(eName, eValue, [cbSuccess], [cbError])
Register an event with BINARY contents (contained in a string).

**Kind**: static method of [<code>jsApone</code>](#module_jsApone)  

| Param | Type | Description |
| --- | --- | --- |
| eName | <code>string</code> | Name of the event |
| eValue | <code>string</code> | Contents to be saved: binary string. |
| [cbSuccess] | <code>function</code> | Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured. |
| [cbError] | <code>function</code> | Callback function in case of error. Receives the status property of XMLHttpRequest. |

<a name="module_jsApone.registerBinaryStream"></a>

### jsApone.registerBinaryStream(eName, eValue, [cbSuccess], [cbError])
Register an event with BINARY contents (contained in a stream).

**Kind**: static method of [<code>jsApone</code>](#module_jsApone)  
**Access**: public  

| Param | Type | Description |
| --- | --- | --- |
| eName | <code>string</code> | Name of the event |
| eValue | <code>stream</code> | Contents to be saved: binary stream (eg. file). |
| [cbSuccess] | <code>function</code> | Callback function in case of success. Receives no contents. Note: errors from the message broker are not captured. |
| [cbError] | <code>function</code> | Callback function in case of error. Receives the status property of XMLHttpRequest. |

<a name="module_jsApone.isCompleted"></a>

### jsApone.isCompleted([cbSuccess], [cbError])
Check if the experiment has already been completed for the current experimental unit.

**Kind**: static method of [<code>jsApone</code>](#module_jsApone)  
**Access**: public  

| Param | Type | Description |
| --- | --- | --- |
| [cbSuccess] | <code>function</code> | Callback function in case of success. Receives a boolean indicating if the experiment was completed by the experimental unit. |
| [cbError] | <code>function</code> | Callback function in case of error. Receives the status property of XMLHttpRequest. |

