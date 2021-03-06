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
<script src='js/expMonitor.js'></script>
<script src="js/plotly-latest.min.js"></script>
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
						<a id='menu_monexp' class="active item">Experiments</a>
						<a id='menu_monusr' class="item">Users</a>
					</div>
				</div>



			</div>
		</div>

		<div id='contents_monexp' class='fourteen wide stretched column'>

			<!-- <div id='contents_mon' class='ui grid' style='height: 100%;'>  -->

				<div class='sixteen wide stretched column' style="height: 100%;">
					<h3 class='ui blue header'>Running Experiments</h3>
					<div class="ui grid" style="height: 45%; overflow-y: auto;">
					
						<table class="ui compact blue celled table _running" style="padding-left:0;padding-right:0;">

							<thead>
								<tr>
								    <th>Status</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _running',1);">Experiment</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _running',2);">Started</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _running',3);">Max. Completed</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _running',4);">Date to End</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact blue celled table _running',5);">Units</th>
									<th>Data</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>

					</div>
					<h3 class='ui grey header'>Finished Experiments</h3>
					<div class="ui grid" style='height: 45%; overflow-y: auto;'>
					
						<table class="ui compact grey celled table _finished" style="padding-left:0;padding-right:0;">
							<thead>
								<tr>
									<th>Status</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact grey celled table _finished',1);">Experiment</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact grey celled table _finished',2);">Last Execution</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact grey celled table _finished',3);">Max. Completed</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact grey celled table _finished',4);">Date to End</th>
									<th style='cursor: pointer;' onclick="sortTable('ui compact grey celled table _finished',5);">Units</th>
									<th>Data</th>
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
		
		<div id="chartwindow" class="ui modal _chart">
			<div class="header">Experiment events</div>
			<div class ="parameters">
			

					<div class="ui fluid segment">
					
						<select name='mondropdown' class='_chartename ui dropdown'>
							<option value="">Select Event</option>
						</select>
						
						<select name='mondropdown' class='_chartagg ui dropdown'>
							<option value="">Select Operation</option>
						</select>
						
						<div class='ui checkbox' style='padding-left:2px' >
							<input  tabindex='0' name='chartcompleted' type='checkbox'	title='Only experimental units with "completed" event'>
							<label>Completed</label>
						</div>

						
						<div class='_chartload small blue ui button' tabindex='0' style='float:right'>Load Data</div>
						
					</div>
	
			</div>
				<div id="chartContainer" style="height: 370px; width: 100%"></div>
		</div>

<div>

</div>
</body>
</html>