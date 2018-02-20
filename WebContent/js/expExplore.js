//TODO: Support to date search in a range

/**
 * Experiment search menu methods
 */


var tabexpsearch;

$(document).ready(function() {

	const tabexpsearch_item_treatment = "<div class='item'><div class='ui divider'></div>" +
			"<div class='inline fields'>" +
			"<div class='field'><h4 class='ui blue left floated header'>Variant:</h4><span name='tname'>Variant name</span></div>" +
			"<div class='field'><h4 class='ui blue left floated header'>Client URL:</h4><span name='turl'>Client URL</span></div>" +
			"<div class='field'><div class='ui checkbox'><input tabindex='0' name='tcontrol' type='checkbox' disabled><label>Is Control</label></div></div>" +
			"</div>" +
			"<div class='inline fields'><div class='field'>" +
			"<h4 class='ui blue left floated header'>Description:</h4><span name='tdesc'>Variant description</span></div>" +
			"</div>" +
			"<div class='inline fields'>" +
			"<div class='field'><h4 class='ui blue left  floated header'>Definition:</h4><div class='container' name='tdef'></div></div>" +
			"</div>" +
			"<div class='ui divider'></div></div>";

	const tabexpsearch_item_config = "<a class='ui blue large label'><span name='dname'>Variant</span><div class='detail'><span name='percentage'>0</span>%</div></a>";


	function initFilter() {
		var newfilter = new Object();
		newfilter.treatment = [];
		newfilter.config = [];
		return newfilter;
	}
	
	var filter= initFilter();
	
	function init(){
		tabexpsearch = $("#contents_expsearch");
		loadExperiments(filter);
		tabexpsearch.find(".ui.button._filter").on("click", function() {
			filter = getFilter();
			loadExperiments(filter);
		});
		tabexpsearch.find(".ui.button._all").on("click", function() {
			filter = initFilter();
			loadExperiments(filter);
		});
		
		tabexpsearch.find(".ui.button._clear").on("click", function(){
			tabexpsearch.find(".ui.form").find("input[type=text]").val("");
			tabexpsearch.find("[name=run]").prop("checked",false);
			tabexpsearch.find("[name=norun]").prop("checked",false);
		});
		
		tabexpsearch.find(".ui.button._start").on("click", function(){
			tabexpsearch.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").each(function(){
				var idrun = $(this).closest(".item").attr("id");
				startExperiment(idrun);
			});
		});
		
		tabexpsearch.find(".ui.button._stop").on("click", function(){
			tabexpsearch.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").each(function(){
				var idrun = $(this).closest(".item").attr("id");
				stopExperiment(idrun);
			});
		});
		
		tabexpsearch.find(".ui.button._test").on("click", function(){
			tabexpsearch.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").each(function(){
				var idrun = $(this).closest(".item").attr("id");
				test(idrun);
			});
		});
		
		tabexpsearch.find(".ui.button._remove").on("click", function() {
			tabexpsearch.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").each(function(){
				var idrun = $(this).closest(".item").attr("id");
				$(".ui.modal._info").find(".content").empty();
				$(".ui.modal._info").find(".content").append("<p>You are about to remove experiment "+idrun+"</p>");
				$(".ui.modal._info").find(".content").append("<p>The associated events will be removed. Continue?</p>");
				$(".ui.modal._info").find(".content").append("<div class='actions'><div class='ui approve button'>Yes</div><div class='ui cancel button'>No</div></div>");
				$(".ui.modal._info").modal('setting', {
					onApprove : function() {
						removeExperiment(idrun);
						}
					});
			
				$(".ui.modal._info").modal('show');
			});
			 
		});
		
		tabexpsearch.find(".ui.button._new").on("click", function(){
			var idrun = tabexpsearch.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").first().closest(".item").attr("id");
			if (idrun){	newConfig(idrun);}
		});

		
		tabexpsearch.find(".ui.button._newadv").on("click", function(){
			var idrun = tabexpsearch.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").first().closest(".item").attr("id");
			if (idrun){	newConfigAdv(idrun);}
		});

		
		tabexpsearch.find(".ui.button._events").on("click", function(){
			var idrun = tabexpsearch.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").first().closest(".item").attr("id");
			if (idrun){	
				tabexpevent_idexp= idrun; 
				$("#menu_event").trigger("click", [idrun]);
			}
		});
		
		tabexpsearch.find("[name=run]").on("change", function(){
			if ($(this).is(":checked")){
				tabexpsearch.find("[name=norun]").prop("checked", false);
			}
		});
		
		tabexpsearch.find("[name=norun]").on("change", function(){
			if ($(this).is(":checked")){
				tabexpsearch.find("[name=run]").prop("checked", false);
			}
		});
		
		
		tabexpsearch.find(".ui.checkbox.master").find("[type=checkbox]").on("click", function(){
			if ($(this).is(":checked")){
				tabexpsearch.find(".ui.checkbox.list").find("[type=checkbox]").prop("checked", true);
			} else {
				tabexpsearch.find(".ui.checkbox.list").find("[type=checkbox]").prop("checked", false);
			}
		});
		
		tabexpsearch.find('.ui.calendar._toend').calendar({type: 'date'});
		tabexpsearch.find('.ui.calendar._ended').calendar({type: 'date'});
		tabexpsearch.find('.ui.calendar._started').calendar({type: 'date'});
	}
	
	function newConfigSuccess(data){
		$("#menu_expnew").trigger("click", [data]);		
	}
	
	function newConfigAdvSuccess(data){
		$("#menu_expnew_adv").trigger("click", [data]);		
	}
	
	function newConfig(idrun){
		var errorMessage = "<p>There was an error loading the experiment "+idrun+"</p>";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: getExperimentURL +"/"+ idrun,
			  success: newConfigSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	function newConfigAdv(idrun){
		var errorMessage = "<p>There was an error loading the experiment "+idrun+"</p>";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: getExperimentURL +"/"+ idrun,
			  success: newConfigAdvSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	function getFilter(){
		filter = initFilter();
		configitem = {};
		treatitem = {};
		if (tabexpsearch.find("[name=identifier]").val()) {configitem._id = tabexpsearch.find("[name=identifier]").val();} 
		if (tabexpsearch.find("[name=name]").val()) {filter.name = tabexpsearch.find("[name=name]").val();} 
		if (tabexpsearch.find("[name=experimenter]").val()) {filter.experimenter = tabexpsearch.find("[name=experimenter]").val();}
		if (tabexpsearch.find("[name=description]").val()) {filter.description = tabexpsearch.find("[name=description]").val();}
		if (tabexpsearch.find("[name=unit]").val()) {filter.unit = tabexpsearch.find("[name=unit]").val();}
		if (tabexpsearch.find("[name=tname]").val()) {treatitem.name = tabexpsearch.find("[name=tname]").val();}
		if (tabexpsearch.find("[name=tdesc]").val()) {treatitem.description = tabexpsearch.find("[name=tdesc]").val();}
		if (tabexpsearch.find("[name=tdef]").val()) {treatitem.definition = tabexpsearch.find("[name=tdef]").val();}
		if (tabexpsearch.find("[name=turl]").val()) {treatitem.url = tabexpsearch.find("[name=turl]").val();}
		if (tabexpsearch.find("[name=cname]").val()) {configitem.name = tabexpsearch.find("[name=cname]").val();}
		if (tabexpsearch.find("[name=controller]").val()) {configitem.controller_code = tabexpsearch.find("[name=controller]").val();}
		if (tabexpsearch.find("[name=run]").is(":checked")) {configitem.run = "ON";}
		if (tabexpsearch.find("[name=norun]").is(":checked")) {configitem.run = "OFF";}
		if (tabexpsearch.find("[name=date_started]").val()) {configitem.date_started = []; configitem.date_started.push(tabexpsearch.find("[name=date_started]").val());}
		if (tabexpsearch.find("[name=date_ended]").val()) {configitem.date_ended = []; configitem.date_ended.push(tabexpsearch.find("[name=date_ended]").val());}
		if (tabexpsearch.find("[name=date_toend]").val()) {configitem.date_to_end = tabexpsearch.find("[name=date_toend]").val();}
		filter.config.push(configitem);
		filter.treatment.push(treatitem);
		return filter;
	}
	
	function switchON(idrun){
		tabexpsearch.find(".ui.relaxed.divided.list").find("#"+idrun).find("[name=run]").text("ON");
	}
	
	function switchOFF(idrun){
		tabexpsearch.find(".ui.relaxed.divided.list").find("#"+idrun).find("[name=run]").text("OFF");
	}
	
	function removeItem(idrun){
		tabexpsearch.find(".ui.relaxed.divided.list").find("#"+idrun).remove();
	}
	
	function searchSuccess(data){
		tabexpsearch.find(".ui.relaxed.divided.list").empty();
		$.each(data, function (index, value) {
			if (!value.description)
				value.description = "No description";
			tabexpsearch.find(".ui.relaxed.divided.list").append("<div class='item' id='"+value.idrun+"'>" +
			"<div class='ui checkbox list'><div class='content'><input name='example' type='checkbox'> <label><a class='header'>" +
			"<spam name='experimenter'>"+value.name+"</spam>:<spam name='cname'>"+value.cname+"</spam>@<spam name='name'>"+value.experimenter+" - " +
			"Status: <spam name='run'>"+value.run+"</spam> - <span name='idrun'>"+value.idrun+"</span></a></label></div>" +
			"<div class='description' name='description'>"+value.description+"</div></div></div>");
	    });
		
		tabexpsearch.find(".ui.relaxed.divided.list").find(".header").on("click", function(){
			var idrun = $(this).closest(".item").attr("id");
			displayInfo(idrun);
		});
	}
	
	function loadExperiments(filter){
		var errorMessage = "<p>There was an error loading the experiments</p>";
		$.ajax({
			  type: 'POST',	
			  contentType: 'application/json',
			  dataType: "json",
			  url: expsearchURL,
			  data: JSON.stringify(filter),
			  success: searchSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	function startSuccess(){
		switchON($(this).attr("data"));
	}

	
	function startExperiment(idrun){
		var errorMessage = "<p>Experiment "+idrun+" did not start.</p><p>The date or exposures might have exceeded the settings.</p>"; 
		$.ajax({
			  contentType: 'text/plain',
			  type: 'PUT',
			  url: startURL,
			  data: idrun,
			  success: startSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	function removeSuccess(){
		removeItem($(this).attr("data"));
	}
	
	function removeExperiment(idrun){
		var errorMessage = "<p>Experiment "+idrun+" could not be deleted</p>";
		$.ajax({
		  contentType: 'text/plain',
		  type: 'POST',
		  url: removeURL,
		  data: idrun,
		  success: removeSuccess,
		  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
		});
	}

	
	function stopSuccess(){
		switchOFF($(this).attr("data"));
	}

	
	function stopExperiment(idrun){
		var errorMessage = "<p>Experiment "+idrun+" did not stop</p>";
		$.ajax({
			  contentType: 'text/plain',
			  type: 'PUT',
			  url: stopURL,
			  data: idrun,
			  success: stopSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	function displaySuccess(data){
		var config = data.config[0];
		$(".ui.modal._experimentinfo").find ("[name=idexp]").text(config._id);
		$(".ui.modal._experimentinfo").find("[name=experimenter]").text(config.experimenter);
		$(".ui.modal._experimentinfo").find("[name=name]").text(data.name);
		$(".ui.modal._experimentinfo").find("[name=unit]").text(data.unit);
		$(".ui.modal._experimentinfo").find("[name=description]").text(data.description);
		
		$(".ui.modal._experimentinfo").find(".container._treatment").empty();
		$.each(data.treatment, function (index, value) {
			$(".ui.modal._experimentinfo").find(".container._treatment").append(tabexpsearch_item_treatment);
			$(".ui.modal._experimentinfo").find(".container._treatment").children().last().find("[name=tname]").text(value.name);
			$(".ui.modal._experimentinfo").find(".container._treatment").children().last().find("[name=tdesc]").text(value.description);
			$(".ui.modal._experimentinfo").find(".container._treatment").children().last().find("[name=tdef]").text(value.definition);
			$(".ui.modal._experimentinfo").find(".container._treatment").children().last().find("[name=turl]").text(value.url);
			if (value.control) $(".ui.modal._experimentinfo").find(".container._treatment").children().last().find("[name=tcontrol]").prop('checked', true);
			
		});
		$(".ui.modal._experimentinfo").find("[name=cname]").text(config.name);
		$(".ui.modal._experimentinfo").find("[name=controller]").text(config.controller_code);
		if (config.max_exposures) $(".ui.modal._experimentinfo").find("[name=maxexp]").text(config.max_exposures);
		var date = new Date(config.date_to_end);
		if (config.max_exposures) $(".ui.modal._experimentinfo").find("[name=date_toend]").text(date.toLocaleDateString()+" "+date.toLocaleTimeString());
		
		$(".ui.modal._experimentinfo").find("[name=date_started]").empty();
		$.each(config.date_started, function (index, value) {
			var date = new Date(value);
			$(".ui.modal._experimentinfo").find("[name=date_started]").append(date.toLocaleDateString()+" "+date.toLocaleTimeString());
			$(".ui.modal._experimentinfo").find("[name=date_started]").append(" - ");
		});
		
		$(".ui.modal._experimentinfo").find("[name=date_ended]").empty();
		$.each(config.date_ended, function (index, value) {
			var date = new Date(value);
			$(".ui.modal._experimentinfo").find("[name=date_ended]").append(date.toLocaleDateString()+" "+date.toLocaleTimeString());
			$(".ui.modal._experimentinfo").find("[name=date_ended]").append(" - ");
		});

		$(".ui.modal._experimentinfo").find(".container._config").empty();
		$.each(config.distribution, function (index, value) {
			$(".ui.modal._experimentinfo").find(".container._config").append(tabexpsearch_item_config);
			$(".ui.modal._experimentinfo").find(".container._config").children().last().find("[name=dname]").text(value.treatment);
			$(".ui.modal._experimentinfo").find(".container._config").children().last().find("[name=percentage]").text(value.segments);
		});
		
		$(".ui.modal._experimentinfo").modal('show');	
	
	}
	
	
	function displayInfo(idrun){
		var errorMessage = "Error displaying experiment information, experiment "+idrun;
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: getExperimentURL +"/"+ idrun,
			  success: displaySuccess,
			  error: function(xhr, status, error) {consoleError(xhr, errorMessage);}
			});
	}
	
	
	function test(idrun){
		var url = testURL+"/"+idrun;
		window.open(url);
	}
	
	init();

});