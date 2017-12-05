/**
 * Experiment Monitoring
 */
var tabmonexp;

$(document).ready(function() {
	
	const REFRESH=30000; //update after time in milliseconds
	
	const row = "<tr id='idrun.laststarted'><td>  <div class='inline field'><div class='ui toggle checkbox'><input tabindex='0' name='toggle' type='checkbox'>" +
			"<label>ON</label></div></div></td>" +
			"<td><span class='experiment sort'>Experiment</span></td>" +
			"<td><span class='date_started'>Date</span><span class='date_ended'></span></td>" +
			"<td><span class='max_exposures sort'>None</span></td>" +
			"<td><span class='date_to_end'>None</span></td>" +
			"<td><span class='treatments'>Treatments</span></td>" +
			"</tr>";
	
	var monrunning,monfinished;
	
	function init(){
		tabmonexp = $("#contents_monexp");
		monrunning = tabmonexp.find(".ui.compact.red.celled.table._running > tbody");
		monfinished = tabmonexp.find(".ui.compact.grey.celled.table._finished > tbody");
		refresh();
		setInterval(function(){ refresh(); }, REFRESH); 
	}
	
	function refresh(){
		getRunning();
	}
	
//	function getRunningError(xhr, status, error){
//		console.error("There was an error loading the running experiments");
//	}
	
	function getTreatmentsHTML(treatments){
		var result = "";
		var total = 0;
		$.each(treatments, function (index, value) {
			total = total + parseInt(value.value);
		});
		$.each(treatments, function (index, value) {
			var per = Math.round((parseInt(value.value) / total) * 100);
			result = result +"<div class='ui mini statistic'><div class='value'>"+value.value+" ("+per+"%)</div><div class='label'>"+value.name+"</div></div>";
		});
		result = result + "<div class='ui mini statistic'><div class='value sort'>"+total+"</div><div class='label'> Total </div></div>";
		return result;
	}
	
	function getRunningSuccess(data){
		var tofinish=[];
		var tocreate = [];
		
		$.each(monrunning.children(), function(index, item){
			tofinish.push($(item).attr("id"));
		});
		
		$.each(data, function (index, value) {
			var id = value.idrun+"-"+value.laststarted;
			var item = monrunning.find("#"+id);
			if (item.length > 0){ //if match, update, else take to finish
				item.find(".treatments").html(getTreatmentsHTML(value.treatments));
				tofinish.splice($.inArray(id, tofinish),1);
			} else {
				tocreate.push(value);
			}
		});
		
		//Important to remove before adding new experiments as running
		$.each(tofinish, function(index, id){ 
			//monfinished.append(value);
			finish(id);
		});
		
		$.each(tocreate, function(index, value){
			create(value);
		});
	}
	
	function finish(id){
		item = monrunning.find("#"+id);
		item.find("[name=toggle]").prop("checked", false);
		item.find(".treatments").off(); //removes click event
		item.css( 'cursor', 'default' );
		monfinished.prepend(item);
		getExperimentFinished(id.substr(0,id.indexOf("-")));
	}
	
	function create(value){
		var newrow = row;
		newrow = $(newrow).attr("id", value.idrun+"-"+value.laststarted);
		newrow.find("[name=toggle]").prop("checked", true);
		newrow.find("[name=toggle]").on("change", function(event){
			 if (event.originalEvent){
				if ($(this).is(":checked")){
					startExperiment(value.idrun);
				} else {
					stopExperiment(value.idrun);
				}
			 }
		});
		newrow.find(".treatments").on("click", function(){
			var id = newrow.attr("id");
			displayParamValues(id.substr(0,id.indexOf("-")));
		});
		newrow.find(".treatments").css( 'cursor', 'pointer' );
		var date_started = new Date(value.laststarted);
		//newrow.find(".date_started").text(date_started.toLocaleDateString()+" "+date_started.toLocaleTimeString());
		newrow.find(".date_started").html("<span hidden class='sort date'>"+date_started+"</span><span>"+date_started.toLocaleDateString()+" "+date_started.toLocaleTimeString()+"</span>");
		monrunning.prepend(newrow);
		getExperimentCreate(value.idrun);
		newrow.find(".treatments").html(getTreatmentsHTML(value.treatments));
	}
	
//	function displayParamError(xhr, status, error){
//		$(".ui.modal._error").find(".content").empty();
//		$(".ui.modal._error").find(".content").append("<p>There was an error loading data from experiment +"+$(this).attr("data")+"</p>");
//		//$(".ui.modal._error").find(".content").append(xhr.responseText);
//		$(".ui.modal._error").modal('show');
//	}
	
	function displayParamSuccess(data){
		$(".ui.modal._info").find(".header").empty();
		$(".ui.modal._info").find(".header").append("Treatment Monitoring Experiment id "+data.idrun);
		$(".ui.modal._info").find(".content").empty();
		$.each(data.treatments, function (index, treatment){
			var body ="";
			$.each(treatment.subtreatments, function (index, subtreatment){
				body = body + "<tr> <td>"+subtreatment.params+"</td> <td>"+subtreatment.value+"</td> </tr>";
			});
			body = body + "<tr> <td>TOTAL</td> <td>"+treatment.value+"</td> </tr>";
			var header = "<h3 class='ui blue header'>"+treatment.name+"</h3>"
			var table ="<table class='ui compact blue celled table'><thead><th>Values</th><th>Exposures</th></thead><tbody>"+body+"<tbody></table>";
			$(".ui.modal._info").find(".content").append(header);
			$(".ui.modal._info").find(".content").append(table);
		});
		
		
		$(".ui.modal._info").modal('show');

	}

	
	function displayParamValues(idconf){
		var errorMessage = "<p>There was an error loading data from experiment +"+$(this).attr("data")+"</p>";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: monitorSubtreatmentsURL+"/"+idconf,
			  success: displayParamSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	function getRunning(){
		var errorMessage = "There was an error loading the running experiments";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: monitorTreatmentsURL,
			  success: getRunningSuccess,
			  error: function(xhr, status, error) {consoleError(xhr, errorMessage);}
			});
	}
	
	function getExpCreateSuccess(data){
		var config = data.config[0];
		var newrow = monrunning.find("[id^="+config._id+"]"); //there should be only one experiment/configuration running
		newrow.find(".experiment").text(data.name+"@"+config.experimenter+": "+config.name);
		if (config.max_exposures)
			newrow.find(".max_exposures").text(config.max_exposures);
		if (config.date_to_end){
			var date_to_end = new Date(config.date_to_end);
			//newrow.find(".date_to_end").text(date_to_end.toLocaleDateString()+" "+date_to_end.toLocaleTimeString());
			newrow.find(".date_to_end").html("<span hidden class='sort date'>"+date_to_end+"</span><span>"+date_to_end.toLocaleDateString()+" "+date_to_end.toLocaleTimeString()+"</span>");
		}
	}
	
	function getExpFinishedSuccess(data){
		var config = data.config[0];
		var row = monfinished.find("[id^="+config._id+"]");
		if (row.find(".date_ended").text() == ""){
			var laststarted = new Date(parseInt(row.attr("id").substr(row.attr("id").indexOf("-") + 1))).getTime();
			var date_started_array = config.date_started;
			
			for (i=0;i<date_started_array.length;i++){
				var longdate = new Date(date_started_array[i]).getTime();
				if (longdate == laststarted){
					var date_ended = new Date(config.date_ended[i]);
					row.find(".date_ended").text(" - "+date_ended.toLocaleDateString()+ " " + date_ended.toLocaleTimeString());
				
				}
			}
		}
	}
	
	function getExperimentCreate(idrun){
		var errorMessage = "There was an error loading "+$(this).data+" experiment";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: getExperimentURL +"/"+ idrun,
			  success: getExpCreateSuccess,
			  error: function(xhr, status, error) {consoleError(xhr, errorMessage);}
			});
	}
	
	function getExperimentFinished(idrun){
		var errorMessage = "There was an error loading "+$(this).data+" experiment";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: getExperimentURL +"/"+ idrun,
			  success: getExpFinishedSuccess,
			  error: function(xhr, status, error) {consoleError(xhr, errorMessage);}
			});
	}
	
