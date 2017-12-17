/**
 * New experiment methods
 */


var tabnewexp;
var tabnewexp_existingexp=null; //experiment -not configuration- identifier to save a new configuration of an existing experiment

$(document).ready(function() {
	
	const tabnewexp_segment_treatment = "<div class='inline fields'><div class='required field'><label>Variant</label> <input name='tname' placeholder='Variant.Name' " +
	"type='text' title='Variant name'></div><div class='four wide field'><label>Description</label> <input name='tdesc' placeholder='Variant.Description' type='text' title='Description of this variant'></div>" +
	"<div class='required six wide field'><label>Client URL</label> 	 <input name='turl' placeholder='Valid URL' type='text' title='URL where the variant is located'></div><div class='field'>" +
	"<div class='ui checkbox'><input tabindex='0' name='tcontrol' type='checkbox' title='Check if this is the control variant'><label>Is Control</label></div><i class='_treat minus inline link circle icon'></i>" +
	"<i class='_treat add inline link circle icon'></i></div>";

	const tabnewexp_segment_config ="<div class='inline fields'><div class='required field'><label>Variant</label> <select class='_treatment ui dropdown'>" +
	"<option value=''>Select Variant</option></select></div><div class='required field'><label>Units (%)</label>" +
	"<input name='percentage' placeholder='Percentage' type='text' title='Percentage of different experimental units exposed to this variant'></div><i class='_conf minus inline link circle icon'></i>" +
	"<i class='_conf add inline link circle icon'></i></div>";

	function init(){
		tabnewexp = $("#contents_expnew");
		//$(".nexp.treatment.ui.dropdown").on("click", treatdropboxClick);
		tabnewexp.find("[name='tname']").on("change", updateTreatSelection);
		tabnewexp.find("._treat.minus.inline.link.circle.icon").on("click", treatminusClick);
		tabnewexp.find("._treat.add.inline.link.circle.icon").on("click", treataddClick);
		tabnewexp.find("._conf.minus.inline.link.circle.icon").on("click", confminusClick);
		tabnewexp.find("._conf.add.inline.link.circle.icon").on("click", confaddClick);
		tabnewexp.find(".ui.button._add").on("click", addExperiment);
		tabnewexp.find(".ui.calendar._toend").calendar();
		tabnewexp.find(".ui.button._clear").on("click", function(){
			tabnewexp.find(".ui.form").find("input[type=text]").val("");
			tabnewexp.find("[name=tcontrol]").prop("checked",false);
			tabnewexp.find("[name=treatdropdown]").val("");
			tabnewexp.find(".ui.fluid.segment._treatments").empty();
			tabnewexp.find(".ui.fluid.segment._treatments").append(tabnewexp_segment_treatment);
			tabnewexp.find(".ui.segment._distributions").empty();
			tabnewexp.find(".ui.segment._distributions").append(tabnewexp_segment_config);
			tabnewexp.find("._treat.minus.inline.link.circle.icon").on("click", treatminusClick);
			tabnewexp.find("._treat.add.inline.link.circle.icon").on("click", treataddClick);
			tabnewexp.find("._conf.minus.inline.link.circle.icon").on("click", confminusClick);
			tabnewexp.find("._conf.add.inline.link.circle.icon").on("click", confaddClick);

			unblock();
		});
		
		getExperimenter();
		
		var data = window.location.search;
		if (data){
			data = data.split("=")[1];
			data = decodeURIComponent(data);
			tabnewexp_existingexp = $.parseJSON(data);
			block();
		}
		
		
		//$("#contents_expnew").on("menu_selection", function(){
			//if (tabnewexp_existingexp){block();}
		//});
		
	}
	
	function block(){
		var data= tabnewexp_existingexp;
		tabnewexp.find("[name=name]").val(data.name);
		tabnewexp.find("[name=experimenter]").val(data.experimenter);
		tabnewexp.find("[name=description]").val(data.description);
		tabnewexp.find(".ui.fluid.segment._treatments").empty();
		$.each(data.treatment, function (index, value) {
			tabnewexp.find(".ui.fluid.segment._treatments").append(tabnewexp_segment_treatment);
			tabnewexp.find(".ui.fluid.segment._treatments").children().last().find("[name=tname]").val(value.name);
			tabnewexp.find(".ui.fluid.segment._treatments").children().last().find("[name=tdesc]").val(value.description);
			tabnewexp.find(".ui.fluid.segment._treatments").children().last().find("[name=turl]").text(value.definition);
			if (value.control){tabnewexp.find(".ui.fluid.segment._treatments").children().last().find("[name=tcontrol]").prop("checked",true);}
		});
		updateTreatSelection();
	
		tabnewexp.find("[name=name]").prop('disabled', true);
		tabnewexp.find("[name=experimenter]").prop('disabled', true);
		tabnewexp.find("[name=description]").prop('disabled', true);
		tabnewexp.find("[name=tname]").prop('disabled', true);
		tabnewexp.find("[name=tdesc]").prop('disabled', true);
		tabnewexp.find("[name=turl]").prop('disabled', true);
		tabnewexp.find("[name=tcontrol]").prop('disabled', true);
	}
	
	function unblock(){
		tabnewexp.find("[name=name]").prop('disabled', false);
		tabnewexp.find("[name=experimenter]").prop('disabled', false);
		tabnewexp.find("[name=description]").prop('disabled', false);
		tabnewexp.find("[name=tname]").prop('disabled', false);
		tabnewexp.find("[name=tdesc]").prop('disabled', false);
		tabnewexp.find("[name=turl]").prop('disabled', false);
		tabnewexp.find("[name=tcontrol]").prop('disabled', false);
		tabnewexp_existingexp=null;
		getExperimenter();
	}

	
	function updateTreatSelection(){
		tabnewexp.find("._treatment.ui.dropdown").each(function(){
			var dbox = $(this);
			var selected = dbox.val();
			dbox.empty();
			dbox.append("<option value=''>Select Treatment</option>");
			tabnewexp.find(".ui.fluid.segment._treatments").find("[name='tname']").each(function(){
				if ($(this).val())
				dbox.append("<option value='"+$(this).val()+"'>"+$(this).val()+"</option>");
			});
			dbox.val(selected);
		});
	}

	function treatminusClick(){
		if (tabnewexp.find(".ui.fluid.segment._treatments").children().length > 1)
			$(this).parents(".fields").remove();
			updateTreatSelection();
	}
	
	function treataddClick(){
		tabnewexp.find(".ui.fluid.segment._treatments").append(tabnewexp_segment_treatment);
		tabnewexp.find(".ui.fluid.segment._treatments").children().last().find("._treat.add.inline.link.circle.icon").on("click", treataddClick);
		tabnewexp.find(".ui.fluid.segment._treatments").children().last().find("._treat.minus.inline.link.circle.icon").on("click", treatminusClick);
		tabnewexp.find(".ui.fluid.segment._treatments").children().last().find("[name='tname']").on("change", updateTreatSelection);
	}
	
	function confminusClick(){
		if (tabnewexp.find(".ui.segment._distributions").children().length > 1)
			$(this).parents(".fields").remove();
	}
	
	function confaddClick(){
		tabnewexp.find(".ui.segment._distributions").append(tabnewexp_segment_config);
		tabnewexp.find(".ui.segment._distributions").children().last().find("._conf.add.inline.link.circle.icon").on("click", confaddClick);
		tabnewexp.find(".ui.segment._distributions").children().last().find("._conf.minus.inline.link.circle.icon").on("click", confminusClick);
		updateTreatSelection();
	}
	
//	function newexpError(xhr){
//		$(".ui.modal._error").find(".content").empty();
//		$(".ui.modal._error").find(".content").append("<p>The experiment was not saved</p>");
//		//$(".ui.modal._error").find(".content").append(xhr.responseText);
//		$(".ui.modal._error").modal('show');
//	}
	
//	function newexpSuccess(data){
//		$(".ui.modal._success").find(".content").empty();
//		$(".ui.modal._success").find(".content").append("<p>The experiment has been saved successfully</p>");
//		$(".ui.modal._success").modal('show');
//	}
//	
//	function newconfSuccess(data){
//		unblock();
//		$(".ui.modal._success").find(".content").empty();
//		$(".ui.modal._success").find(".content").append("<p>The experiment has been saved successfully</p>");
//		$(".ui.modal._success").modal('show');
//	}
	
	function addExperiment(){
		var successMessage = "<p>The experiment has been saved successfully</p>";
		var errorMessage = "<p>The experiment was not saved</p>";
		if (checkForm()){
			if (!tabnewexp_existingexp){
				var formjson = getFormJSON();
				$.ajax({
					contentType: 'application/json',
				    type: 'POST',
				    url: newexpURL,
				    data: formjson,
				    success: function() {tabnewexp.find(".ui.button._clear").trigger("click"); success(successMessage);},
				    error: function(xhr, status, error) {alertError(xhr, errorMessage);}
				    });
			} else {
				var inputJson = getNewConfJSON();
				//var fd = new FormData();    
				//fd.append( 'idexp', tabnewexp_existingexp._id);
				//fd.append('configuration', formjson);
				$.ajax({
					contentType: 'application/json',
					//processData: false,
				    type: 'POST',
				    url: newconfURL,
				    data: inputJson,
				    success: function() {tabnewexp.find(".ui.button._clear").trigger("click");success(successMessage); },
				    error: function(xhr, status, error) {alertError(xhr, errorMessage);}
				    });
			}
		}
	}
	
	function getExperimenter(){
		$.ajax({
			contentType: 'text/plain',
		    type: 'GET',
		    url: getUserURL,
		    success: function(user) {if (user) {tabnewexp.find("[name='experimenter']").val(user); tabnewexp.find("[name='experimenter']").prop('disabled',true)}},
		    });
	}
	
	function getNewConfJSON(){
		var idexp = tabnewexp_existingexp._id;
		var json = new Object();
		json.idexp = idexp;
		json.configuration = getConf();
		return JSON.stringify(json);
	}
	
	function getConf(){
		var config = new Object();
		config.name =  tabnewexp.find("[name='cname']").val();
		config.experimenter = tabnewexp.find("[name=experimenter]").val();
		var date_to_end = tabnewexp.find("[name='date_toend']").val();
		if (date_to_end)
			config.date_to_end = date_to_end;
			//config.date_to_end = new Date(date_to_end).toISOString();
		var max_exposures = tabnewexp.find("[name='maxexp']").val();
		if (max_exposures)
			config.max_exposures = parseInt(max_exposures); 
		config.distribution = [];
		
		tabnewexp.find(".ui.segment._distributions").find(".fields").each(function(){
			var dist = new Object();
			dist.treatment = $(this).find("._treatment.ui.dropdown").find(":selected").text();
			dist.segments = parseInt($(this).find("[name='percentage']").val(),10);
			config.distribution.push(dist);
		});
		return config;
	}
	
	function getTreatment(item){
		var treatment = new Object();
		treatment.name = item.find("[name='tname']").val();
		var treatdesc = item.find("[name='tdesc']").val();
		if (treatdesc)
			treatment.description = treatdesc; 
		treatment.definition = item.find("[name='tdef']").val();
		treatment.url = item.find("[name='turl']").val();
		treatment.control = item.find("[name='tcontrol']").is(":checked");
		return treatment;
	}
	
	function getFormJSON(){
		var form = new Object();
		form.name = tabnewexp.find("[name='name']").val();
		form.experimenter = tabnewexp.find("[name='experimenter']").val();
		var expdesc = tabnewexp.find("[name='description']").val();
		if (expdesc)
			form.description = expdesc; 
		form.treatment = [];
		form.config = [];
		
		tabnewexp.find(".ui.fluid.segment._treatments").find(".fields").each(function(){
			form.treatment.push(getTreatment($(this)));
		});
		
		form.config.push(getConf());
		return JSON.stringify(form);
	}
	
	function checkForm(){
		var correct = true;
		var emptyprompt = "can not be empty";
		var numberprompt = "has to be a number";
		var percentprompt = "has to be a percentage: [0-100]";
		var uniqueprompt ="has to be unique";
			
		tabnewexp.find(".ui.error.message").empty();	
		tabnewexp.find(".ui.error.message").append("<div class='header'>There are some errors</div>");
		var list ="";
		
		if (!tabnewexp.find("[name=name]").val()) {correct = false; list=list+"<li class='item'>Experiment name "+emptyprompt+".</li>";}
		if (!tabnewexp.find("[name=cname]").val()) {correct = false; list=list+"<li class='item'>Configuration name "+emptyprompt+".</lit>";}
		if (tabnewexp.find("[name=maxexp]").val() && !$.isNumeric(tabnewexp.find("[name=maxexp]").val())) {correct = false; list=list+"<li class='item'>Number of exposures "+numberprompt+".</li>";}
		
		var tnames = [];
		tabnewexp.find("[name=tname]").each(function(){
			if (!$(this).val()){correct = false;list=list+"<li class='item'>Variant name "+emptyprompt+".</li>";}
			else if (tnames.includes($(this).val())) {correct = false; list=list+"<li class='item'>Variant name "+uniqueprompt+".</li>";} 
			else {tnames.push($(this).val())}
		});
		
		var checked = false;
		tabnewexp.find("[name='tcontrol']").each(function(){
			if ($(this).is(":checked")){
				if (checked){correct = false; list=list+"<li class='item'>Treatment control "+uniqueprompt+".</li>";}
				checked = true;
			}
		})
		if (!checked){correct = false;list=list+"<li class='item'>Treatment control "+emptyprompt+".</li>";}
	
		tabnewexp.find("[name=turl]").each(function(){
			if (!$(this).val()){correct = false;list=list+"<li class='item'>Treatment url "+emptyprompt+".</li>"}
		});
		tabnewexp.find("[name=treatdropdown]").each(function(){
			if (!$(this).val()){correct = false;list=list+"<li class='item'>Treatment selection in configuration "+emptyprompt+".</li>"}
		});
		tabnewexp.find("[name=percentage]").each(function(){
			if (!$.isNumeric($(this).val())){correct = false;list=list+"<li class='item'>Variant distribution in configuration "+emptyprompt+" and "+numberprompt+".</li>";}
			else if ($(this).val() < 0 || $(this).val() > 100) {correct = false;list=list+"<li class='item'>Variant distribution in configuration "+percentprompt+".</li>";}
		});
		
		total = 0;
		var treatDist = [];
		tabnewexp.find(".ui.segment._distributions").find(".fields").each(function(){
			var element = $(this).find("._treatment.ui.dropdown").find(":selected").text();
			if (treatDist.indexOf(element) === -1) {
		        treatDist.push(element);
			} else {
				correct = false; list=list+"<li class='item'>Variant in the distribution "+uniqueprompt+"</li>";
			}
			
			total = total + parseInt($(this).find("[name='percentage']").val(),10);
		});
		if (total > 100){
			correct = false; list=list+"<li class='item'>The sum of the distribution percentages exceed 100</li>";
		}
		
		tabnewexp.find(".ui.error.message").append("<ul class='ui list'>"+list+"</ul>");
		if (!correct){tabnewexp.find(".ui.error.message").show();} else {tabnewexp.find(".ui.error.message").hide();}
		
		return correct;
	}

	
	init();
	
		
	
});