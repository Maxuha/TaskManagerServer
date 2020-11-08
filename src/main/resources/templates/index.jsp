<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Task manager</title>
</head>
<body>
    <%if (request.getSession().getAttribute("JSESSIONID") != null) {%>
        <form action="${pageContext.request.contextPath}/logout" method="post">
            <button type="submit">Logout</button>
        </form>
    <%}%>
</body>
</html>