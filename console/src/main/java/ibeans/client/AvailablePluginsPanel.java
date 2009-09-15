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
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
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

public class AvailablePluginsPanel extends LayoutContainer
{

    private ContentPanel panel;
    private IBeansConsole2 iBeansConsole;
    private Grid<PluginData> grid;

    public AvailablePluginsPanel(IBeansConsole2 iBeansConsole)
    {
        this.iBeansConsole = iBeansConsole;
        panel = new ContentPanel();
        panel = new ContentPanel();
        //panel.setSpacing(10);
        panel.setAutoHeight(true);
        panel.setBodyBorder(false);
        panel.setHeaderVisible(true);
        panel.setLayout(new FitLayout());
        panel.setAutoWidth(true);
        this.setLayout(new FlowLayout());


    }

    @Override
    protected void onRender(Element parent, int index)
    {
        super.onRender(parent, index);
        setLayout(new FlowLayout(10));
        createGrid();
        add(panel);
    }

    protected void loadData(final ListStore<PluginData> store) throws ClientIBeansException
    {
        RepositoryServiceAsync service = iBeansConsole.getRepositoryService();

        service.getAvailableModules(new AsyncCallback<List<Plugin>>()
        {
            public void onFailure(Throwable caught)
            {
                iBeansConsole.updateStatus(Status.ERROR, caught.getMessage() + " (" + caught.getClass().getName() + ")");
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
                iBeansConsole.updateStatus(Status.INFO, "Available Module Plugin data loaded");
            }
        });

        service.getAvailableIBeans(new AsyncCallback<List<Plugin>>()
        {
            public void onFailure(Throwable caught)
            {
                iBeansConsole.updateStatus(Status.ERROR, caught.getMessage() + " (" + caught.getClass().getName() + ")");
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
                iBeansConsole.updateStatus(Status.INFO, "Available iBean Plugin data loaded");
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
            iBeansConsole.updateStatus(Status.ERROR, "Failed to load data from the iBeans Store: " + e.getMessage());
        }

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        GridSelectionModel<PluginData> sm = new GridSelectionModel<PluginData>();

//        String template = "<table cellpadding='10' cellspacing='10'><tr>\n" +
//                "    <td rowspan=\"3\" valign=\"top\" width=\"58\"><img src=\"http://content.mulesoft.org/images/iconModule.jpg\" alt=\"Module\" height=\"54\" width=\"58\"></td>\n" +
//                "    <td width='*'>{description}</td>\n" +
//                "    <td valign=\"top\" width=\"120\"><b>Rating:</b> <img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Unique Visitors Last Month\"><img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Rating for this version\"><img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Unique Visitors Last Month\"><img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Unique Visitors Last Month\"><img src=\"http://content.mulesoft.org/images/star-full.bmp\" alt=\"Unique Visitors Last Month\">    </td>\n" +
//                "  </tr><tr><td><b>Author:</b> {author}</td><td><b>Downloads:</b> {downloads}</td></tr>" +
//                "<tr><td><b>URL:</b> <a href='{url}' target='_blank'>{url}</a></td><td><b>Comments:</b> {commentsCount} <a href='{commentsUrl}' target='_blank'>view</a></td></tr></table>";

        String template = "<table cellpadding='10' cellspacing='10'><tr>\n" +
                "    <td rowspan=\"3\" valign=\"top\" width=\"58\"><img src=\"images/icon{type}.jpg\" height=\"54\" width=\"58\"></td>\n" +
                "    <td width='*' colspan='2'>{description}</td>\n" +
                "  </tr><tr><td><b>Author:</b> {author}</td><td></td></tr>" +
                "<tr><td><b>URL:</b> <a href='{url}' target='_blank'>{url}</a></td><td> </td></tr>" +
                "<tr><td></td><td><b>License:<b> <a href='{licenseUrl}' target='_blank'>{licenseName}</a></td><td></td></tr></table>";

        //XTemplate tpl = XTemplate.create("<p><b>Description:</b> {description}</p><br/><b>Author: </b> {author}<br/><b>Vendor: </b> {vendor}<br/><b>URL: </b> {url}<br/><b>Downloads: </b> {downloads}<br/><b>Rating: </b> {rating}<br/>");
        XTemplate tpl = XTemplate.create(template);
        RowExpander expander = new RowExpander(tpl);
        configs.add(expander);

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Name");
        column.setWidth(100);
        configs.add(column);


        column = new ColumnConfig();
        column.setId("downloads");
        column.setHeader("Downloads");
        column.setWidth(100);
        configs.add(column);

        column = new ColumnConfig();
        column.setId("rating");
        column.setHeader("Rating");
        column.setWidth(100);
        configs.add(column);

        column = new ColumnConfig("type", "Type", 100);
        configs.add(column);

        column = new ColumnConfig("version", "Version", 100);
        configs.add(column);

        GridCellRenderer<PluginData> buttonRenderer = new GridCellRenderer<PluginData>()
        {

            private boolean init;

            public Object render(final PluginData model, String property, ColumnData config, final int rowIndex,
                                 final int colIndex, ListStore<PluginData> store, Grid<PluginData> grid)
            {
                if (!init)
                {
                    init = true;
                    grid.addListener(Events.ColumnResize, new Listener<GridEvent<PluginData>>()
                    {

                        public void handleEvent(GridEvent<PluginData> be)
                        {
                            for (int i = 0; i < be.getGrid().getStore().getCount(); i++)
                            {
                                ((Button) be.getGrid().getView().getWidget(i, be.getColIndex())).setWidth(be.getWidth() - 10);
                            }
                        }
                    });
                }

                Button b = new Button((String) model.get(property), new SelectionListener<ButtonEvent>()
                {
                    @Override
                    public void componentSelected(ButtonEvent ce)
                    {
                        Info.display("Download", "TODO Check user is logged in and download");
                    }
                });
                b.setWidth(grid.getColumnModel().getColumnWidth(colIndex) - 10);
                b.setToolTip("Click to download");
                b.setText("Download >>");
                return b;
            }
        };

        column = new ColumnConfig("download", "Get it!", 100);
        column.setRenderer(buttonRenderer);
        column.setAlignment(HorizontalAlignment.CENTER);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        ContentPanel cp = new ContentPanel();
        cp.setHeading("List of iBeans and modules Available in the iBeans Store.");
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
        grid.setAutoWidth(true);
        //TODO we need auto sizing
        grid.setHeight(300);

        grid.getView().setAutoFill(true);
        cp.add(grid);
        panel.add(cp);
    }

}