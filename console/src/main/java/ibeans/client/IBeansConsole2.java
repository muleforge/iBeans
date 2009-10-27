/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.client;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ibeans.client.model.AppInfo;
import ibeans.client.util.ExternalHyperlink;
import ibeans.client.util.InlineFlowPanel;

/**
 * TODO
 */
public class IBeansConsole2 implements EntryPoint
{
    private InlineFlowPanel rightHeaderPanel;

    private PluginsServiceAsync pluginsService;
    private IBeansCentralServiceAsync repositoryService;
    private ApplicationServiceAsync applicationService;

    protected TabPanel tabPanel;
    protected Viewport base;
    protected Label product;
    protected FlowPanel footerPanel;

    private ContentPanel centerPanel;
    private List<StatusItem> statusList = new ArrayList<StatusItem>();
    private StatusPanel statusBar;
    private UserInfo user;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        this.pluginsService = (PluginsServiceAsync) GWT.create(PluginsService.class);
        this.repositoryService = (IBeansCentralServiceAsync) GWT.create(IBeansCentralService.class);
        this.applicationService = (ApplicationServiceAsync) GWT.create(ApplicationService.class);

        ServiceDefTarget target = (ServiceDefTarget) pluginsService;
        // Use this so we can run in hosted mode but rewrite the URL once the app is deployed
        String baseUrl = "/ibeans/ibeans.Console/"; // GWT.getModuleBaseURL();
        //String baseUrl = GWT.getModuleBaseURL();
        target.setServiceEntryPoint(baseUrl + "PluginsService");

        target = (ServiceDefTarget) repositoryService;
        target.setServiceEntryPoint(baseUrl + "IBeansCentralService");

        target = (ServiceDefTarget) applicationService;
        target.setServiceEntryPoint(baseUrl + "ApplicationService");

        GXT.BLANK_IMAGE_URL = "gxt/images/default/s.gif";

        // prefetch the image, so that e.g. SessionKilled dialog can be properly displayed for the first time
        // when the server is already down and cannot serve it.
        Image.prefetch("images/lightbox.png");

        user = getUserInfo();

        base = new Viewport();
        base.setLayout(new BorderLayout());

        createHeader(null /*new Image(LOGO)*/);

        tabPanel = new TabPanel();
        tabPanel.setAutoHeight(true);
        tabPanel.setBodyBorder(false);
        tabPanel.setAutoWidth(true);

        createBody();
        loadTabs(this);
        createFooter();

        RootPanel.get().add(base);
        base.layout(true);
        updateStatus(Status.INFO, "Ready for action");

    }

    public PluginsServiceAsync getPluginsService()
    {
        return pluginsService;
    }

    public IBeansCentralServiceAsync getRepositoryService()
    {
        return repositoryService;
    }

    public void updateStatus(ibeans.client.Status status, String text)
    {
        String style;
        switch (status)
        {
            case TIP:
                statusBar.setInfo(text);
                break;
            case INFO:
                statusBar.setInfo(text);
                break;
            case WARNING:
                statusBar.setWarning(text);
                break;
            case ERROR:
                statusBar.setError(text);
                break;
            default:
                statusBar.setInfo(text);
        }

        statusList.add(new StatusItem(statusBar.getCurrentIconStyle(), text));
    }

    public void errorStatus(Throwable t)
    {
        if (t.getMessage().startsWith("<"))
        {
            new HTMLDialog("Server Error", t.getMessage()).show();
            updateStatus(Status.ERROR, "Server Error, see log: " + " (" + t.getClass().getName() + ")");
        }
        else
        {
            updateStatus(Status.ERROR, t.getMessage() + " (" + t.getClass().getName() + ")");
        }
    }

    private void createFooter()
    {
        ContentPanel southPanel = new ContentPanel();
        southPanel.setBorders(false);
        southPanel.setHeaderVisible(false);

        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.SOUTH, 60);
        data.setMargins(new Margins());

        footerPanel = new FlowPanel();
        footerPanel.setStyleName("footer");

        statusBar = new StatusPanel();

        footerPanel.add(statusBar);
        InlineFlowPanel bottom = new InlineFlowPanel();
        footerPanel.add(bottom);
        prependFooterConent(bottom);
        southPanel.add(footerPanel);
        base.add(southPanel, data);
    }


    /**
     * adds to the left of the  copyright info
     */
    protected void prependFooterConent(final FlowPanel panel)
    {

        applicationService.getApplicationInfo(new AbstractAsyncCallback<AppInfo>(this)
        {
            public void onSuccess(AppInfo info)
            {
                product = new Label("About " + info.getName());
                product.setStyleName("footer-link");
                product.addClickHandler(new ClickHandler()
                {
                    public void onClick(ClickEvent arg0)
                    {
                        new AboutPanel();
                    }
                });
                panel.add(product);
                panel.add(newSpacerPipe());

                Label copyright = new Label(info.getCopyright());
                //copyright.setStyleName("footer-text");
                panel.add(copyright);
            }
        });
    }

    public Label newSpacerPipe()
    {
        Label pipe = new Label(" | ");
        pipe.setStyleName("pipe-with-space");
        return pipe;
    }


    protected void createHeader(Image logo)
    {
        ContentPanel northPanel = new ContentPanel();
        northPanel.setBorders(false);
        northPanel.setHeaderVisible(false);

        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 33);
        data.setMargins(new Margins());
        data.setSize(50);

        rightHeaderPanel = new InlineFlowPanel();
        rightHeaderPanel.setStyleName("header-right");
        rightHeaderPanel.add(createHeaderOptions());

        // custom logo
        final FlowPanel header = new FlowPanel();
        header.setStyleName("header");
        header.add(rightHeaderPanel);
