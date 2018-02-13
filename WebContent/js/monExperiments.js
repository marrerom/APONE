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
			"<td><span class='data'><i class='big area chart icon'></i></span></td>" +
			"</tr>";
	
	var monrunning,monfinished;
	
	function init(){
		tabmonexp = $("#contents_monexp");
		monrunning = tabmonexp.find(".celled.table._running > tbody");
		monfinished = tabmonexp.find(".celled.table._finished > tbody");
		refresh();
		setInterval(function(){ refresh(); }, REFRESH); 
	}
	
	function refresh(){
		getRunning();
	}

	
	function getTreatmentsHTML(exposures, completed){
		var result = "";
		var totalexposures = 0;
		$.each(exposures, function (index, value) {
			totalexposures = totalexposures + parseInt(value.value);
		});

		result = result + "<div class='ui mini statistic exposures'><div class='value sort'>"+totalexposures+"</div><div class='label'> Exposures </div></div>";
		
		var totalcompleted = 0;
		$.each(completed, function (index, value) {
			totalcompleted = totalcompleted + parseInt(value.value);
		});
		result = result + "<div class='ui mini statistic completed'><div class='value sort'>"+totalcompleted+"</div><div class='label'> Completed </div></div>";
	
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
				item.find("[name=toggle]").prop("checked", true);
				
				item.find(".treatments").html(getTreatmentsHTML(value.exposures, value.completed));
				
				item.find(".ui.mini.statistic.exposures").on("click", function(){
					displayParamValues(id.substr(0,id.indexOf("-")), "exposure");
				});
				item.find(".ui.mini.statistic.exposures").css( 'cursor', 'pointer' );
				item.find(".ui.mini.statistic.completed").on("click", function(){
					displayParamValues(id.substr(0,id.indexOf("-")), "completed");
				});
				item.find(".ui.mini.statistic.completed").css( 'cursor', 'pointer' );

				tofinish.splice($.inArray(id, tofinish),1);
			} else {
				tocreate.push(value);
			}
		});
		
		//Important to remove before adding new experiments as running
		$.each(tofinish, function(index, id){ 
			finish(id);
		});
		
		$.each(tocreate, function(index, value){
			create(value);
		});
	}
	
	function finish(id){
		item = monrunning.find("#"+id);
		item.find("[name=toggle]").prop("checked", false);
		item.find(".ui.mini.statistic.exposure").off();
		item.find(".ui.mini.statistic.completed").off();
		item.find(".chart.icon").off();
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
		
		var date_started = new Date(value.laststarted);
		newrow.find(".date_started").html("<span hidden class='sort date'>"+date_started+"</span><span>"+date_started.toLocaleDateString()+" "+date_started.toLocaleTimeString()+"</span>");
		monrunning.prepend(newrow);
		getExperimentCreate(value.idrun);
		newrow.find(".treatments").html(getTreatmentsHTML(value.exposures, value.completed));

		newrow.find(".chart.icon").on("click", function(){
			var id = newrow.attr("id");
			displayChart(id.substr(0,id.indexOf("-")));
		});
		newrow.find(".chart.icon").css( 'cursor', 'pointer' );

		newrow.find(".ui.mini.statistic.exposures").on("click", function(){
			var id = newrow.attr("id");
			displayParamValues(id.substr(0,id.indexOf("-")), "exposure");
		});
		newrow.find(".ui.mini.statistic.exposures").css( 'cursor', 'pointer' );
		
		newrow.find(".ui.mini.statistic.completed").on("click", function(){
			var id = newrow.attr("id");
			displayParamValues(id.substr(0,id.indexOf("-")), "completed");
			});
		newrow.find(".ui.mini.statistic.completed").css( 'cursor', 'pointer' );
	}
	
	function displayChart(id){
		$(".ui.modal._chart").find(".header").empty();
		$(".ui.modal._chart").find(".content").empty();
		$(".ui.modal._chart").find(".content").append("<canvas id='chart' width='400' height='300'>");
		var errorMessage = "<p>There was an error loading data from experiment +"+id+"</p>";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: monitorSubtreatURL+"/"+id,
			  success: displayChartSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	function displayChartSuccess(data){
		
		var ctx = document.getElementById("chart").getContext('2d');
		var exposures = new Object();
		var completed = new Object();
		$.each(data, function (index, event){
			if (event.ename == "exposure"){
				$.each(event.treatments, function (index2, treatexp){
					exposures[treatexp.name] = treatexp.value;
				});
			} else if (event.ename == "completed"){
				$.each(event.treatments, function (index2, treatexp){
					completed[treatexp.name] = treatexp.value;
				});
			}
				
		});

		colors = ['#a6cee3','#1f78b4','#b2df8a','#33a02c','#fb9a99','#e31a1c','#fdbf6f','#ff7f00'];
		var datasets = new Array();
		var enames = new Array();
		var treatments = new Object();
		$.each(data, function (index, event){
			if (event.ename != "exposure" && event.ename != "completed"){
				enames.push(event.ename);
				color = 0;
				$.each(event.treatments, function (index, treatment){
					var dtreatment = treatments[treatment.name];
					var nexposures = exposures[treatment.name];
					var ncompleted = completed[treatment.name];
					var txt; 
					if (ncompleted == null)
						ncompleted = 0;
					if (nexposures == null){
						nexposures = 1;
						txt = "no exposures registered!";
					} else
						txt =  nexposures + " exp.";
					txt = txt + "/"+ncompleted+" comp.";
					if (dtreatment == null){
						dtreatment = new Object();
						dtreatment.label = treatment.name+" ("+txt+")";
						//dtreatment.backgroundColor = 'rgb(' + (Math.floor(Math.random() * 256)) + ',' + (Math.floor(Math.random() * 256)) + ',' + (Math.floor(Math.random() * 256)) + ')';;
						dtreatment.backgroundColor = colors[color%7];
						color++;
						dtreatment.data = new Array();
						datasets.push(dtreatment);
						treatments[treatment.name] = dtreatment;
					} 
					dtreatment.data.push(treatment.value/nexposures); 
				});
			}
		});
		
		$(".ui.modal._chart").modal('show');
		
		var myBubbleChart = new Chart(ctx,{
		    type: 'bar',
		    data: {
		      labels: enames,
		      datasets: datasets
		    },
		    options: {
		        barValueSpacing: 20,
                scales: {
                    xAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: 'User events'
                        }
                    }],
                    yAxes: [{
                        display: true,
                        scaleLabel: {
                            display: true,
                            labelString: "Average count per exposures"
                        },
                        ticks: {
                        	min: 0
                        }
                    }]
                },
                title: {
                    display: true,
                    text: 'Event count'
                }
            }
		});

		$(".ui.modal._chart").modal('refresh');
	}

	
	function displayParamSuccess(data){
		$(".ui.modal._info").find(".header").empty();
		$(".ui.modal._info").find(".header").append("Monitoring Experiment id "+data.idrun);
		$(".ui.modal._info").find(".content").empty();
		$.each(data.treatments, function (index, treatment){
			var body ="";
			$.each(treatment.subtreatments, function (index, subtreatment){
				body = body + "<tr> <td>"+subtreatment.params+"</td> <td>"+subtreatment.value+"</td> </tr>";
			});
			body = body + "<tr> <td>TOTAL</td> <td>"+treatment.value+"</td> </tr>";
			var header = "<h3 class='ui blue header'>"+treatment.name+"</h3>"
			var table ="<table class='ui compact blue celled table'><thead><th>Values</th><th>Exposures/Completed</th></thead><tbody>"+body+"<tbody></table>";
			$(".ui.modal._info").find(".content").append(header);
			$(".ui.modal._info").find(".content").append(table);
		});
		
		
		$(".ui.modal._info").modal('show');

	}

	
	function displayParamValues(idconf, ename){
		var errorMessage = "<p>There was an error loading data from experiment +"+$(this).attr("data")+"</p>";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: monitorSubtreatURL+"/"+idconf+"/"+ename,
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
	
	function startExperiment(idrun){
		var errorMessage = "<p>Experiment +"+idrun+" did not start.</p><p>The date or exposures might have exceeded the settings.</p>";
		$.ajax({
			  contentType: 'text/plain',
			  type: 'PUT',
			  url: startURL,
			  data: idrun,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}

	
	function stopExperiment(idrun){
		var errorMessage = "<p>Experiment "+idrun+" did not start</p>";
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