package ibeans.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ibeans.client.model.Plugin;

/**
 * TODO
 */
public interface IBeansCentralService extends RemoteService
{
    public List<Plugin> getAvailableIBeans() throws ClientIBeansException;

    public List<Plugin> getAvailableModules() throws ClientIBeansException;

    public String downloadIBean(String user, String pass, String id, String version) throws ClientIBeansException;

    /**
     * Utility/Convenience class.
     * Use RepositoryService.App.getInstance() to access static instance of RepositoryServiceAsync
     */
    public static class App
    {
        private static final IBeansCentralServiceAsync ourInstance;

        static
        {
            ourInstance = (IBeansCentralServiceAsync) GWT.create(IBeansCentralService.class);
            ((ServiceDefTarget) ourInstance).setServiceEntryPoint(GWT.getModuleBaseURL() + "ibeans.Console/IBeansCentralService");
        }

        public static IBeansCentralServiceAsync getInstance()
        {
            return ourInstance;
        }
    }
}
