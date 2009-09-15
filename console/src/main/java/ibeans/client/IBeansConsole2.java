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
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
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
import ibeans.client.util.InlineFlowPanel;

/**
 * TODO
 */
public class IBeansConsole2 implements EntryPoint//, ValueChangeHandler<String>
{

    public static final String WILDCARD = "*";
    private static final String DEFAULT_PAGE = "browse";

    private InlineFlowPanel rightHeaderPanel;

    private PluginsServiceAsync pluginsService;
    private RepositoryServiceAsync repositoryService;
    private ApplicationServiceAsync applicationService;

    protected TabPanel tabPanel;
    //protected WUser user;
    protected int oldTab;

    protected int adminTabIndex;
    protected Viewport base;
    //protected PropertyInterfaceManager propertyInterfaceManager = new PropertyInterfaceManager();
    protected List extensions;
    protected Label product;
    protected FlowPanel footerPanel;

    protected List<String> tabNames = new ArrayList<String>();
    protected int repositoryTabIndex;
    private ContentPanel centerPanel;
    private List<StatusItem> statusList = new ArrayList<StatusItem>();
    private StatusPanel statusBar;


    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        this.pluginsService = (PluginsServiceAsync) GWT.create(PluginsService.class);
        this.repositoryService = (RepositoryServiceAsync) GWT.create(RepositoryService.class);
        this.applicationService = (ApplicationServiceAsync) GWT.create(ApplicationService.class);

        ServiceDefTarget target = (ServiceDefTarget) pluginsService;
        // Use this so we can run in hosted mode but rewrite the URL once the app is deployed
        //String baseUrl = "/ibeans/ibeans.Console"; // GWT.getModuleBaseURL();
        String baseUrl = GWT.getModuleBaseURL();
        target.setServiceEntryPoint(baseUrl + "/PluginsService");

        target = (ServiceDefTarget) repositoryService;
        target.setServiceEntryPoint(baseUrl + "/RepositoryService");

        target = (ServiceDefTarget) applicationService;
        target.setServiceEntryPoint(baseUrl + "/ApplicationService");

        //GXT.setDefaultTheme(Theme.GRAY, true);
        GXT.BLANK_IMAGE_URL = "gxt/images/default/s.gif";
        //final String LOGO = "images/tcat_logo_main.gif";

        // prefetch the image, so that e.g. SessionKilled dialog can be properly displayed for the first time
        // when the server is already down and cannot serve it.
        Image.prefetch("images/lightbox.png");

        base = new Viewport();
        base.setLayout(new BorderLayout());

        createHeader(null /*new Image(LOGO)*/);

        tabPanel = new TabPanel();
        tabPanel.setAutoHeight(true);
        tabPanel.setBodyBorder(false);
        tabPanel.setAutoWidth(true);

        createBody();

        //createNav();

//        registryService.getUserInfo(new AbstractCallback(repositoryPanel) {
//            public void onSuccess(Object o) {
//                user = (WUser) o;
//
//                // always the left most item
//                rightHeaderPanel.insert(new Label("Welcome, " + user.getName()), 0);
//
//                suppressTabHistory = true;
//                loadTabs(console2);
//                suppressTabHistory = false;
//                showFirstPage();
//            }
//        });


        loadTabs(this);
        //createStatusBar();

        createFooter();

