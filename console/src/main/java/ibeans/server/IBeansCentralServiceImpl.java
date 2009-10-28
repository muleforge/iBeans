package ibeans.server;

import org.mule.ibeans.IBeansContext;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.ibeanscentral.IBeanInfo;
import org.mule.ibeans.ibeanscentral.IbeansCentralIBean;
import org.mule.util.IOUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;

import ibeans.client.ClientIBeansException;
import ibeans.client.IBeansCentralService;
import ibeans.client.model.Plugin;

/**
 * TODO
 */
public class IBeansCentralServiceImpl extends RemoteServiceServlet implements IBeansCentralService
{
    private IbeansCentralIBean ibeansCentral;
    private IBeansContext iBeansContext;
    private File deployedIbeans;

    private Properties props = new Properties();

    @Override
    public void init() throws ServletException
    {
        super.init();

        InputStream is = getClass().getClassLoader().getResourceAsStream("ibeans.properties");
        if (is == null)
        {
            throw new ServletException("Could not load 'ibeans.properites' from the classpath. Make sure this file is present in IBEANS_HOME/conf");
        }
        props = new Properties();
        try
        {
            props.load(is);
        }
        catch (IOException e)
        {
            throw new ServletException("failed to read 'ibeans.properties'", e);
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }

        String catalinaHome = getServletConfig().getInitParameter("catalina.home");

        if (catalinaHome == null)
        {
            catalinaHome = System.getProperty("catalina.home");
        }

        if (catalinaHome == null)
        {
            throw new ServletException("The catalina.home property is not set that defines the home directory for Tomcat.  Set it either as an init parameter on PluginsServiceImpl servlet or as a system property");
        }

        String ibeansPath = catalinaHome + PluginsServiceImpl.IBEANS_BASE_NAME + PluginsServiceImpl.IBEANS_NAME + File.separator + PluginsServiceImpl.DEPLOYED_NAME;
        deployedIbeans = new File(ibeansPath);

        if (!deployedIbeans.exists())
        {
            throw new ServletException("Could not find Mule iBeans directory: " + ibeansPath + ". Make sure you have the catalina.home set or pass in the home path when running this util");
        }

        //TODO, listener does not fire in hsted mode for some reason
//        IBeansServletContextListener l = new IBeansServletContextListener();
//        l.initialize(getServletContext());

        iBeansContext = (IBeansContext) getServletContext().getAttribute(IBeansContext.CONTEXT_PROPERTY);
        ibeansCentral = iBeansContext.createIBean(IbeansCentralIBean.class);

        ibeansCentral.setCredentials(props.getProperty(IBeansProperties.PROPERTY_USERNAME),
                props.getProperty(IBeansProperties.PROPERTY_PASSWORD));

    }

    public List<Plugin> getAvailableIBeans() throws ClientIBeansException
    {
        try
        {
            List<IBeanInfo> ibeans = ibeansCentral.getIBeans();
            List<Plugin> plugins = new ArrayList<Plugin>(ibeans.size());
            for (IBeanInfo ibean : ibeans)
            {
                plugins.add(createPlugIn(ibean));
            }
            return plugins;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ClientIBeansException(e.getMessage());
        }
    }

    protected Plugin createPlugIn(IBeanInfo info) throws CallException
    {
        Plugin p = new Plugin(info.getShortName(), info.getName(), info.getDescription(), info.getVersion(), "iBean");
        p.setAuthor(info.getAuthorName());
        p.setAuthorUrl(info.getAuthorUrl());
        p.setLicenseName(info.getLicenseName());
        p.setLicenseUrl(info.getLicenseUrl());
        p.setUrl(info.getUrl());
        //p.setDownloadUrl(ibeansCentral.getIBeanDownloadUrl(info).toString());
        return p;
    }

    public String downloadIBean(String user, String pass, String id, String version) throws ClientIBeansException
    {
        IbeansCentralIBean userIbeansCentral = iBeansContext.createIBean(IbeansCentralIBean.class);
        userIbeansCentral.setCredentials(user, pass);
        FileOutputStream out = null;
        InputStream in = null;
        try
        {
            IBeanInfo ibean = userIbeansCentral.getIBeanByShortName(id, version);
            if (ibean == null)
            {
                throw new ClientIBeansException("iBean  not found: " + id + "(" + version + ")");
            }
            in = userIbeansCentral.downloadIBean(ibean.getDownloadUri());
            if (in != null)
            {
                File file = new File(deployedIbeans, ibean.getFullFileName());
                out = new FileOutputStream(file);
                IOUtils.copy(in, out);
                return "The ibean has been downloaded to: " + file;
            }
            else
            {
                throw new ClientIBeansException("Failed to download ibean from: " + ibean.getDownloadUri());
            }
        }
        catch (ClientIBeansException e)
        {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new ClientIBeansException(e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }


    public Boolean verifyUser(String user, String password) throws ClientIBeansException
    {
        IbeansCentralIBean userIbeansCentral = iBeansContext.createIBean(IbeansCentralIBean.class);
        try
        {
            return userIbeansCentral.verifyCredentials(user, password);
        }
        catch (CallException e)
        {
            throw new ClientIBeansException(e.getMessage());
        }
    }
}