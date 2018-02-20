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
<script src='js/eventExplore.js'></script>
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
						<a id='menu_event' class="active item">Manage</a>

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
						<a id='menu_monusr' class="item">Users</a>
					</div>
				</div>
				
				

			</div>
		</div>

		<div id='contents_event' class='fourteen wide stretched column'>

			<div class='ui grid' style='height: 100%;'> 


				<div class='seven wide stretched column' style='height: 100%;'>
					<!-- for some weird reason the stretched is not working here-->
					<div class='ui grid' style='height: 90%;'>
						<div class='sixteen wide column'>
							<div class='ui blue segment'
								style='height: 100%; overflow-y: auto'>
								<form class='ui form'>
									<div class='inline fields'>
										<div class='inline field'>
											<label>Experimenter</label> <input name='experimenter' placeholder="Experiment's owner" type='text' title="Experiment's owner">
										</div>
										<div class='inline fields'>
										<div class='field'>
											<div class='ui radio checkbox'>
												<input id='etype_binary' name='etype' type='radio' value='BINARY'> <label>Binary</label>
											</div>
										</div>
										<div class='field'>
											<div class='ui radio checkbox'>
												<input id='etype_string' name='etype' type='radio' checked value='STRING'> <label>String</label>
											</div>
										</div>
										<div class='field'>
											<div class='ui radio checkbox'>
												<input id='etype_json' name='etype' type='radio' value='JSON'> <label>JSON</label>
											</div>
										</div>
										<div class='field'>
											<div class='ui radio checkbox'>
												<input id='etype_none' name='etype' type='radio' value=''> <label>Any</label>
											</div>
										</div>
										
										</div>
									</div>

									<div class='fields'>
										<div class='eight wide field'>
											<label>Name</label> <input name='ename'
												placeholder='Event name' type='text' title='Predefined (exposure) or user-defined name of event (eg.click, dwell-time)'>
										</div>
										<div class='eight wide field'>
											<label>Variant</label> <input name='treatment' placeholder='Variant.Name' type='text' title='Variant name'>
										</div>
									
									</div>
									<div class='fields'>
										<div class='eight wide field'>
											<label>Timestamp</label>
											<div class="ui calendar">
												<div class="ui input left icon">
													<i class="calendar icon"></i> <input type="text"
														name='timestamp' placeholder="Date" title='Date the event was created'>
												</div>
											</div>
										</div>
									
									
										<div class='eight wide field'>
											<label>Unit ID</label> <input name='idunit'
												placeholder='Unit ID' type='text' title='Unique identifier of the experimental unit (eg. user)'>
										</div>

									</div>

									<div class='fields'>
										<div class='eight wide field'>
											<label>Event ID</label> <input name='_id'
												placeholder='Event ID' type='text'>
										</div>
										<div class='eight wide field'>
											<label>Experiment ID</label> <input name='idconfig'
												placeholder='Experiment ID' type='text' title='Unique identifier of the experiment'>
										</div>
									</div>


									<div class='fields'>
										<div class='eight wide field'>
											<label>User-agent</label> <input name='agent' placeholder='User-agent' type='text' title='Information about the agent used by the user to register the event (regex)'>
										</div>
									
										<div class='eight wide field'>
											<label>Value</label> <input name='evalue' placeholder='Value' type='text' title='Information about the interaction with the user. If the type selected is String, this content works as a regex. If the type selected is JSON, this content works as a JSON document (eg. {"hits":0} will return those events where we have at least that pair property-value in their contents)'>
										</div>
										
									</div>
									
									<div class='ui fluid segment _params'style='height: 10%; overflow-y: auto'>
									<div class='inline fields'>

										<div class='field'>
											<label>Parameter</label> <input name='pname'
												placeholder='Param.Name' type='text' title='(PlanOut) Parameter name'>
										</div>

										<div class='field'>
											<label>Value</label> <input name='pvalue'
												placeholder='Param.Value' type='text' title='(PlanOut) Parameter value'>
										</div>

										<i class='_param minus inline link circle icon'></i> <i
											class='_param add inline link circle icon'></i>
									</div>


								</div>

								</form>
							</div>
							<!-- segment -->
						</div>
					</div>
					<div class='ui grid'>
						<div class='sixteen wide column'>
							<div class='ui center aligned basic segment'>
								<div class='blue ui button _clear' tabindex='0'>Clear</div>
								<div class='blue ui button _filter' tabindex='0'>Filter</div>
								<div class='blue ui button _all' tabindex='0'>Show All</div>
							</div>
						</div>

					</div>


				</div>


				<div class='eight wide stretched column' style='height: 100%;'>
					<!-- for some weird reason the stretched is not working here-->
					<div class='ui grid' style='height: 90%;'>
						<div class='sixteen wide column'>
							<div class='ui fluid blue segment'
								style='height: 100%; overflow-y: auto'>
								<div class="ui checkbox master">
									<div class='content'>
										<input name="example" type="checkbox"> <label></label>
									</div>

								</div>
								<h3 class='ui dividing header' style='display: inline;'>Events
								</h3>

								<div class='ui relaxed divided list'>
									<div class='item'>
										<div class="ui checkbox list">
											<div class='content'>
												<input name="example" type="checkbox"> <label><a
													class='header'>experiment:name@experimenter - Binary/String/JSON</a></label>
											</div>
											<div class='description'>Value</div>
										</div>
									</div>

								</div>
							</div>
						</div>
					</div>

					<div class='ui grid'>
						<div class='sixteen wide column'>
							<div class='ui center aligned basic segment'>
								<div class='blue ui button _remove' tabindex='0'>Remove</div>
								<div class='blue ui button _downloadcsv' tabindex='0'>Download CSV</div>
								<div class='blue ui button _downloadjson' tabindex='0'>Download JSON</div>
							</div>
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