        RootPanel.get().add(base);
        base.layout(true);
        updateStatus(Status.INFO, "Ready for action");

    }

    public PluginsServiceAsync getPluginsService()
    {
        return pluginsService;
    }

    public RepositoryServiceAsync getRepositoryService()
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
        updateStatus(Status.ERROR, t.getMessage() + " (" + t.getClass().getName() + ")");
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

        applicationService.getApplicationInfo(new AsyncCallback<AppInfo>()
        {
            public void onFailure(Throwable throwable)
            {
                errorStatus(throwable);
            }

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

        applicationService.getApplicationInfo(new AsyncCallback<AppInfo>()
        {
            public void onFailure(Throwable throwable)
            {
                errorStatus(throwable);
            }

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

//        ExternalHyperlink logout = new ExternalHyperlink("Log Out", GWT.getHostPageBaseURL() + "j_logout");
//        options.add(newSpacerPipe());
//        options.add(logout);

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


    public int getRepositoryTab()
    {
        return repositoryTabIndex;
    }

    protected void loadTabs(final IBeansConsole2 console)
    {
        final TabItem welcomeTab = new TabItem();
        welcomeTab.setText("Welcome");
        welcomeTab.setScrollMode(Style.Scroll.AUTOX);
        RequestBuilder req = new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + "welcome.html");
        req.setCallback(new RequestCallback()
        {
            public void onResponseReceived(Request request, Response response)
            {
                welcomeTab.add(new Html(response.getText()));
                final CheckBox box = new CheckBox(" Do not show this screen in future");
                //box.setStyleName("welcome-check");
                box.addClickHandler(new ClickHandler()
                {
                    public void onClick(ClickEvent event)
                    {
                        Info.display("Display", "Do not display in future " + box.getValue());
                    }
                });
                welcomeTab.add(box);
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


        TabItem configTab = new TabItem();
        configTab.setText("Configure");
        configTab.setLayout(new FlowLayout());
        configTab.add(new InstalledPluginsPanel(console));
        configTab.layout();

        TabItem storeTab = new TabItem();
        storeTab.setText("iBeans Store");
        storeTab.add(new AvailablePluginsPanel(console));

        TabItem examplesTab = new TabItem();
        examplesTab.setText("Examples");
        examplesTab.add(new ExamplesPanel(console));

        tabPanel.add(welcomeTab);
        tabPanel.add(examplesTab);
        tabPanel.add(configTab);
        tabPanel.add(storeTab);
    }

    protected InstalledPluginsPanel createInstalledPluginsPanel(final IBeansConsole2 console)
    {
        return new InstalledPluginsPanel(console);
    }

    protected AvailablePluginsPanel createAvailablePluginsPanel(final IBeansConsole2 console)
    {
        return new AvailablePluginsPanel(console);
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

//    protected boolean showAdminTab(WUser user) {
//        for (Iterator<String> itr = user.getPermissions().iterator(); itr.hasNext();) {
//            String s = itr.next();
//
//            if (s.startsWith("MANAGE_") || "EXECUTE_ADMIN_SCRIPTS".equals(s)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    protected void showFirstPage() {
//        // Show the initial screen.
//        String initToken = History.getToken();
//        if (initToken.length() > 0) {
//            onHistoryChanged(initToken);
//        } else {
//            show("browse");
//        }
//    }
//
//
//
//    /**
//     * Shows a page, but does not trigger a history event.
//     *
//     * @param token
//     */
//    public void show(String token) {
//        show(getPageInfo(token), getParams(token));
//    }
//
//    protected void show(PageInfo page, List<String> params) {
//        TabItem p = (TabItem) tabPanel.getWidget(page.getTabIndex());
//
//        if (!tabPanel.getSelectedItem().equals(p)) {
//            tabPanel.setSelection(p);
//        }
//
//        p.removeAll();
//        p.layout();
//
//        Widget instance = page.getInstance();
//        p.add(instance);
//        p.layout();
//
//        if (instance instanceof Showable) {
//            ((Showable) instance).showPage(params);
//        }
//    }

//    public void onValueChange(ValueChangeEvent<String> event) {
//        onHistoryChanged(event.getValue());
//    }

//    public void onHistoryChanged(String token) {
//        suppressTabHistory = true;
//        currentToken = token;
//        if ("".equals(token)) {
//            token = DEFAULT_PAGE;
//        }
//
//        if ("nohistory".equals(token) && curInfo != null) {
//            suppressTabHistory = false;
//            return;
//        }
//
//        PageInfo page = getPageInfo(token);
//        List<String> params = getParams(token);
//
//        // hide the previous page
//        if (curInfo != null) {
//            Widget instance = curInfo.getInstance();
//            if (instance instanceof Showable) {
//                ((Showable) instance).hidePage();
//            }
//        }
//
//        if (page == null) {
//            // went to a page which isn't in our history anymore. go to the first page
//            if (curInfo == null) {
//                onHistoryChanged(DEFAULT_PAGE);
//            }
//        } else {
//            curInfo = page;
//
//            int idx = page.getTabIndex();
//            if (idx >= 0 && idx < tabPanel.getItemCount()) {
//                tabPanel.setSelection(tabPanel.getItem(page.getTabIndex()));
//            }
//            show(page, params);
//        }
//
//        suppressTabHistory = false;
//    }
//
//
//    private List<String> getParams(String token) {
//        List<String> params = new ArrayList<String>();
//        String[] split = token.split("/");
//
//        if (split.length > 1) {
//            for (int i = 1; i < split.length; i++) {
//                params.add(split[i]);
//            }
//        }
//        return params;
//    }
//
//    public String getCurrentToken() {
//        return currentToken;
//    }
//
//    public PageInfo getPageInfo(String token) {
//        PageInfo page = history.get(token);
//
//        if (page == null) {
//
//            // hack to match "foo/*" style tokens
//            int slashIdx = token.indexOf("/");
//            if (slashIdx != -1) {
//                page = history.get(token.substring(0, slashIdx) + "/" + WILDCARD);
//            }
//
//            if (page == null) {
//                page = history.get(token.substring(0, slashIdx));
//            }
//        }
//
//        return page;
//    }
//
//    public void setMessageAndGoto(String token, String message) {
//        PageInfo pi = getPageInfo(token);
//
//        ErrorPanel ep = (ErrorPanel) pi.getInstance();
//
//        History.newItem(token);
//
//        ep.setMessage(message);
//    }

//    public PropertyInterfaceManager getPropertyInterfaceManager() {
//        return propertyInterfaceManager;
//    }
//
//    public List getExtensions() {
//        return extensions;
//    }
//
//    public RegistryServiceAsync getRegistryService() {
//        return registryService;
//    }
//
//    public SecurityServiceAsync getSecurityService() {
//        return securityService;
//    }
//
//    public HeartbeatServiceAsync getHeartbeatService() {
//        return this.heartbeatService;
//    }
//
//    public AdminServiceAsync getAdminService() {
//        return adminService;
//    }

//    public TabPanel getTabPanel() {
//        return tabPanel;
//    }
//
//    public BaseConstants getBaseConstants() {
//        return baseConstants;
//    }
//
//    public BaseMessages getBaseMessages() {
//        return baseMessages;
//    }
//
//    public boolean hasPermission(String perm) {
//        for (Iterator<String> itr = user.getPermissions().iterator(); itr.hasNext();) {
//            String s = itr.next();
//
//            if (s.startsWith(perm)) return true;
//        }
//        return false;
//    }
//
//    public int getAdminTab() {
//        return adminTabIndex;
//    }
//
//    public void addHistoryListener(String token, AbstractShowable composite) {
//        historyListeners.put(token, composite);
//    }
//
//    public WExtensionInfo getExtension(String id) {
//        for (Iterator itr = extensions.iterator(); itr.hasNext();) {
//            WExtensionInfo ei = (WExtensionInfo) itr.next();
//
//            if (id.equals(ei.getId())) {
//                return ei;
//            }
//        }
//        return null;
//    }

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
}
