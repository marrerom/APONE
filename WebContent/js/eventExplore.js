//TODO: Support to date search in a range

var tabexpevent;
var tabexpevent_idexp = null //id of experiment configuration set in tabsearch to display events only from that experiment

$(document).ready(function() {

	const SNIPPET = 100; //number of characters of event value displayed (in case it is not binary)
	
	const tabexpevent_segment_param = "<div class='inline fields'><div class='field'><label>Parameter</label> <input name='pname' " +
			"placeholder='Param.Name' type='text'></div><div class='field'><label>Value</label> <input name='pvalue' " +
			"placeholder='Param.Value' type='text'></div><i class='_param minus inline link circle icon'></i> " +
			"<i class='_param add inline link circle icon'></i></div>";


	function initFilter() {
		var newfilter = new Object();
		return newfilter;
	}
	
	var filter= initFilter();
	
	function init(){
		
		tabexpevent = $("#contents_event");
		loadEvents(filter);
		
		tabexpevent.find("._param.minus.inline.link.circle.icon").on("click", paramminusClick);
		tabexpevent.find("._param.add.inline.link.circle.icon").on("click", paramaddClick);
		
		tabexpevent.find(".ui.button._filter").on("click", function() {
			filter = getFilter();
			loadEvents(filter);
		});

		tabexpevent.find(".ui.button._all").on("click", function() {
			filter = initFilter();
			loadEvents(filter);
		});
		tabexpevent.find(".ui.button._clear").on("click", function(){
			clear();
		});
		
		tabexpevent.find('.ui.calendar').calendar({type: 'date'});
		
		tabexpevent.find(".ui.button._remove").on("click", function() {
			tabexpevent.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").each(function(){
				var idevent = $(this).closest(".item").attr("id");
				removeEvent(idevent);
			});
		});
		
		tabexpevent.find(".ui.button._downloadcsv").on("click", function() {
			var event_list = [];
			tabexpevent.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").each(function(){
				var idevent = $(this).closest(".item").attr("id");
				event_list.push(idevent);
			});
			downloadcsv(event_list)
		});
		
		tabexpevent.find(".ui.button._downloadjson").on("click", function() {
			var event_list = [];
			tabexpevent.find(".ui.relaxed.divided.list").children().find("[type=checkbox]:checked").each(function(){
				var idevent = $(this).closest(".item").attr("id");
				event_list.push(idevent);
			});
			downloadjson(event_list)
		});
		
		tabexpevent.find(".ui.checkbox.master").find("[type=checkbox]").on("click", function(){
			if ($(this).is(":checked")){
				tabexpevent.find(".ui.checkbox.list").find("[type=checkbox]").prop("checked", true);
			} else {
				tabexpevent.find(".ui.checkbox.list").find("[type=checkbox]").prop("checked", false);
			}
		});

		
//		tabexpevent.find("[name=binary]").on("change", function(){
//			if ($(this).is(":checked")){
//				tabexpevent.find("[name=nobinary]").prop("checked", false);
//			}
//		});
		
//		tabexpevent.find("[name=nobinary]").on("change", function(){
//			if ($(this).is(":checked")){
//				tabexpevent.find("[name=binary]").prop("checked", false);
//			}
//		});
		
		var data = window.location.search;
		if (data){
			data = data.split("=")[1];
			data = decodeURIComponent(data);
			tabexpevent_idexp = $.parseJSON(data);
			tabexpevent.find("[name=idconfig]").val(tabexpevent_idexp);
			loadEvents(getFilter());
			tabexpevent_idexp=null;
		}

//		tabexpevent.on("menu_selection", function(){
//			if (tabexpevent_idexp){
//				clear();
//				tabexpevent.find("[name=idconfig]").val(tabexpevent_idexp);
//				loadEvents(getFilter());
//				tabexpevent_idexp=null;
//				}
//		});
	}
	
	function clear(){
		tabexpevent.find(".ui.form").find("input[type=text]").val("");
		tabexpevent.find("#etype_none").attr("checked",true);
		//tabexpevent.find("[name=binary]").prop("checked",false);
		//tabexpevent.find("[name=nobinary]").prop("checked",false);
	}
	
	function removeItem(idevent){
		tabexpevent.find(".ui.relaxed.divided.list").find("#"+idevent).remove();
	}
	
	function removeSuccess(){
		removeItem($(this).attr("data"));
	}
	
