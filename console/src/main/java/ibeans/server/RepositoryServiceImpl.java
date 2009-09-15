package ibeans.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ibeans.client.ClientIBeansException;
import ibeans.client.RepositoryService;
import ibeans.client.model.Plugin;

/**
 * TODO
 */
public class RepositoryServiceImpl extends RemoteServiceServlet implements RepositoryService
{

    private Properties props = new Properties();
    private IBeansStoreClient client;

    public RepositoryServiceImpl() throws ClientIBeansException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream("ibeans.properties");
        if (is == null)
        {
            throw new ClientIBeansException("Could not load 'ibeans.properites' from the classpath. Make sure this file is present in IBEANS_HOME/conf");
        }
        props = new Properties();
        try
        {
            props.load(is);
        }
        catch (IOException e)
        {
            throw new ClientIBeansException("failed to read 'ibeans.properties'", e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                //ignore
            }
        }
        client = new IBeansStoreClient(
                props.getProperty(IBeansProperties.PROPERTY_USERNAME),
                props.getProperty(IBeansProperties.PROPERTY_PASSWORD),
                props.getProperty(IBeansProperties.PROPERTY_URL));
    }

    public List<Plugin> getAvailableIBeans() throws ClientIBeansException
    {
        return client.getAllPlugins();
    }

    public List<Plugin> getAvailableModules() throws ClientIBeansException
    {
        List<Plugin> list = new ArrayList<Plugin>();
        list.add(new Plugin("bar", "Bar Module", "Does some crazy stuff in bars", "1.0", Plugin.TYPE_MODULE));
        return list;
    }
}