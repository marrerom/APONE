/**
 * Add Users
 */

var tabusrnew;

$(document).ready(function() {

	
	function init() {
		
		tabusrnew = $("#contents_usrnew");
		
		tabusrnew.find(".ui.button._add").on("click", function() {
			var newuser = getUser();
			addUser(newuser);
		});
		
		
		tabusrnew.find(".ui.button._clear").on("click", function(){
			tabusrnew.find(".ui.form").find("input[type=text]").val("");
		});
	}
	
	function addUser(newuser){
		var successMessage = "The user was created successfully!";
		var errorMessage = "There was an error creating the new user";
		if (checkForm()){
			$.ajax({
			      contentType: 'application/json',
				  type: 'POST',	
				  url: newUserURL,
				  data: newuser,
				  success: function(data) {
					  tabusrnew.find(".ui.button._clear").trigger("click");
					  success(successMessage + " The user unique name is: "+data); 
					  },
				  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
				});
		}
	}
	
	function getUser(){
		var newuser = new Object();
		newuser.name =  tabusrnew.find("[name=name]").val();
		newuser.idTwitter = tabusrnew.find("[name=idTwitter]").val();
		newuser.rol = tabusrnew.find("[name=rol]").find(":selected").text();
		return JSON.stringify(newuser);
	}
	
	function checkForm(){
		var correct = true;
		var emptyprompt = "can not be empty";
			
		tabusrnew.find(".ui.error.message").empty();	
		tabusrnew.find(".ui.error.message").append("<div class='header'>There are some errors</div>");
		var list ="";
		
		if (!tabusrnew.find("[name=name]").val()) {correct = false; list=list+"<li class='item'>User name "+emptyprompt+".</li>";}
		if (!tabusrnew.find("[name=idTwitter]").val()) {correct = false; list=list+"<li class='item'>Twitter identifier "+emptyprompt+".</li>";}
		
		tabusrnew.find(".ui.error.message").append("<ul class='ui list'>"+list+"</ul>");
		if (!correct){tabusrnew.find(".ui.error.message").show();} else {tabusrnew.find(".ui.error.message").hide();}
		
		return correct;
	}

	
	init();
});
