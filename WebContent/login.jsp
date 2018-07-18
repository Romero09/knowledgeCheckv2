<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Login Page</title>
<link href="css/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<form method="post" action="LoginServlet">
<div style="padding: 100px 0 0 250px;">
<div id="login-box">
<h2>Login Page</h2>
<h3>Please provide your credentials to start the test</h3>
<br>
<div id="login-box-name" style="margin-top:20px;">SURNAME:</div>
<div id="login-box-field" style="margin-top:10px;">
<input name="SURENAME" class="form-login" title="Username" value="" size="30" maxlength="50" />
</div>
<div id="login-box-name" style="margin-top:10px;">CODE:</div>
<div id="login-box-field" style="margin-top:10px;">
<input name="CODE" type="password" class="form-login" title="Password" value="" size="30" maxlength="48" />
</div>
<br />
<input style="margin-left:200px;"type="submit" value="Login" />
<br />
<span class="login-box-options">
New User?<a href="register.jsp" style="margin-left:30px;">Register Here</a>
</span>
</div>
</div>
</form>
</body>
</html>