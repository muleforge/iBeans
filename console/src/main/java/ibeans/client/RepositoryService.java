package ibeans.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import java.util.List;

import ibeans.client.model.Plugin;

/**
 * TODO
 */
public interface RepositoryService extends RemoteService
{
    public List<Plugin> getAvailableIBeans() throws ClientIBeansException;

    public List<Plugin> getAvailableModules() throws ClientIBeansException;

    /**
     * Utility/Convenience class.
     * Use RepositoryService.App.getInstance() to access static instance of RepositoryServiceAsync
     */
    public static class App
    {
        private static final RepositoryServiceAsync ourInstance;

        static
        {
            ourInstance = (RepositoryServiceAsync) GWT.create(RepositoryService.class);
            ((ServiceDefTarget) ourInstance).setServiceEntryPoint(GWT.getModuleBaseURL() + "ibeans.Console/RepositoryService");
        }

        public static RepositoryServiceAsync getInstance()
        {
            return ourInstance;
        }
    }
}
