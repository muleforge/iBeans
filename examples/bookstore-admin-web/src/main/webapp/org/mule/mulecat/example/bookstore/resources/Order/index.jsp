<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>MuleCat REST Bookstore Sample - Order placed</title>
</head>
<body>

<h1>Order Placed</h1>

Congratulations you will soon be the proud owner of ${it.book.title} by ${it.book.author}

Your item has been sent to: ${it.address}
We have also sent you a confirmation email to: ${it.email}

<br/>
<a href="/<%=request.getContextPath()%>">Return to Home Page</a>
</body>
</html>