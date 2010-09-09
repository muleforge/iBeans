package ibeans.client;

import com.google.gwt.user.client.rpc.RemoteService;

import ibeans.client.model.AppInfo;

/**
 * TODO
 */
public interface ApplicationService extends RemoteService
{
    AppInfo getApplicationInfo();
}