package ibeans.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

import ibeans.client.model.Plugin;
import ibeans.client.model.PluginData;

public class ExamplesPanel extends LayoutContainer
{

    private ContentPanel panel;
    private IBeansConsole2 iBeansConsole;
    private Grid<PluginData> grid;

    public ExamplesPanel(IBeansConsole2 iBeansConsole)
    {
        this.iBeansConsole = iBeansConsole;
        panel = new ContentPanel();
        //panel.setSpacing(10);
        panel.setAutoHeight(true);
        panel.setBodyBorder(false);
        panel.setHeaderVisible(true);
        panel.setLayout(new FitLayout());
        panel.setAutoWidth(true);


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
        service.getRunningWebapps(new AsyncCallback<List<Plugin>>()
        {
            public void onFailure(Throwable caught)
            {
                iBeansConsole.errorStatus(caught);
            }

            public void onSuccess(List<Plugin> result)
            {
                for (Plugin plugin : result)
                {
                    PluginData pluginData = new PluginData(plugin);
                    pluginData.set("name", "<a href='/" + plugin.getId() + "' target='_blank'>" + plugin.getName() + "</a>");
                    store.add(pluginData);
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

        String template = "<table cellpadding='10' cellspacing='10'><tr>\n" +
                "    <td valign=\"top\" width=\"58\"><img src=\"images/Webapp.jpg\" alt=\"Example\" height=\"54\" width=\"58\"></td>\n" +
                "    <td width='*'>{furtherInfo}</td>\n" +
                "    </tr></table>";

        XTemplate tpl = XTemplate.create(template);
        RowExpander expander = new RowExpander(tpl);
        configs.add(expander);

        ColumnConfig column = new ColumnConfig("name", "Name", 100);
        configs.add(column);

        column = new ColumnConfig("description", "Description", 200);
        configs.add(column);

        column = new ColumnConfig("version", "Version", 100);
        column.setAlignment(HorizontalAlignment.RIGHT);
        //column.setRenderer(gridNumber);
        configs.add(column);

        ColumnModel cm = new ColumnModel(configs);

        panel.setHeading("List example webapps installed in this instance.");
        panel.setFrame(true);
        //cp.setIcon(Examples.ICONS.table());

        grid = new EditorGrid<PluginData>(store, cm);
        grid.setSelectionModel(sm);
        grid.setBorders(true);
        grid.addPlugin(expander);
        grid.setSelectionModel(sm);

        grid.getView().setAutoFill(true);
        grid.setAutoHeight(true);
        grid.setAutoWidth(true);
        panel.add(grid);
    }

}