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

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.List;
import java.util.ArrayList;

/**
 * TODO
 */
public class PluginsPanel extends com.extjs.gxt.ui.client.widget.TabPanel
{
    protected List<String> tabNames = new ArrayList<String>();

    public  PluginsPanel(IBeansConsole2 console)
    {
        tabNames.add("installed");
        tabNames.add("available");

        //tabPanel = new com.extjs.gxt.ui.client.widget.TabPanel();
       setStyleName("x-tab-panel-header_sub1");
        setAutoWidth(true);
        setAutoHeight(true);

        addListener(Events.Select, new SelectionListener<TabPanelEvent>()
        {

            @Override
            public void componentSelected(TabPanelEvent ce)
            {
                TabItem item = ce.getItem();
                int newTab = getItems().indexOf(item);
                History.newItem(tabNames.get(newTab));
            }

        });

        loadTabs(console);
    }

    protected void loadTabs(final IBeansConsole2 console)
    {
        TabItem installedTab = new TabItem();
        installedTab.setText("Installed");
        installedTab.add(createInstalledPluginsPanel(console));

        TabItem availableTab = new TabItem();
        availableTab.setText("iBeans Store");
        availableTab.add(createAvailablePluginsPanel(console));
        add(installedTab);
        add(availableTab);
    }

    protected TabItem createEmptyTab(String name)
    {
        TabItem tab = new TabItem();
        tab.setText(name);
        tab.setLayout(new FlowLayout());
        return tab;
    }

    protected InstalledPluginsPanel createInstalledPluginsPanel(final IBeansConsole2 console)
    {
        return  new InstalledPluginsPanel(console);
    }

    protected AvailablePluginsPanel createAvailablePluginsPanel(final IBeansConsole2 console)
    {
        return new AvailablePluginsPanel(console);
    }
}
