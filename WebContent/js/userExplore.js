/**
 * Admin Users
 */

var tabadmusr;

$(document).ready(function() {

	const row = "<tr>" +
	"<td><input name='checkbox' type='checkbox'></td>" +
	"<td><span class='idname sort'>User name</span></td>" +
	"<td><span class='name sort'>User name</span></td>" +
	"<td><span class='idTwitter sort'>User name</span></td>" +
	"<td><span class='rol sort'>Rol</span></td>" +
	"<td><span class='created sort'>Exp. Created</span></td>" +
	"<td><span class='running sort'>Exp. Running</span></td>" +
	"<td><span class='participated sort'>Exp. Participated</span></td>" +
	"<td><span class='completed sort'>Exp. Completed</span></td>" +
	"</tr>";
	
	
	function init() {
		
		tabadmusr = $("#contents_admusr");
		
		table = tabadmusr.find(".ui.compact.blue.celled.table._users > tbody");
		
		tabadmusr.find(".checkbox.master").find("[type=checkbox]").on("click", function(){
				if ($(this).is(":checked")){
					table.find("[type=checkbox]").prop("checked", true);
				} else {
					table.find("[type=checkbox]").prop("checked", false);
				}
			});
		
		loadUsers();
		
		tabadmusr.find(".ui.button._delete").on("click", function() {
			table.find("[type=checkbox]:checked").each(function(){
				var idname = $(this).closest("tr").attr("id");
				$(".ui.modal._info").find(".content").empty();
				$(".ui.modal._info").find(".content").append("<p>You are about to remove user "+idname+"</p>");
				$(".ui.modal._info").find(".content").append("<p>The associated experiments and events will be removed. Continue?</p>");
				$(".ui.modal._info").find(".content").append("<div class='actions'><div class='ui approve button'>Yes</div><div class='ui cancel button'>No</div></div>");
				$(".ui.modal._info").modal('setting', {
					onApprove : function() {
			    	  deleteUser(idname);
					}
				});
				$(".ui.modal._info").modal('show');
				
			});	
		});
	}
	
	function getUsersSuccess(data){
		$.each(data, function (index, value) {
			var existing = table.find("#"+value.idname);
			var newrow = row;
			if (existing.length != 0) {
				newrow = $(existing[0]);
			}else{
				newrow = $(newrow).attr("id", value.idname);
				table.prepend(newrow);
			}
			newrow.find(".idname").text(value.idname);
			newrow.find(".name").text(value.name);
			newrow.find(".idTwitter").text(value.idTwitter);
			newrow.find(".rol").text(value.rol);
			newrow.find(".created").text(value.ncreated); //TODO:only count
			newrow.find(".running").text(value.nrunning); //TODO:only count
			newrow.find(".participated").text(value.nparticipated); //TODO: only count
			newrow.find(".completed").text(value.ncompleted); //TODO: only count
		});
	}
	
		
	function loadUsers(){
		var errorMessage = "There was an error loading users";
		$.ajax({
			  type: 'GET',	
			  dataType: "json",
			  url: adminUsersURL,
			  success: getUsersSuccess,
			  error: function(xhr, status, error) {consoleError(xhr, errorMessage);}
			});
	}
	
	function deleteUser(idname){
		var errorMessage = "There was an error deleting the user";
		$.ajax({
			  type: 'PUT',	
			  url: deleteUserURL+"/"+idname,
			  success: function() {var existing = table.find("#"+idname); existing.remove();},
			  error: function(xhr, status, error) {alertError(xhr, errorMessage);}
			});
	}
	
	
	
	init();

});