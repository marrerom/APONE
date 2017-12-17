/*
 * General js methods 
 */

const IREPLATFORM_URI = "";
 
var newexpURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/new/experiment";
var newconfURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/new/configuration";
var eventSearchURL = IREPLATFORM_URI + "/IREPlatform/service/event/search";
var getEventURL = IREPLATFORM_URI + "/IREPlatform/service/event/get";
var removeEventURL = IREPLATFORM_URI + "/IREPlatform/service/event/delete";
var getCSVURL = IREPLATFORM_URI + "/IREPlatform/service/event/getCSV";
var getJSONURL = IREPLATFORM_URI + "/IREPlatform/service/event/getJSON";
var monitorTreatmentsURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/monitor/treatments";
var monitorSubtreatExposureURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/monitor/subtreatments/exposure";
var monitorSubtreatCompletedURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/monitor/subtreatments/completed";
var expsearchURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/search";
var startURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/start";
var stopURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/stop";
var removeURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/delete";
var removeEventsURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/deleteEvents";
var getExperimentURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/get";
var getUserURL = IREPLATFORM_URI + "/IREPlatform/service/user/authenticatedUser";
var monitorUsersURL = IREPLATFORM_URI + "/IREPlatform/service/user/monitoring";
var assignmentURL = IREPLATFORM_URI + "/IREPlatform/service/user/assignexp";
var completedURL = IREPLATFORM_URI + "/IREPlatform/service/user/completedexp";
var testURL = IREPLATFORM_URI + "/IREPlatform/service/experiment/redirect"


function alertError(xhr, errorMessage){
	var what = errorMessage;
	var detail = xhr.responseText;
	var errorType;
	var action;
	if (xhr.status == 400){
		errorType = "Bad Request error "+xhr.status;
		action = "Check input";
	} else if (xhr.status == 403){
		errorType = "Forbidden request "+xhr.status;
		action = "Check permissions";
	} else if (xhr.status == 401){
		errorType = "Unauthorized request "+xhr.status;
		action = "User unauthorized. Reload the web app in the browser and try again";
	} else {
		errorType = "Server error "+xhr.status;
		action = "Contact Admin.";
	}
	
	$(".ui.modal._error").find(".content").empty();
	$(".ui.modal._error").find(".content").append(what);
	$(".ui.modal._error").find(".content").append("<p>"+errorType +" - "+detail+"</p>");
	$(".ui.modal._error").find(".content").append("<p>"+action+"</p>");
	$(".ui.modal._error").modal('show');
}

function success(message){
	$(".ui.modal._success").find(".content").empty();
	$(".ui.modal._success").find(".content").append(message);
	$(".ui.modal._success").modal('show');
}

function consoleError(xhr, errorMessage){	
	var errorType = "Error: "+errorMessage;
	if (status == 400)
		errorType = "Bad Request error: check input";
	else if (status == 500)
		errorType = "Server error: contact admin.";
	else if (status == 401)
		errorType = "User unauthorized: try again";
	console.error(errorMessage+". "+errorType);
}


//adapted from W3SCHOOLS, javascript
function sortTable(tableClass, n) {
	var table, rows, switching, i, x, y, xsort, ysort, shouldSwitch, dir, switchcount = 0;
	// table = document.getElementById("myTable2");
	table = document.getElementsByClassName(tableClass)[0];
	switching = true;
	// Set the sorting direction to ascending:
	dir = "asc";
	/*
	 * Make a loop that will continue until no switching has been done:
	 */
	while (switching) {
		// Start by saying: no switching is done:
		switching = false;
		rows = table.getElementsByTagName("TR");
		/*
		 * Loop through all table rows (except the first, which contains table
		 * headers):
		 */
		for (i = 1; i < (rows.length - 1); i++) {
			// Start by saying there should be no switching:
			shouldSwitch = false;
			/*
			 * Get the two elements you want to compare, one from current row
			 * and one from the next:
			 */
			x = rows[i].getElementsByTagName("TD")[n];
			y = rows[i + 1].getElementsByTagName("TD")[n];
			xsort = x.getElementsByClassName("sort")[0];
			ysort = y.getElementsByClassName("sort")[0];
			if (xsort && ysort) {
				if (xsort.classList.contains("date")
						&& ysort.classList.contains("date")) {
					xsort = new Date(xsort.innerText);
					ysort = new Date(ysort.innerText);
				} else {
					xsort = xsort.innerText.toLowerCase();
					ysort = ysort.innerText.toLowerCase();
				}
			} else
				return;

			/*
			 * Check if the two rows should switch place, based on the
			 * direction, asc or desc:
			 */
			if (dir == "asc") {
				if (xsort > ysort) { // TODO: check a specific span?
					// If so, mark as a switch and break the loop:
					shouldSwitch = true;
					break;
				}
			} else if (dir == "desc") {
				if (xsort < ysort) {
					// If so, mark as a switch and break the loop:
					shouldSwitch = true;
					break;
				}
			}
		}
		if (shouldSwitch) {
			/*
			 * If a switch has been marked, make the switch and mark that a
			 * switch has been done:
			 */
			rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
			switching = true;
			// Each time a switch is done, increase this count by 1:
			switchcount++;
		} else {
			/*
			 * If no switching has been done AND the direction is "asc", set the
			 * direction to "desc" and run the while loop again.
			 */
			if (switchcount == 0 && dir == "asc") {
				dir = "desc";
				switching = true;
			}
		}
	}
}


 
 $(document).ready(function() {
 
	 /*
		 * Menu selection
		 */
	 function init(){
 		$("#menu_expsearch").on("click",function(){
	 		location.href='expExplore.jsp';
	 	});
	 	
	 	$("#menu_expnew").on("click",function(event, data){
	 		if (data)
	 			location.href='expCreate.jsp?data='+encodeURIComponent(JSON.stringify(data));
	 		else
	 			location.href='expCreate.jsp';
	 	});
	 	
	 	$("#menu_expnew_adv").on("click",function(event, data){
	 		if (data)
	 			location.href='expCreateAdv.jsp?data='+encodeURIComponent(JSON.stringify(data));
	 		else
	 			location.href='expCreateAdv.jsp';
	 	});
	 	
	 	$("#menu_event").on("click",function(event, data){
	 		if (data)
	 			location.href='eventExplore.jsp?data='+encodeURIComponent(JSON.stringify(data));
	 		else
	 			location.href='eventExplore.jsp';
	 	});
	 	
	 	$("#menu_monexp").on("click",function(){
	 		location.href='monExperiments.jsp';
	 	});
	 	
	 	$("#menu_monusr").on("click",function(){
	 		location.href='monUsers.jsp';
	 	});
	 }

	 /*
	  * Custom event triggered when a menu is selected. Useful to make changes coming from other menus before display
	  */
//	 $.event.trigger({
//		type: "menu_selection",
//		message: "Menu selected",
//		time: new Date()
//	 });

	 
	 init();
	 
	 
 });
 
 
