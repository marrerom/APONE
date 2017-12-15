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
<script src='js/expCreateAdv.js'></script>
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
		<h4 class='ui blue huge header'>Information Retrieval
			Experimental Platform</h4>
	</div>
	<div class='ui padded content grid' style='height: 90%;'>
		<div class='two wide column'>
			<div id='menu' class='ui vertical fluid tabular menu'>
				<div class="item">
					<div class="header">Experiments</div>
					<div class="menu">
						<a id='menu_expsearch' class="item">Manage</a> <a id='menu_expnew'
							class="item">Create new</a> <a id='menu_expnew_adv'
							class="active item">Create new (Adv.)</a>
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
						<a id='menu_monexp' class="item">Experiments</a>
						<a id='menu_monusr' class="item">Users</a>
					</div>
				</div>

			</div>
		</div>

		<div id='contents_expnew_adv' class='fourteen wide stretched column'> 

			<!-- <div id='contents_expnew_adv' class='ui grid' style='height: 100%;'>  -->

				<div class='ui grid' style='height: 90%;'>
					<div class='sixteen wide column'>
						<div class='ui blue segment'
							style='height: 100%; overflow-y: auto'>
							<form id='nexp_form' class='ui form'>

								<div class='fields'>
									<div class='required field'>
										<label>Experiment</label> <input name='name'
											placeholder='Experiment Name' type='text'>
									</div>
									<div class='required field'>
										<label>Experimenter</label> <input name='experimenter'
											placeholder='Experimenter' type='text'
											title='Name of the experimenter' title="Experiment's owner">
									</div>
									<div class='required field'>
										<label>Unit</label> <input name='unit'
											placeholder='Unit descriptor' type='text'>
									</div>

									<div class='eleven wide field'>
										<label>Description</label> <input name='description'
											placeholder='Description' type='text'
											title='Description of the Experiment'>
									</div>

								</div>


								<h3 class='ui blue right header'>Variants</h3>
								<!-- 								 <div class='ui fluid green segment'
									style='height: 5%; overflow-y: auto'> -->
								<div class='ui fluid segment _treatments'
									style='height: 10%; overflow-y: auto'>

									<div class='inline fields'>

										<div class='required field'>
											<label>Variant</label> <input name='tname'
												placeholder='Variant.Name' type='text' title='Variant name'>
										</div>

										<div class='field'>
											<label>Client URL</label> <input name='turl'
												placeholder='Valid URL' type='text'
												title='URL where the variant is located'>
										</div>

										<div class='field'>

											<label>Description</label> <input name='tdesc'
												placeholder='Variant.Description' type='text'
												title='Description of this variant'>
										</div>


										<div class='field'>
											<label>Definition <a
												href='https://planout-editor.herokuapp.com/' target='_blank'>(Planout
													DSL)</a></label>
											<!--  <input name='tdef'	placeholder='Definition (DSL)' type='text'> -->
											<textarea name='tdef' rows='2'
												title='PlanOut script contents'></textarea>
										</div>
										<div class='field'>
											<div class='ui checkbox'>
												<input tabindex='0' name='tcontrol' type='checkbox'
													title='Check if the variant is control'> <label>Is
													Control</label>
											</div>
										</div>
										<i class='_treat minus inline link circle icon'></i> <i
											class='_treat add inline link circle icon'></i>

									</div>
								</div>


								<!-- </div>  -->
								<h3 class='ui right blue header'>Configuration</h3>
								<!-- <div class='ui fluid orange segment'
									style='height: 10%; overflow-y: auto'>  -->
								<div class='fields'>
									<div class='required field'>
										<label>Configuration</label> <input name='cname'
											placeholder='Conf.Name' type='text'
											title='Name of the specific configuration of
												 the experiment (we can define similar experiments with different configurations, 
												 for example for testing with less percentage of users assigned to treatments)'>
									</div>
									<div class='field'>
										<label>Client</label> <input name='controller'
											placeholder='Client info' type='text'
											title='Information about the client used: link to the code, version, etc.'>
									</div>

									<div class='field'>
										<label>Date to end</label>
										<div class="ui calendar _toend">
											<div class="ui input left icon">
												<i class="calendar icon"></i> <input type="text"
													name='date_toend' placeholder="Date/Time"
													title='date and time when the experiment is supposed to stop'>
											</div>
										</div>


									</div>
									<div class='field'>
										<label>Max.Completed Units</label> <input name='maxexp'
											placeholder='Max. units' type='text'
											title='Maximum number of different experimental units to expose to the experiment'>
									</div>
								</div>
								<div class='ui segment _distributions'
									style='height: 10%; overflow-y: auto'>
									<div class='inline fields'>
										<div class='required field'>

											<label>Variant</label> <select name='treatdropdown'
												class='_treatment ui dropdown'>
												<option value="">Select Variant</option>
											</select>
										</div>
										<div class='required field'>
											<label>Units (%)</label> <input name='percentage'
												placeholder='Percentage' type='text'
												title='Percentage of different experimental units exposed to this variant'>
										</div>
										<i class='_conf minus inline link circle icon'></i> <i
											class='_conf add inline link circle icon'></i>

									</div>
								</div>

								<!-- </div>  -->
								<div class="ui error message"></div>

							</form>
						</div>
						<!-- segment -->
					</div>
				</div>
				<div class='ui grid'>
					<div class='sixteen wide column'>
						<div class='ui center aligned basic segment'>
							<div class='ui button _clear' tabindex='0'>Clear</div>
							<div class='ui button _add' tabindex='0'>Add</div>
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
		</div>
</body>
</html>