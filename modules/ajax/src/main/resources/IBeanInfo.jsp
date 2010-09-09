<%@ page import="org.mule.api.MuleContext" %>
<%@ page import="org.mule.api.config.MuleProperties" %>
<%@ page import="org.mule.ibeans.config.IBeanHolder" %>
<%@ page import="static org.mule.ibeans.IBeansSupport.select" %>
<%@ page import="java.util.Collection" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Simple jsp page with an iBean</title>

    <link rel="stylesheet" href="ibeans-examples.css"/>
</head>
<body>
<%
    MuleContext ctx = (MuleContext) getServletConfig().getServletContext().getAttribute(MuleProperties.MULE_CONTEXT_PROPERTY);
    String id = (String) request.getAttribute("ibeanId");
    if (id == null)
    {
%>
<table class="ibeansTable"><%
    Collection<IBeanHolder> ibeans = ctx.getRegistry().lookupObjects(IBeanHolder.class);
    for (IBeanHolder ibean : ibeans)
    {
%>
    <tr>
        <td><a href="<%=ibean.getId()%>/info"><%=ibean.getId()%>
        </a>


        </td>

    </tr>
    }
    %>
</table>
<%
        }
    }

%>
</body>
</html>