//	function removeError(xhr, status, error){
//		$(".ui.modal._error").find(".content").empty();
//		$(".ui.modal._error").find(".content").append("<p>Event "+$(this).attr("data")+" could not be deleted</p>");
//		//$(".ui.modal._error").find(".content").append(xhr.responseText);
//		$(".ui.modal._error").modal('show');
//	}
	
	function removeEvent(idevent){
		var errorMessage = "<p>Event "+$(this).attr("data")+" could not be deleted</p>";
		$.ajax({
		  contentType: 'text/plain',
		  type: 'POST',
		  url: removeEventURL,
		  data: idevent,
		  success: removeSuccess,
		  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
		});
	}
	
	function downloadSuccess(data, textstatus, request){
		var blob = new Blob([data], {type: 'text/x-csv'});
		var url = window.URL.createObjectURL(blob);
		var filename = request.getResponseHeader("Content-Disposition").match(/filename="(.+)"/)[1];
	    var link = document.createElement('a');
	    link.href = window.URL.createObjectURL(blob);
	    link.download = filename;
	    link.click();
        window.URL.revokeObjectURL(url);
	}
	
//	function downloadError(xhr, status, error){
//		$(".ui.modal._error").find(".content").empty();
//		$(".ui.modal._error").find(".content").append("<p>There was an error downloading the events</p>");
//		//$(".ui.modal._error").find(".content").append(xhr.responseText);
//		$(".ui.modal._error").modal('show');
//	}
	
	function downloadcsv(event_list){
		var errorMessage = "<p>There was an error downloading the events</p>";
		$.ajax({
			  type: 'POST',	
			  contentType: 'application/json',
			  //dataType: 'application/octet-stream',
			  url: getCSVURL,
			  data: JSON.stringify(event_list),
			  success: downloadSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	function downloadjson(event_list){
		var errorMessage = "<p>There was an error downloading the events</p>";
		$.ajax({
			  type: 'POST',	
			  contentType: 'application/json',
			  //dataType: 'application/octet-stream',
			  url: getJSONURL,
			  data: JSON.stringify(event_list),
			  success: downloadSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
//	function searchError(xhr, status, error){
//		$(".ui.modal._error").find(".content").empty();
//		$(".ui.modal._error").find(".content").append("<p>There was an error loading the events</p>");
//		//$(".ui.modal._error").find(".content").append(xhr.responseText);
//		$(".ui.modal._error").modal('show');
//	}
	
	function searchSuccess(data){
		tabexpevent.find(".ui.relaxed.divided.list").empty();
		$.each(data, function (index, value) {
			var timestamp = new Date(value.timestamp);
			var evalue = "[Binary content]";
			if (value.etype == "STRING")
				evalue = value.evalue;
			else if (value.etype == "JSON"){
				var valueobj = JSON.parse(value.evalue);
				evalue = JSON.stringify(valueobj);
		    	var len = evalue.length;
		    	if ( len > SNIPPET) len = SNIPPET; 
		    	evalue = evalue.substring(0, len);
			}
				
			tabexpevent.find(".ui.relaxed.divided.list").append("<div class='item' id='"+value._id+"'>" +
			"<div class='ui checkbox list'><div class='content'><input name='example' type='checkbox'> <label><a class='header'>" +
			"<spam>"+value.ename+"</spam>@<spam>"+value.experimenter+"</spam>  - " +
			"<spam>"+value.etype+"</spam></a></label></div>" +
			"<div class='description'>"+evalue+"</div></div></div>");
	    });
		
		tabexpevent.find(".ui.relaxed.divided.list").find(".header").on("click", function(){
			var idevent = $(this).closest(".item").attr("id");
			displayInfo(idevent);
		});
	}
	
	function loadEvents(filter){
		var errorMessage = "<p>There was an error loading the events</p>";
		$.ajax({
			  type: 'POST',	
			  contentType: 'application/json',
			  dataType: "json",
			  url: eventSearchURL,
			  data: JSON.stringify(filter),
			  success: searchSuccess,
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	

	function displaySuccess(data){
		$(".ui.modal._eventinfo").find("[name=idevent]").text(data._id);
		var timestamp = new Date(data.timestamp);
		$(".ui.modal._eventinfo").find("[name=timestamp]").val(timestamp.toLocaleDateString()+" "+timestamp.toLocaleTimeString());
		$(".ui.modal._eventinfo").find("[name=idconfig]").val(data.idconfig);
		$(".ui.modal._eventinfo").find("[name=idunit]").val(data.idunit);
		$(".ui.modal._eventinfo").find("[name=ename]").val(data.ename);
		$(".ui.modal._eventinfo").find("[name=treatment]").val(data.treatment);
		$(".ui.modal._eventinfo").find("[name=paramvalues]").val(JSON.stringify(data.paramvalues));
		$(".ui.modal._eventinfo").find("[name=etype]").val(data.etype);
		$(".ui.modal._eventinfo").find("[name=agent]").val(data.useragent);
		$(".ui.modal._eventinfo").find("[name=experimenter]").val(data.experimenter);
		
		if (data.etype == "BINARY"){
			$(".ui.modal._eventinfo").find("[name=evalue]").empty();
			$(".ui.modal._eventinfo").find("[name=evalue]").prop('disabled', true);
			$(".ui.modal._eventinfo").find("#etype_binary").attr("checked",true);
		} else if (data.etype == "JSON"){
			$(".ui.modal._eventinfo").find("[name=evalue]").prop('disabled', false);
			var valueobj = JSON.parse(data.evalue); //it is sent from IREPlatform as string, even if it is binary or json
			$(".ui.modal._eventinfo").find("[name=evalue]").val(JSON.stringify(valueobj));
			$(".ui.modal._eventinfo").find("#etype_json").attr("checked",true);
		} else {
			$(".ui.modal._eventinfo").find("[name=evalue]").prop('disabled', false);
			$(".ui.modal._eventinfo").find("[name=evalue]").val(data.evalue);
			$(".ui.modal._eventinfo").find("#etype_string").attr("checked",true);
		}
		$(".ui.modal._eventinfo").modal('show');
	}
	
	
	function displayInfo(idevent){
		var errorMessage = "Error displaying event information, event "+idevent;
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: getEventURL +"/"+ idevent,
			  success: displaySuccess,
			  error: function(xhr, status, error) {consoleError(xhr, errorMessage);}
			});
	}
	
	function getFilter(){
		filter = initFilter();
		if (tabexpevent.find("[name=timestamp]").val()) {filter.timestamp = tabexpevent.find("[name=timestamp]").val();} 
		if (tabexpevent.find("#etype_string").prop("checked")) {filter.etype = tabexpevent.find("#etype_string").val();}
		else if (tabexpevent.find("#etype_json").prop("checked")) {filter.etype = tabexpevent.find("#etype_json").val();}
		else if (tabexpevent.find("#etype_binary").prop("checked")) {filter.etype = tabexpevent.find("#etype_binary").val();}
		if (tabexpevent.find("[name=ename]").val()) {filter.ename = tabexpevent.find("[name=ename]").val();} 
		if (tabexpevent.find("[name=idunit]").val()) {filter.idunit = tabexpevent.find("[name=idunit]").val();} 
		if (tabexpevent.find("[name=_id]").val()) {filter._id = tabexpevent.find("[name=_id]").val();} 
		if (tabexpevent.find("[name=idconfig]").val()) {filter.idconfig = tabexpevent.find("[name=idconfig]").val();} 
		if (tabexpevent.find("[name=evalue]").val()) {filter.evalue = tabexpevent.find("[name=evalue]").val();}
		if (tabexpevent.find("[name=treatment]").val()) {filter.treatment = tabexpevent.find("[name=treatment]").val();}
		if (tabexpevent.find("[name=agent]").val()) {filter.useragent = tabexpevent.find("[name=agent]").val();}
		if (tabexpevent.find("[name=experiment]").val()) {filter.experiment = tabexpevent.find("[name=experiment]").val();}

		paramvalues = new Object();
		
		tabexpevent.find(".ui.fluid.segment._params").find(".fields").each(function(){
			var name = $(this).find("[name=pname]").val();
			var value = $(this).find("[name=pvalue]").val();
			if (name && value){
				paramvalues[name]=value;
			}
		});
		
		filter.paramvalues = paramvalues;
		
		return filter;
	}
	
	function paramminusClick(){
		if (tabexpevent.find(".ui.fluid.segment._params").children().length > 1)
			$(this).parents(".fields").remove();
	}
	
	function paramaddClick(){
		tabexpevent.find(".ui.fluid.segment._params").append(tabexpevent_segment_param);
		tabexpevent.find(".ui.fluid.segment._params").children().last().find("._param.add.inline.link.circle.icon").on("click", paramaddClick);
		tabexpevent.find(".ui.fluid.segment._params").children().last().find("._param.minus.inline.link.circle.icon").on("click", paramminusClick);
	}
	
	init();
	
	
});