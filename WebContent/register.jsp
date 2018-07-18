<html>
<head>
<title>Registration Form</title>
</head>
<body>

<link href="css/style.css" rel="stylesheet" type="text/css" />
<form action="RegisterServlet" method="POST">
<div style="padding: 100px 0 0 250px;">
<div id="table-box">
<h2>Student Registration Form</h2>
<table align="LEFT" cellpadding = "5" style="width: 200px; height: 100px">
<tr>
<td>First Name</td>
<td><input type="text" name="firstName" maxlength="30"/>

</td>
</tr>

<tr>
<td>Last Name</td>
<td><input type="text" name="lastName" maxlength="30"/>

</td>
</tr>
<tr>
<td>CODE</td>
<td><input type="text" name="password" maxlength="100" /></td>
</tr>
<tr>
<td colspan="2" align="center">
<input type="submit" value="Submit">
<input type="reset" value="Reset">
</td>
</tr>
</table>
</div>
</form>
</body>
</html>