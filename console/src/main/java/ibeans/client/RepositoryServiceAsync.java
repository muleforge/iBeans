package ibeans.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

import ibeans.client.model.Plugin;

/**
 * TODO
 */
public interface RepositoryServiceAsync
{
    void getAvailableIBeans(AsyncCallback<List<Plugin>> async) throws ClientIBeansException;

    void getAvailableModules(AsyncCallback<List<Plugin>> async) throws ClientIBeansException;
}
