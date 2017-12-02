<!DOCTYPE html>
<html>
<head>
<meta content='text/html;charset=utf-8' http-equiv='Content-Type'>
<meta content='utf-8' http-equiv='encoding'>
<title>Semantic Test</title>
<link rel='stylesheet' href='css/semantic.min.css'>
<link rel='stylesheet' href='css/specific.css'>
<link rel='stylesheet' href='css/calendar.min.css'>
<script src='https://code.jquery.com/jquery-3.1.1.min.js'></script>
<script src='js/semantic.min.js'></script>
<script src='js/general.js'></script>
<script src='js/calendar.min.js'></script>
<script src='js/expExplore.js'></script>
</head>
<body>
<%
    session=request.getSession(false);
    if(session.getAttribute("user")==null)
    {
        response.sendRedirect(request.getContextPath() + "/service/authenticate");
    }

%> 
	<div class='ui blue stacked segment'>
		<h4 class='ui blue huge header'>Information Retrieval
			Experimental Platform</h4>
	</div>
	<div class='ui padded content grid' style='height: 90%;'>
		<div class='two wide column'>
			<div id='menu' class='ui vertical fluid tabular menu'>
				<div class="item">
					<div class="header">Experiments</div>
					<div class="menu">
						<a id='menu_expsearch' class="active item">Manage</a> 
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
					<div class="header">Monitoring</div>
					<div class="menu">
						<a id='menu_mon' class="item">Dashboard</a>

					</div>
				</div>

			</div>
		</div>

		<div id='contents_expsearch' class='fourteen wide stretched column'>
			<div class='ui grid' style='height: 100%;'>

				<div class='seven wide stretched column'>
					<div class='ui grid' style='height: 90%;'>
						<div class='sixteen wide column'>
							<div class='ui blue segment'
								style='height: 100%; overflow-y: auto'>
								<form class='ui form'>

									<div class='field'></div>

									<div class='fields'>
										<div class='field'>
											<label>Identifier</label> <input name='identifier'
												placeholder='Identifier' type='text' title='Unique identifier of the experiment'>
										</div>
										<div class='field'>
											<label>Experimenter</label> <input name='experimenter'
												placeholder='Experimenter' type='text' title='Name of the experimenter'>
										</div>
										<div class='field'>
											<label>Experiment</label> <input name='name' placeholder='Experiment Name'
												type='text' title='Experiment name'>
										</div>
									</div>
									<div class='field'>
										<label>Description</label> <input name='description'
											placeholder='Description' type='text' title='Experiment description'>
									</div>

									<h4 class='ui right blue header'>Variant</h4>

									<!-- <div class='ui fluid green segment'
										style='height: 10%; overflow-y: auto'> -->
									<div class='fields'>
										<div class='field'>
											<label>Variant</label> <input name='tname'
												placeholder='Variant.Name' type='text' title='Variant name'>
										</div>
										<div class='eleven wide field'>
											<label>Description</label> <input name='tdesc'
												placeholder='Variant.Description' type='text' title='Description of this variant'>
										</div>
									</div>
									<div class='fields'>
									<div class='eight wide field'>
										<label>Definition</label> <input name='tdef'
											placeholder='Definition (DSL)' type='text' title='PlanOut script contents'>
									</div>
									<div class='eight wide field'>
										<label>Client URL</label>
										<input name='turl' placeholder='Valid URL' type='text' title='URL where the variant is located'>
									</div>
									</div>
									<!-- </div>-->
									<h4 class='ui right blue header'>Configuration</h4>

									<!-- <div class='ui fluid orange segment' 
										style='height: 10%; overflow-y: auto'>  -->
									<div class='fields'>
										<div class='five wide field'>
											<label>Configuration</label> <input name='cname'
												placeholder='Conf.Name' type='text' title='Name of the specific configuration of
												 the experiment (we can define similar experiments with different configurations, 
												 for example for testing with less percentage of users assigned to treatments)'>
										</div>
										<div class='twelve wide field'>
											<label>Client</label> <input name='controller'
												placeholder='Client info' type='text' title='Information about the client used: link to the code, version, etc.'>
										</div>
										<div class='field'>
											<div class='ui checkbox'>
												<input tabindex='0' name='run' type='checkbox' title='the experiment is currently running'> <label>Running</label>
											</div>
											<div class='ui checkbox'>
												<input tabindex='0' name='norun' type='checkbox' title='the experiment is not running currently'> <label>No
													Running</label>
											</div>

										</div>
										<div class='field'></div>

									</div>


									<div class='fields'>

										<div class='four wide field'>
											<label>Date started</label>
											<div class="ui calendar _started">
												<div class="ui input left icon">
													<i class="calendar icon"></i> <input type="text"
														name='date_started' placeholder="Date/Time" title='date when the experiment started'>
												</div>
											</div>
										</div>
										<div class='four wide field'>
											<label>Date ended</label>
											<div class="ui calendar _ended">
												<div class="ui input left icon">
													<i class="calendar icon"></i> <input type="text"
														name='date_ended' placeholder="Date/Time" title='date when the experiment ended'>
												</div>
											</div>

										</div>
										<div class='four wide field'>
											<label>Date to end</label>
											<div class="ui calendar _toend">
												<div class="ui input left icon">
													<i class="calendar icon"></i> <input type="text"
														name='date_toend' placeholder="Date/Time" title='date when the experiment is supposed to stop'>
												</div>
											</div>


										</div>

										<div class='four wide field'>
											<label>Max.Units</label> <input name='maxexp'
												placeholder='Max. units' type='text' title='Maximum number of different experimental units to expose to the experiment'>
										</div>



									</div>
									<!-- </div> -->
								</form>
							</div>
							<!-- segment -->
						</div>
					</div>
					<div class='ui grid'>
						<div class='sixteen wide column'>
							<div class='ui center aligned basic segment'>
								<div class='ui button _clear' tabindex='0'>Clear</div>
								<div class='ui button _filter' tabindex='0'>Filter</div>
								<div class='ui button _all' tabindex='0'>Show All</div>
							</div>
						</div>

					</div>


				</div>


				<div class='nine wide stretched column'>
					<div class='ui grid' style='height: 90%;'>
						<div class='sixteen wide column'>
							<div class='ui fluid blue segment'
								style='height: 100%; overflow-y: auto'>
								<div class="ui checkbox master">
									<div class='content'>
										<input name="example" type="checkbox"> <label></label>
									</div>

								</div>
								<h3 class='ui dividing header' style='display: inline;'>Experiments
								</h3>

								<div class='ui relaxed divided list'>
									<div class='item'>
										<div class="ui checkbox list">
											<div class='content'>
												<input name="example" type="checkbox"> <label><a
													class='header'>Experiment test@experimenter</a></label>
											</div>
											<div class='description'>Description...</div>
										</div>
									</div>

								</div>
							</div>
						</div>
					</div>

					<div class='ui grid'>
						<div class='sixteen wide column'>
							<div class='ui center aligned basic segment'>
								<div class='ui button _remove' tabindex='0'>Remove</div>
								<div class='ui button _start' tabindex='0'>Start</div>
								<div class='ui button _stop' tabindex='0'>Stop</div>
								<div class='ui button _events' tabindex='0'>Show events</div>
								<div class='ui button _new' tabindex='0'>New conf.</div>
								<div class='ui button _newadv' tabindex='0'>New conf.(Adv.)</div>
							</div>
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


		<div class="ui modal _experimentinfo">
			<div class="header">
				Experiment <span name='idexp'></span>
			</div>
			<div class="content">
				<div class='ui blue segment' style='height: 100%; overflow-y: auto'>
					<form class='ui form'>
						<div class='inline fields'>
							<div class='field'>
								<h4 class="ui blue left  floated header">Experiment:</h4>
								<span name='name'>Experiment name</span>
							</div>
							<div class='field'>
								<h4 class="ui blue left floated header">Experimenter:</h4>
								<span name='experimenter'>Experimenter</span>
							</div>
							<div class='field'>
								<h4 class="ui blue left floated header">Unit:</h4>
								<span name='unit'>Experiment unit</span>
							</div>
							</div>
							<div class='inline fields'>
							<div class='field'>
								<h4 class="ui blue left floated header">Description:</h4>
								<span name='description'>Experiment description</span>
							</div>
						</div>

						<h3 class='ui blue right header'>Variants</h3>
						<div class='container _treatment'></div>

						<h3 class='ui right blue header'>Configuration</h3>
						<div class='inline fields'>
							<div class='field'>
								<h4 class="ui blue left  floated header">Configuration:</h4>
								<span name='cname'>Config. name</span>
							</div>
							<div class='field'>
								<h4 class="ui blue left floated header">Client:</h4>
								<span name='controller'>Client info</span>
							</div>
							</div>
							<div class='inline fields'>
							<div class='field'>
								<h4 class="ui blue left floated header">Maximum units:
								</h4>
								<span name='maxexp'>Undefined</span>
							</div>
							<div class='field'>
								<h4 class="ui blue left floated header">Date to end:</h4>
								<span name='date_toend'>Undefined</span>
							</div>

						</div>
						<div class='fields'>
							<div class='seven wide field'>
								<h4 class="ui blue left floated header">Started:</h4>
								<span name='date_started'>Never</span>
							</div>
							<div class='seven wide field'>
								<h4 class="ui blue left floated header">Ended:</h4>
								<span name='date_ended'>Never</span>
							</div>
						</div>

						<div class='container _config'></div>
					</form>
				</div>
			</div>
		</div>
</body>
</html>