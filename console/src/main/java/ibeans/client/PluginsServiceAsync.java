package ibeans.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

import ibeans.client.model.Plugin;

/**
 * TODO
 */
public interface PluginsServiceAsync
{

    void getInstalledPlugins(AsyncCallback<List<Plugin>> async);

    void updatePlugins(List<Plugin> plugins, AsyncCallback<Void> async);

    void getRunningWebapps(AsyncCallback<List<Plugin>> async);
}