//        logo.setTitle("Home");
//        logo.addStyleName("gwt-Hyperlink");
        northPanel.add(header);
        base.add(northPanel, data);

        applicationService.getApplicationInfo(new AbstractAsyncCallback<AppInfo>(this)
        {
            public void onSuccess(AppInfo appInfo)
            {
                Label head = new Label(appInfo.getName());
                head.setStyleName("header-title");
                header.add(head);
                Label subhead = new Label(appInfo.getVersion());
                subhead.setStyleName("header-sub-title");
                header.add(subhead);
            }
        });

    }

    protected InlineFlowPanel createHeaderOptions()
    {

        InlineFlowPanel options = new InlineFlowPanel();
        options.setStyleName("header-right-options");

        if (user.getUser() != null)
        {
            Label l = new Label("Welcome, " + user.getUser());
            options.add(l);

            options.add(newSpacerPipe());

            Label l2 = new Label("Log Out");
            l2.setStyleName("faux-link");
            options.add(l2);
            l2.addClickHandler(new ClickHandler()
            {
                public void onClick(ClickEvent clickEvent)
                {
                    clearUserInfo();
                    rightHeaderPanel.remove(0);
                    rightHeaderPanel.add(createHeaderOptions());
                }
            });
        }
        else
        {
            Label l = new Label("Log In");
            l.setStyleName("faux-link");

            l.addClickHandler(new ClickHandler()
            {
                public void onClick(ClickEvent clickEvent)
                {
                    Window window = new Window();
                    DownloadWindow login = new DownloadWindow(null, null, null, null, window, IBeansConsole2.this);
                    window.add(login);
                    window.show();
                }
            });
            options.add(l);
        }

        options.add(newSpacerPipe());
        options.add(new ExternalHyperlink("Docs", "http://www.mulesoft.org/display/IBEANS", "_blank"));
        options.add(newSpacerPipe());
        options.add(new ExternalHyperlink("Help", "http://www.mulesoft.org/display/IBEANS/Managing+iBeans", "_blank"));

        return options;
    }

    protected void createBody()
    {
        centerPanel = new ContentPanel();
        centerPanel.setBorders(false);
        centerPanel.setHeaderVisible(false);
        centerPanel.setScrollMode(Style.Scroll.NONE);
        centerPanel.setLayout(new FlowLayout());
        centerPanel.add(tabPanel);

        BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
        data.setMargins(new Margins());

        base.add(centerPanel, data);
    }

    protected void loadTabs(final IBeansConsole2 console)
    {
        if (user.isShowWelcome())
        {
            final TabItem welcomeTab = new TabItem();
            welcomeTab.setText("Welcome");
            welcomeTab.setScrollMode(Style.Scroll.AUTOX);
            RequestBuilder req = new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + "welcome-beta.html");
            req.setCallback(new RequestCallback()
            {
                public void onResponseReceived(Request request, Response response)
                {
                    welcomeTab.add(new Html(response.getText()));
//                    final CheckBox box = new CheckBox(" Do not show this screen in future");
//                    //box.setStyleName("welcome-check");
//                    box.addClickHandler(new ClickHandler()
//                    {
//                        public void onClick(ClickEvent event)
//                        {
//                            user.setShowWelcome(box.getValue());
//                            saveUserInfo(user);
//                        }
//                    });
//                    welcomeTab.add(box);
                    welcomeTab.layout();
                }

                public void onError(Request request, Throwable exception)
                {
                    errorStatus(exception);
                }
            });
            try
            {
                req.send();
            }
            catch (RequestException e)
            {
                errorStatus(e);
            }
            tabPanel.add(welcomeTab);
        }


        TabItem configTab = new TabItem();
        configTab.setText("Configure");
        configTab.setLayout(new FlowLayout());
        configTab.add(new InstalledPluginsPanel(console));
        configTab.layout();

        TabItem storeTab = new TabItem();
        storeTab.setText("iBeans Central");
        storeTab.add(new IBeansCentralPanel(console));

        TabItem examplesTab = new TabItem();
        examplesTab.setText("Examples");
        examplesTab.add(new ExamplesPanel(console));

        tabPanel.add(examplesTab);
        tabPanel.add(configTab);
        tabPanel.add(storeTab);
    }

    protected TabItem createEmptyTab(String name, String toolTip)
    {
        TabItem tab = new TabItem();
        TabItem.HeaderItem header = tab.getHeader();
        header.setText(name);

        if (toolTip != null)
        {
            header.setToolTip(toolTip);
        }
        tab.setLayout(new FlowLayout());
        return tab;
    }

    protected TabItem createEmptyTab(String name)
    {
        return createEmptyTab(name, null);
    }

    UserInfo getUserInfo()
    {
        if (user == null)
        {
            String rawCookie = Cookies.getCookie("ibeans-console");
            if (rawCookie.equals(""))
            {
                UserInfo info = new UserInfo();
                return info;
            }
            else
            {
                return new UserInfo(rawCookie);
            }
        }
        else
        {
            return user;
        }
    }

    void saveUserInfo(final UserInfo info)
    {
        if (info.getUser() != null)
        {
            this.getRepositoryService().verifyUser(info.getUser(), info.getPass(), new AbstractAsyncCallback<Boolean>(this)
            {
                public void onSuccess(Boolean b)
                {
                    if (b)
                    {
                        doSaveUser(info);
                        updateStatus(Status.INFO, "Welcome " + info.getUser() + "!");
                    }
                    else
                    {
                        this.onFailure(new ClientIBeansException("Username or password is incorrect"));
                    }
                }
            });
        }
        else
        {
            doSaveUser(info);
        }

    }

    private void doSaveUser(UserInfo info)
    {
        Date now = new Date();
        now.setYear(now.getYear() + 1);
        Cookies.setCookie("ibeans-console", info.toString(), now);
        user = info;
        rightHeaderPanel.remove(0);
        rightHeaderPanel.add(createHeaderOptions());
    }

    void clearUserInfo()
    {
        saveUserInfo(new UserInfo());
    }

    private class StatusItem implements ModelData, Serializable
    {
        private HashMap<String, Object> data = new HashMap<String, Object>();

        private StatusItem(String type, String text)
        {
            data.put("type", type);
            data.put("text", text);
            data.put("timestamp", new Date());
        }

        public <X> X get(String property)
        {
            return (X) data.get(property);
        }

        public Map<String, Object> getProperties()
        {
            return data;
        }

        public Collection<String> getPropertyNames()
        {
            return data.keySet();
        }

        public <X> X remove(String property)
        {
            return (X) data.remove(property);
        }

        public <X> X set(String property, X value)
        {
            return (X) data.put(property, value);
        }

        public String getType()
        {
            return (String) data.get("type");
        }

        public String getText()
        {
            return (String) data.get("text");
        }

        public Date getTimestamp()
        {
            return (Date) data.get("timestamp");
        }
    }


    class UserInfo
    {
        private String user;
        private String pass;
        private boolean showWelcome = true;

        UserInfo()
        {
        }

        UserInfo(String raw)
        {
            if (raw == null)
            {
                return;
            }

            int i = raw.indexOf(";");
            while (i > 0 && i < raw.length())
            {
                String pair = raw.substring(0, i);
                int x = pair.indexOf("=");
                String key = pair.substring(0, x);
                String value = pair.substring(x + 1);
                if (key.equals("user"))
                {
                    setUser(value);
                }
                else if (key.equals("pass"))
                {
                    setPass(value);
                }
                else if (key.equals("showWelcome"))
                {
                    setShowWelcome(Boolean.parseBoolean(value));
                }
                raw = raw.substring(i + 1);
                i = raw.indexOf(";");
            }
        }

        public String getUser()
        {
            return user;
        }

        public void setUser(String user)
        {
            this.user = user;
        }

        public String getPass()
        {
            return pass;
        }

        public void setPass(String pass)
        {
            this.pass = pass;
        }

        public boolean isShowWelcome()
        {
            return showWelcome;
        }

        public void setShowWelcome(boolean showWelcome)
        {
            this.showWelcome = showWelcome;
        }

        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            if (user != null)
            {
                buf.append("user=").append(user).append(";");
                buf.append("pass=").append(pass).append(";");
                buf.append("showWelcome=").append(showWelcome).append(";");
            }
            return buf.toString();
        }

    }
}
