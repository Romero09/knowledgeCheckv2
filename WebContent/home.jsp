<%@page import="java.util.List"%>
<%@page import="com.javawebtutor.service.LoginService"%>
<%@page import="java.util.Date"%>
<%@page import="com.javawebtutor.model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="css/style.css" />
<title>Result Page</title>
</head>
<body>
	<div style="padding: 100px 0 0 250px;">
		<div id="home-box">


			<h1>Home Page</h1>


			<%
                 User user = (User) session.getAttribute("user");
             %>
			<b>Welcome <%= user.getFirstName() + " " + user.getLastName()%></b> <br />
			<a href="logout.jsp">Logout</a> <br />
		</div>
	</div>

</body>
</html>