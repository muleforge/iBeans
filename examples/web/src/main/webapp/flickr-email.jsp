<%@ page import="org.mule.ibeans.IBeansContext" %>
<%@ page import="org.mule.ibeans.flickr.FlickrIBean" %>
<%@ page import="org.mule.ibeans.gmail.GMailIBean" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="static org.mule.ibeans.IBeansSupport.select" %>
<%@ page import="java.util.List" %>
<%@ page import="javax.activation.DataSource" %>
<%@ page import="javax.activation.URLDataSource" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Node" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Simple jsp page with an iBean</title>

    <link rel="stylesheet" href="ibeans-examples.css"/>
</head>
<%
    IBeansContext ibeansContext = (IBeansContext) getServletConfig().getServletContext().getAttribute(IBeansContext.CONTEXT_PROPERTY);
    FlickrIBean flickr = ibeansContext.createIBean(FlickrIBean.class);
    String apiKey = (String) ibeansContext.getConfig().get("flickr.apiKey");
    flickr.init(apiKey, FlickrIBean.FORMAT.XML, Document.class);

    GMailIBean gmail = ibeansContext.createIBean(GMailIBean.class);
    String user = (String) ibeansContext.getConfig().get("gmail.username");
    String pass = (String) ibeansContext.getConfig().get("gmail.password");
    gmail.init(user, pass);

    List<DataSource> photos = new ArrayList<DataSource>();
%>
<body>
<h2>Flickr iBean Example</h2>

<form action="flickr-email.jsp" method="POST">

    <table>
        <tr>
            <td>Search Flickr:</td>
            <td><input id="search" class="input" type="text" name="search"/></td>
            <td><input id="submit" class="button" type="submit" value="Search"/></td>
            <td>Size:</td>
            <td><select name="size">
                <option value="SmallSquare">Small Square</option>
                <option value="Thumbnail">Thumbnail</option>
                <option value="Small" selected="true">Small</option>
                <option value="Meduim">Medium</option>
                <option value="Large">Large</option>
                <option value="Original">Original</option>
            </select></td>
            <td>Size:</td>
            <td><select name="type">
                <option value="Jpeg" selected="true">jpg</option>
                <option value="Gif">gif</option>
                <option value="Png">png</option>
            </select></td>
        </tr>
        <tr>
            <td>Email results to:</td>
            <td><input id="email" class="input" type="text" name="email"/></td>
        </tr>
    </table>
</form>
    <%
    if (request.getMethod().equals("POST")) {
    %>
<div id="results" class="photoGrid">
    <%
        Document doc = flickr.searchPhotos(request.getParameter("search"));

        for (Node n : select("//photo", doc))
        {
            URL url = flickr.getPhotoURL(n, Enum.valueOf(FlickrIBean.IMAGE_SIZE.class, request.getParameter("size")),
                    Enum.valueOf(FlickrIBean.IMAGE_TYPE.class, request.getParameter("type")));
            photos.add(new URLDataSource(url));
    %>
    <div class="photo">
        <img class src="<%=url.toString()%>" alt="Photo" title="<%=url.toString()%>">
    </div>
    <%
        }
        String emailAddress = request.getParameter("email");

        if (emailAddress != null && emailAddress.length() > 0)
        {
            gmail.send(emailAddress, null, null, null, "Some photos", "Here are some photos", photos.toArray(new DataSource[]{}));
    %><h2> Email Sent!</h2><%
    }
%>
</div>
    <%}%>
</html>