<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<% 
request.setAttribute("rootPath", request.getContextPath());
request.setAttribute("service", request.getParameter("service"));
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>client2 system</title>
</head>
<body>
    <div id="page">
            <div id = "content">
                <h1>username: ${username}</h1>
            </div>
            <form action="${rootPath}/user/clientlogout" method="POST" id="logout" name="logout">
            	<input type="submit" value="logout" />
            </form>
    </div>
</body>
</html>