<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>MuleCat REST Bookstore Example</title>
    </head>
    <body>

    <h1>${it.name}</h1>

    <a href="catalog">View Catalog</a>

   <h2>Find a book</h2>
<form method="POST" name="submitRequest" action="search">
    <table>
        <tr>
            <td>Title: </td>
            <td><input type="text" name="title"/></td>
           </tr>
        <tr>
            <td>Author: </td>
            <td><input type="text" name="author"></td>
        </tr>
    </table>
    <input type="submit" name="submit" value="Order" />
</form>


<br/>
<a href="/<%=request.getContextPath()%>">Return to Home Page</a>
</body>
</html>