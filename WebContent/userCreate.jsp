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
<script src='js/userCreate.js'></script>
</head>
<body>

	<%
		session = request.getSession(false);
		if (session.getAttribute("authuser") == null) {
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
						<a id='menu_expsearch' class="item">Manage</a> <a id='menu_expnew'
							class="item">Create new</a> <a id='menu_expnew_adv' class="item">Create
							new (Adv.)</a>
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
						<a id='menu_admusr' class="item">Explore</a> <a id='menu_usrnew'
							class="active item">New User</a>
					</div>
				</div>


				<div class="item">
					<div class="header">Monitoring</div>
					<div class="menu">
						<a id='menu_monexp' class="item">Experiments</a> <a
							id='menu_monusr' class="item">Users</a>

					</div>
				</div>


			</div>
		</div>

		<div id='contents_usrnew' class='fourteen wide stretched column'>

			<div class='ui grid' style='height: 90%;'>
				<div class='sixteen wide column'>
					<div class='ui blue segment' style='height: 85%; overflow-y: auto'>
						
					<form id='nusr_form' class='ui form'>
						<div class='fields'>
							<div class='required field'>
								<label>Name</label> <input name='name' placeholder='User name'
									type='text' title='User name'>
							</div>

							<div class='required field'>
								<label>Twitter id</label> <input name='idTwitter'
									placeholder='Twitter id' type='text' title="Twitter id">
							</div>

							<div class='required field'>
								<label>Rol</label> <select name='rol'>
									<option value="user">USER</option>
									<option value="admin">ADMIN</option>
								</select>
							</div>

						</div>
					<div class="ui error message"></div>
					</form>

					</div>

					<div class='ui grid'>
						<div class='sixteen wide column'>
							<div class='ui center aligned basic segment'>
								<div class='blue ui button _add' tabindex='0'>Add</div>
								<div class='blue ui button _clear' tabindex='0'>Clear</div>
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

	</div>
</body>
</html>