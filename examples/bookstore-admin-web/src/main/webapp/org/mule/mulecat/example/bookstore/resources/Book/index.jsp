<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>MuleCat Bookstore Example</title>
    </head>
    <body>

   <h2>Book</h2>
    <table>
        <tr>
            <td>Title: </td>
            <td>${it.title}</td>
           </tr>
        <tr>
            <td>Author: </td>
            <td>${it.author}</td>
        </tr>
        <tr>
            <td>Price: </td>
            <td>${it.price}</td>
        </tr>
    </table>
<hr/>

<h2>Order Now</h2>
<form method="POST" name="submitRequest" action="/order">
    <table>
        <tr>
            <td>ID: </td>
            <td><input type="text" name="id" readonly="true"/>${it.id}</td>
           </tr>
        <tr>
            <td>Quantity: </td>
            <td><input type="text" name="quantity"></td>
        </tr>
        <tr>
            <td>Shipping Address: </td>
            <td><input type="text" name="address"/></td>
        </tr>
        <tr>
            <td>E-mail: </td>
            <td><input type="text" name="email"/></td>
        </tr>
    </table>
    <input type="hidden" name="submitted" value="true"/>
    <input type="submit" name="submit" value="Order" />
</form>


<br/>
<a href="/<%=request.getContextPath()%>">Return to Home Page</a>
</body>
</html>