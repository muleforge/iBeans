package ibeans.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import ibeans.client.model.AppInfo;

public interface ApplicationServiceAsync
{

    void getApplicationInfo(AsyncCallback<AppInfo> async);
}
