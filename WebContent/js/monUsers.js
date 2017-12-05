/**
 * Experiment Monitoring
 */
var tabmonusr;

$(document).ready(function() {
	
	const REFRESH=30000; //update after time in milliseconds
	
	const row = "<tr>" +
			"<td><span class='name'>User name</span></td>" +
			"<td><span class='rol'>Rol</span></td>" +
			"<td><span class='created'>Exp. Created</span></td>" +
			"<td><span class='participated'>Exp. Participated</span></td>" +
			"<td><span class='left'>Left (running) experiments </span></td>" +
			"</tr>";
	
	var monusr;
	
	function init(){
		tabmonusr = $("#contents_monusr");
		monusr = tabmonusr.find(".ui.compact.blue.celled.table._users > tbody");
		refresh();
		setInterval(function(){ refresh(); }, REFRESH); 
		
		tabmonusr.find(".ui.button._assign").on("click", function() {
			assign();
		});

	}
	
	function refresh(){
		getUsersInfo();
	}

	
	function getUsersSuccess(data){
		$.each(data, function (index, value) {
			var existing = monusr.find("#"+value._id);
			var newrow = row;
			if (existing.length != 0) {
				newrow = $(existing[0]);
			}else{
				newrow = $(newrow).attr("id", value._id);
				monusr.prepend(newrow);
			}
			newrow.find(".name").text(value.idname);
			newrow.find(".rol").text(value.rol);
			newrow.find(".participated").text(value.nparticipated); //TODO: only count
			newrow.find(".created").text(value.ncreated); //TODO:only count
			newrow.find(".left").text(value.nleft); //TODO:only count
		});
	}
	
		
	function getUsersInfo(){
		var errorMessage = "There was an error loading the users experiments";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: monitorUsersURL,
			  success: getUsersSuccess,
			  error: function(xhr, status, error) {consoleError(xhr, errorMessage);}
			});
	}
	
	function assign(){
		var errorMessage = "There was an error during the assignment";
		$.ajax({
			  type: 'GET',	
			  dataType: "text",
			  url: assignmentURL,
			  success: function(data){window.location.href = data;},
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	
	init();
});