<!-- TODO: display event info the same way experiment info is displayed -no input boxes- -->
		<div class="ui modal _eventinfo">
			<div class="header">
				Event <span name='idevent'></span>
			</div>
			<div class="content">

				<div class='ui blue segment' style='height: 100%; overflow-y: auto'>
					<form class='ui form'>
						<div class='fields'>
							<div class='six wide field'>
								<label>Experiment ID</label> <input name='idconfig'
									placeholder='Experiment ID' type='text' readonly >
							</div>
							<div class='six wide field'>
								<label>Unit ID</label> <input name='idunit'
									placeholder='Unit ID' type='text' readonly>
							</div>
							<div class='six wide field'>
							<label>Timestamp</label> <input type="text" name='timestamp'
									placeholder="Date" readonly>
							</div>
						</div>

						<div class='fields'>
							<div class='field'>
								<label>Experimenter</label> <input name='experimenter' placeholder='Owner of the experiment'
									type='text' readonly>
							</div>
						
							<div class='field'>
								<label>Type</label> <input name='ename' placeholder='Event type'
									type='text' readonly>
							</div>
							<div class='field'>
								<label>Variant</label> <input name='treatment' placeholder='Variant'
									type='text' readonly>
							</div>
							<div class='eleven wide field'>
								<label>User-agent</label> <input name='agent' placeholder='User-agent'
									type='text' readonly>
							</div>


						</div>
			
							<div class='inline fields'>
							<div class='field'>
								<div class='ui radio checkbox'>
									<input id='etype_binary' name='etype' type='radio' value='BINARY' disabled> <label>Binary</label>
								</div>
							</div>
							<div class='field'>
								<div class='ui radio checkbox'>
									<input id='etype_string' name='etype' type='radio' checked value='STRING' disabled> <label>String</label>
								</div>
							</div>
							<div class='field'>
								<div class='ui radio checkbox'>
									<input id='etype_json' name='etype' type='radio' value='JSON' disabled> <label>JSON</label>
								</div>
							</div>
							
							</div>

						<div class='fields'>
						
							<div class='sixteen wide field'>
								<label>Pairs PlanOut variable-value</label> 
								<textarea name='paramvalues'  rows='2' readonly></textarea>
							</div>
						
						</div>
						<div class='fields'>
							<div class='sixteen wide field'>
								<label>Value</label>
								<textarea name='evalue' rows='6' readonly></textarea>
							</div>
						
						</div>

					</form>
				</div>

			</div>
		</div>

</body>
</html>