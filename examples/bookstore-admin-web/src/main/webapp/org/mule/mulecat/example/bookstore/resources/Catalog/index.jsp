<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>MuleCat Bookstore Example</title>
</head>
<body>

<h2>Catalog</h2>
<table>
    <tr>
        <th width="50">ID</th>
        <th>Author</th>
        <th>Title</th>
        <th>Price</th>
    </tr>
    <c:forEach var="i" items="${it.books}">
        <tr>
            <th>${i.id}</th>
            <td>${i.author}</td>
            <td><a href="catalog/items/${i.id}">${i.title}</a></td>
            <td>£${i.price}</td>
        </tr>
    </c:forEach>
</table>
<hr/>

<br/>
<a href="/<%=request.getContextPath()%>">Return to Home Page</a>
</body>
</html>