//	function startError(xhr, status, error){
//		$(".ui.modal._error").find(".content").empty();
//		$(".ui.modal._error").find(".content").append("<p>Experiment +"+$(this).attr("data")+" did not start.</p><p>The date or exposures might have exceeded the settings.</p>");
//		//$(".ui.modal._error").find(".content").append(xhr.responseText);
//		$(".ui.modal._error").modal('show');
//
//	}
	
	
	function startExperiment(idrun){
		var errorMessage = "<p>Experiment +"+$(this).attr("data")+" did not start.</p><p>The date or exposures might have exceeded the settings.</p>";
		$.ajax({
			  contentType: 'text/plain',
			  type: 'PUT',
			  url: startURL,
			  data: idrun,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
//	function stopError(xhr, status, error){
//		$(".ui.modal._error").find(".content").empty();
//		$(".ui.modal._error").find(".content").append("<p>Experiment "+$(this).attr("data")+" did not start</p>");
//		//$(".ui.modal._error").find(".content").append(xhr.responseText);
//		$(".ui.modal._error").modal('show');
//	}
	
	function stopExperiment(idrun){
		var errorMessage = "<p>Experiment "+$(this).attr("data")+" did not start</p>";
		$.ajax({
			  contentType: 'text/plain',
			  type: 'PUT',
			  url: stopURL,
			  data: idrun,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	init();
});