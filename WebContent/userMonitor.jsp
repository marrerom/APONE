<!DOCTYPE html>
<html>
<head>
<meta content='text/html;charset=utf-8' http-equiv='Content-Type'>
<meta content='utf-8' http-equiv='encoding'>
<meta name="robots" content="noindex, nofollow">
<title>Semantic Test</title>
<link rel='stylesheet' href='css/semantic.min.css'>
<link rel='stylesheet' href='css/specific.css'>
<link rel='stylesheet' href='css/calendar.min.css'>
<script src='https://code.jquery.com/jquery-3.1.1.min.js'></script>
<script src='js/semantic.min.js'></script>
<script src='js/general.js'></script>
<script src='js/calendar.min.js'></script>
<script src='js/userMonitor.js'></script>
</head>
<body>

<%
    session=request.getSession(false);
    if(session.getAttribute("authuser")==null)
    {
        response.sendRedirect(request.getContextPath() + "/service/user/authenticate");
    }

%> 
	<div class='ui blue stacked segment'>
		<h4 class='ui blue huge header'>Academic Platform for ONline Experiments (APONE)</h4>
			<div class='user'>User: <%= session.getAttribute("idname") %></div>
	</div>
	<div class='ui padded content grid' style='height: 90%;'>
		<div class='two wide column'>
			<div id='menu' class='ui vertical fluid tabular menu'>
				<div class="item">
					<div class="header">Experiments</div>
					<div class="menu">
						<a id='menu_expsearch' class="item">Manage</a> 
							<a id='menu_expnew' class="item">Create new</a>
							<a id='menu_expnew_adv' class="item">Create new (Adv.)</a>
					</div>
				</div>
				<div class="item">
					<div class="header">Events</div>
					<div class="menu">
						<a id='menu_event' class="item">Manage</a>

					</div>
				</div>


				<div class="item">
					<div class="header">Users</div>
					<div class="menu">
						<a id='menu_admusr' class="item">Explore</a>
						<a id='menu_usrnew' class="item">New User</a>
					</div>
				</div>

				<div class="item">
					<div class="header">Monitoring</div>
					<div class="menu">
						<a id='menu_monexp' class="item">Experiments</a>
						<a id='menu_monusr' class="active item">Users</a>
					</div>
				</div>

			</div>
		</div>

		<div id='contents_monusr' class='fourteen wide stretched column'>

			<!-- <div id='contents_mon' class='ui grid' style='height: 100%;'>  -->

<!-- 				<div class='sixteen wide stretched column' style="height: 100%;"> -->
<!-- 					<h3 class='ui blue header'>Users</h3> -->
<!-- 					<div class="ui grid" style="height: 80%; overflow-y: auto;"> -->
				
								<div class='ui grid'>
					<div class='sixteen wide column'>
						<div class='ui center aligned basic segment'>
							<div class='big blue ui button _assign' tabindex='0'>Participate in an experiment</div>
								
							
						</div>
						Please, note that you may be redirected to the same experiment more than once if the client of that experiment does not indicate that you have already completed it (by means of the "completed" event). In that case, close the pop-up window and click the button again.
					</div>

				</div>
				
				<div class='ui grid' style='height: 90%;'>
					<div class='sixteen wide column'>
					
					
						<div class='ui blue segment'
							style='height: 85%; overflow-y: auto'>
								<h3 class='ui blue header'>Leaderboard</h3>
						<table class="ui compact blue celled table _users" style="padding-left:0;padding-right:0;">

							<thead>
								<tr>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _users',0);">User unique name</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _users',1);">Experiments participated</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _users',2);">Experiments completed</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _users',3);">Remaining (running) experiments not completed</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>

					</div>



				</div>
			</div>

		</div>


		<div class="ui modal _success">
			<div class="header">Congratulations!</div>
			<div class="content"></div>
		</div>

		<div class="ui modal _error">
			<div class="header">Some errors have occurred</div>
			<div class="content"></div>
		</div>

		<div class="ui modal _info">
			<div class="header"></div>
			<div class="content"></div>
		</div>




</body>
</html>