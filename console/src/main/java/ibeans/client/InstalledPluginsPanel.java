package ibeans.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

import ibeans.client.model.Plugin;
import ibeans.client.model.PluginData;

public class InstalledPluginsPanel extends LayoutContainer
{

    private ContentPanel panel;
    private IBeansConsole2 iBeansConsole;
    private Grid<PluginData> grid;

    public InstalledPluginsPanel(IBeansConsole2 iBeansConsole)
    {
        this.iBeansConsole = iBeansConsole;
        panel = new ContentPanel();
        //panel.setSpacing(10);
        panel.setAutoHeight(true);
        panel.setBodyBorder(false);
        panel.setHeaderVisible(true);
        panel.setLayout(new FlowLayout());
        panel.setAutoWidth(true);

        this.setAutoHeight(true);
        this.setLayout(new FlowLayout());
        this.setAutoWidth(true);
    }

    @Override
    protected void onRender(Element parent, int index)
    {
        super.onRender(parent, index);
        createGrid();
        add(panel);
    }

    protected void loadData(final ListStore<PluginData> store) throws ClientIBeansException
    {
        PluginsServiceAsync service = iBeansConsole.getPluginsService();
        service.getInstalledPlugins(new AsyncCallback<List<Plugin>>()
        {
            public void onFailure(Throwable caught)
            {
                iBeansConsole.errorStatus(caught);
            }

            public void onSuccess(List<Plugin> result)
            {
                for (Plugin plugin : result)
                {
                    store.add(new PluginData(plugin));
                    if (plugin.getWarning() != null)
                    {
                        iBeansConsole.updateStatus(Status.WARNING, plugin.getWarning());
                    }
                }
                iBeansConsole.updateStatus(Status.INFO, "Plugin data loaded");
                grid.getView().scrollToTop();
            }
        });
    }

    protected void createGrid()
    {
        ListStore<PluginData> store = new ListStore<PluginData>();
        try
        {
            loadData(store);
        }
        catch (ClientIBeansException e)
        {
            iBeansConsole.updateStatus(Status.ERROR, "Failed to load data from server: " + e.getMessage());
        }

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        GridSelectionModel<PluginData> sm = new GridSelectionModel<PluginData>();

//        String template = "<table cellpadding='10' cellspacing='10'><tr>\n" +
//                "    <td rowspan=\"3\" valign=\"top\" width=\"58\"><img src=\"http://content.mulesoft.org/images/iconModule.jpg\" alt=\"Module\" height=\"54\" width=\"58\"></td>\n" +
//                "    <td width='*'>{description}</td>\n" +
//                "    <td valign=\"top\" width=\"120\"><b>Rating:</b> <img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Unique Visitors Last Month\"><img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Rating for this version\"><img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Unique Visitors Last Month\"><img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Unique Visitors Last Month\"><img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Unique Visitors Last Month\">    </td>\n" +
//                "  </tr><tr><td><b>Author:</b> {author}</td><td><b>Downloads:</b> {downloads}</td></tr>" +
//                "<tr><td><b>URL:</b> <a href='{url}' target='_blank'>{url}</a></td><td><b>Comments:</b> {commentsCount} <a href='{commentsUrl}' target='_blank'>view</a></td></tr>" +
//                "<tr><td></td><td><b>License:<b> <a href='{licenseUrl}' target='_blank'>{licenseName}</a></td><td></td></tr></table>";

        String template = "<table cellpadding='10' cellspacing='10'><tr>\n" +
                "    <td rowspan=\"3\" valign=\"top\" width=\"58\"><img src=\"images/{type}.jpg\" height=\"54\" width=\"58\"></td>\n" +
                "    <td width='*' colspan='2'>{description}</td>\n" +
                "  </tr><tr><td><b>Author:</b> <a href='${authorUrl}' target='_blank'>{author}</a></td><td></td></tr>" +
                "<tr><td><b>URL:</b> <a href='{url}' target='_blank'>{url}</a></td><td> </td></tr>" +
                "<tr><td></td><td><b>License:<b> <a href='{licenseUrl}' target='_blank'>{licenseName}</a></td><td></td></tr></table>";

        XTemplate tpl = XTemplate.create(template);
        RowExpander expander = new RowExpander(tpl);
        configs.add(expander);

        CheckColumnConfig checkColumn = new CheckColumnConfig("enabled", "Enabled", 55);
        CellEditor checkBoxEditor = new CellEditor(new CheckBox());
        checkColumn.setEditor(checkBoxEditor);
        configs.add(checkColumn);

        ColumnConfig column = new ColumnConfig("name", "Name", 300);
        configs.add(column);

        column = new ColumnConfig("type", "Type", 100);
        configs.add(column);

        column = new ColumnConfig("version", "Version", 100);
        column.setAlignment(HorizontalAlignment.RIGHT);
        //column.setRenderer(gridNumber);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        ContentPanel cp = new ContentPanel();
        cp.setHeading("List of iBeans and modules installed in this instance.");
        cp.setFrame(true);
        //cp.setIcon(Examples.ICONS.table());
        cp.setLayout(new FitLayout());
        cp.setAutoWidth(true);
        cp.setAutoHeight(true);

        grid = new EditorGrid<PluginData>(store, cm);
        grid.setSelectionModel(sm);
        grid.setBorders(true);
        grid.addPlugin(expander);
        grid.setSelectionModel(sm);
        grid.setAutoExpandColumn("name");
        grid.addListener(Events.BeforeEdit, new Listener<GridEvent>()
        {
            public void handleEvent(GridEvent be)
            {
                if ((Boolean) be.getModel().get("required"))
                {
                    be.setCancelled(true);
                    Info.display("Action Cancelled", "Item '{0}' cannot be disabled since it is required by Mule iBeans", (String) be.getModel().get("name"));
                }
            }
        });

        grid.getView().setAutoFill(true);
        grid.setAutoWidth(true);
        //TODO we need auto sizing
        grid.setHeight(300);

        cp.add(grid);
        panel.add(cp);


        // add buttons
        Button save = new Button("Save Changes", new SelectionListener<ButtonEvent>()
        {
            public void componentSelected(ButtonEvent ce)
            {
                final ListStore<PluginData> store = grid.getStore();
                List<Plugin> p = new ArrayList<Plugin>(store.getCount());

                for (int i = 0; i < store.getCount(); i++)
                {
                    p.add(store.getAt(i).toPlugin());
                }
                iBeansConsole.getPluginsService().updatePlugins(p, new AsyncCallback()
                {
                    public void onFailure(Throwable caught)
                    {
                        iBeansConsole.updateStatus(Status.ERROR, caught.getMessage());
                    }

                    public void onSuccess(Object result)
                    {
                        store.commitChanges();
                        iBeansConsole.updateStatus(Status.INFO, "Plugin information saved");
                    }
                });
            }
        });

        cp.addButton(save);

        Button cancel = new Button("Cancel", new SelectionListener<ButtonEvent>()
        {
            public void componentSelected(ButtonEvent ce)
            {
                try
                {
                    grid.getStore().removeAll();
                    loadData(grid.getStore());
                }
                catch (ClientIBeansException e)
                {
                    e.printStackTrace();
                }
            }
        });

        cp.addButton(cancel);
    }

}