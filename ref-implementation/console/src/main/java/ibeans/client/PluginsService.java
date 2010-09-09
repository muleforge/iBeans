package ibeans.client;

import com.google.gwt.user.client.rpc.RemoteService;

import java.util.List;

import ibeans.client.model.Plugin;

/**
 * TODO
 */
public interface PluginsService extends RemoteService
{
    List<Plugin> getInstalledPlugins() throws ClientIBeansException;

    void updatePlugins(List<Plugin> plugins) throws ClientIBeansException;

    List<Plugin> getRunningWebapps() throws ClientIBeansException;

}
