<%@ page import="org.mule.ibeans.IBeansContext" %>
<%@ page import="org.mule.ibeans.flickr.FlickrIBean" %>
<%@ page import="org.mule.ibeans.flickr.FlickrSearchIBean" %>
<%@ page import="java.net.URL" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="static org.mule.ibeans.IBeansSupport.select" %>
<%@ page import="org.w3c.dom.Node" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Simple jsp page with an iBean</title>

    <link rel="stylesheet" href="ibeans-examples.css"/>
</head>
<%
    IBeansContext ibeansContext = (IBeansContext) config.getServletContext().getAttribute(IBeansContext.CONTEXT_PROPERTY);
    FlickrSearchIBean flickr = ibeansContext.createIBean(FlickrSearchIBean.class);
    String apiKey = (String) ibeansContext.getConfig().get("flickr.apiKey");
    flickr.init(apiKey, FlickrIBean.FORMAT.XML, Document.class);
%>
<body>
<h2>Flickr iBean Example</h2>

<form action="flickr.jsp" method="POST">

    <table>
        <tr>
            <td>Search Flickr:</td>
            <td><input id="search" class="input" type="text" name="search" value="Search"/></td>
            <td><input id="submit" class="button" type="submit" value="Search"/></td>
            <td>Size:</td>
            <td><select name="size">
                <option value="Small_Square">Small_Square</option>
                <option value="Thumbnail">Thumbnail</option>
                <option value="Small" selected="true">Small</option>
                <option value="Medium">Medium</option>
                <option value="Large">Large</option>
                <option value="Original">Original</option>
            </select></td>
            <td>Type:</td>
            <td><select name="type">
                <option value="Jpeg" selected="true">jpg</option>
                <option value="Gif">gif</option>
                <option value="Png">png</option>
            </select></td>
        </tr>
    </table>
</form>
    <%
    if (request.getMethod().equals("POST")) {%>

        <%--populate user values--%>
        <script type="text/javascript">
            document.getElementById("search").value = <%=request.getParameter("search")%>;
            document.getElementById("size").value = <%=request.getParameter("size")%>;
            document.getElementById("type").value = <%=request.getParameter("type")%>;
        </script>

<div id="results" class="photoGrid">
    <%
        Document doc = flickr.search(request.getParameter("search"));

        for (Node n : select("//photo", doc))
        {
            URL url = flickr.getPhotoURL(n, Enum.valueOf(FlickrIBean.IMAGE_SIZE.class, request.getParameter("size")),
                    Enum.valueOf(FlickrIBean.IMAGE_TYPE.class, request.getParameter("type")));
    %>
    <div class="photo">
        <img class src="<%=url.toString()%>" alt="Photo" title="<%=url.toString()%>">
    </div>
    <%}%>
</div>
    <%}%>
</html>