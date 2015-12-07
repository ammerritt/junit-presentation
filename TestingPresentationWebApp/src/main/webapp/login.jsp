<%@ taglib uri="http://jakarta.apache.org/taglibs/session-1.0" prefix="sess" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/request-1.0" prefix="req"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/response-1.0" prefix="res"%>

<html>
<head>
<title>Login Page for Examples</title>
</head>
<body bgcolor="white">
<form method="POST"
	action="<res:encodeUrl>j_security_check</res:encodeUrl>">
<table border="0" cellspacing="5">
	<tr>
		<th align="right">Username:</th>
		<td align="left"><input type="text" name="j_username"></td>
	</tr>
	<tr>
		<th align="right">Password:</th>
		<td align="left"><input type="password" name="j_password"></td>
	</tr>
	<tr>
		<td align="right"><input type="submit" value="Log In"></td>
		<td align="left"><input type="reset"></td>
	</tr>
</table>
</form>
</body>
</html>
