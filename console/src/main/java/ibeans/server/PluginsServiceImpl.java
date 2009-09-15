package ibeans.server;

import org.mule.ibeans.internal.config.IBeansInfo;
import org.mule.util.FileUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletException;

import ibeans.client.ClientIBeansException;
import ibeans.client.PluginsService;
import ibeans.client.model.Plugin;

/**
 * TODO
 */
public class PluginsServiceImpl extends RemoteServiceServlet implements PluginsService
{
    protected enum WebAppType
    {
        EXPLODED, WAR, BOTH
    }

    public static final String DEPLOYED_NAME = "deployed";
    public static final String NOT_DEPLOYED_NAME = "notdeployed";
    public static final String MODULES_NAME = "modules";
    public static final String IBEANS_NAME = "ibeans";
    public static final String IBEANS_HOME = File.separator + "mule-ibeans";
    public static final String IBEANS_BASE_NAME = IBEANS_HOME + File.separator + "lib" + File.separator;
    private String catalinaHome;

    private File modules;
    private File ibeans;
    private File webapps;

    @Override
    public void init() throws ServletException
    {
        super.init();
        catalinaHome = getServletConfig().getInitParameter("catalina.home");

        if (catalinaHome == null)
        {
            catalinaHome = System.getProperty("catalina.home");
        }

        if (catalinaHome == null)
        {
            throw new ServletException("The catalina.home property is not set that defines the home directory for Tomcat.  Set it either as an init parameter on PluginsServiceImpl servlet or as a system property");
        }

        String modulesPath = catalinaHome + IBEANS_BASE_NAME + MODULES_NAME;
        String ibeansPath = catalinaHome + IBEANS_BASE_NAME + IBEANS_NAME;
        modules = new File(modulesPath);
        ibeans = new File(ibeansPath);
        webapps = new File(catalinaHome + IBEANS_HOME + File.separator + "webapps");

        if (!modules.exists())
        {
            throw new ServletException("Could not find Mule iBeans modules: " + modulesPath + ". Make sure you have the catalina.home set or pass in the home path when running this util");
        }

        if (!ibeans.exists())
        {
            throw new ServletException("Could not find Mule iBeans directory: " + ibeansPath + ". Make sure you have the catalina.home set or pass in the home path when running this util");
        }

        if (!webapps.exists())
        {
            throw new ServletException("Could not find Mule iBeans directory: " + webapps.getAbsolutePath() + ". Make sure you have the catalina.home set or pass in the home path when running this util");
        }
    }


    public List<Plugin> getInstalledPlugins() throws ClientIBeansException
    {
        List<Plugin> plugins = new ArrayList<Plugin>();
        readIBeansDirectory(new File(ibeans, DEPLOYED_NAME), plugins);
        readIBeansDirectory(new File(ibeans, NOT_DEPLOYED_NAME), plugins);
        readModulesDirectory(new File(modules, DEPLOYED_NAME), plugins);
        readModulesDirectory(new File(modules, NOT_DEPLOYED_NAME), plugins);
        readWebAppPlugins(plugins);
        return plugins;
    }

    public List<Plugin> getRunningWebapps() throws ClientIBeansException
    {
        List<Plugin> plugins = new ArrayList<Plugin>();
        readWebappsDirectory(new File(catalinaHome, "webapps"), plugins, WebAppType.EXPLODED);
        return plugins;
    }

    protected void readWebAppPlugins(List<Plugin> plugins) throws ClientIBeansException
    {
        List<Plugin> deployed = new ArrayList<Plugin>();
        List<Plugin> all = new ArrayList<Plugin>();
        readWebappsDirectory(new File(catalinaHome, "webapps"), deployed, WebAppType.EXPLODED);
        readWebappsDirectory(webapps, all, WebAppType.WAR);
        for (Plugin plugin : all)
        {
            plugin.setEnabled(deployed.contains(plugin));
        }
        plugins.addAll(all);
    }

    protected List<Plugin> readWebappsDirectory(File dir, List<Plugin> plugins, WebAppType webAppType) throws ClientIBeansException
    {
        File[] warsDirs = dir.listFiles();
        for (int i = 0; i < warsDirs.length; i++)
        {
            if (!warsDirs[i].isDirectory() && webAppType == WebAppType.EXPLODED)
            {
                continue;
            }
            else if (!warsDirs[i].getName().endsWith(".war") && webAppType == WebAppType.WAR)
            {
                continue;
            }

            try
            {
                Plugin p = createWar(warsDirs[i]);
                if (p != null)
                {
                    plugins.add(p);
                }
            }
            catch (IOException e)
            {
                log("Failed to read ibean: " + warsDirs[i].getAbsolutePath(), e);
            }
        }
        return plugins;
    }

    protected void readIBeansDirectory(File dir, List<Plugin> plugins)
    {
        File[] ibeanJars = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".jar");
            }
        });
        for (int i = 0; i < ibeanJars.length; i++)
        {
            File file = ibeanJars[i];
            try
            {
                plugins.add(createIBean(file));
            }
            catch (Throwable t)
            {
                log("Failed to read ibean: " + file.getAbsolutePath(), t);
            }
        }
    }

    protected void readModulesDirectory(File dir, List<Plugin> plugins) throws ClientIBeansException
    {
        File[] fileModules = dir.listFiles();
        for (int i = 0; i < fileModules.length; i++)
        {
            File fileModule = fileModules[i];
            if (fileModule.isDirectory())
            {
                try
                {
                    plugins.add(createModule(fileModule));
                }
                catch (ClientIBeansException e)
                {
                    throw e;
                }
                catch (Exception e)
                {
                    throw new ClientIBeansException("Failed to read modules: " + e.getMessage(), e);
                }
            }
        }
    }

    public void updatePlugins(List<Plugin> plugins) throws ClientIBeansException
    {
        File deployedLocation;
        File notdeployedLocation;
        for (Plugin plugin : plugins)
        {
            if (plugin.getType().equals(Plugin.TYPE_WEBAPP))
            {
                //special handling for web apps since we always keep a copy of the Web app in iBeans, but add or remove
                //from the Tomcat WebApp directory.  This is because users are used to deleting stuff from the Tomcat
                //Webapp directory
                deployedLocation = new File(catalinaHome, "webapps");
                notdeployedLocation = webapps;
                File warFile = new File(deployedLocation, plugin.getFilename());
                if (!plugin.isEnabled())
                {
                    FileUtils.deleteTree(new File(deployedLocation, plugin.getId()));
                    FileUtils.deleteQuietly(warFile);
                }
                else if (!warFile.exists())
                {
                    try
                    {
                        FileUtils.copyFile(new File(webapps, plugin.getFilename()), warFile);
                    }
                    catch (IOException e)
                    {
                        throw new ClientIBeansException(e);
                    }
                }
            }
            else
            {

                if (plugin.getType().equals(Plugin.TYPE_IBEAN))
                {
                    deployedLocation = new File(new File(ibeans, DEPLOYED_NAME), plugin.getFilename());
                    notdeployedLocation = new File(new File(ibeans, NOT_DEPLOYED_NAME), plugin.getFilename());
                }
                else // (plugin.getType().equals(Plugin.TYPE_MODULE))
                {
                    deployedLocation = new File(new File(modules, DEPLOYED_NAME), plugin.getId());
                    notdeployedLocation = new File(new File(modules, NOT_DEPLOYED_NAME), plugin.getId());
                }

                if (!plugin.isEnabled() && deployedLocation.exists())
                {
                    movePlugin(plugin, deployedLocation, notdeployedLocation);
                }
                else if (plugin.isEnabled() && notdeployedLocation.exists())
                {
                    movePlugin(plugin, notdeployedLocation, deployedLocation);
                }
            }
        }
    }

    protected void movePlugin(Plugin plugin, File from, File to) throws ClientIBeansException
    {
        if (from.renameTo(to))
        {
            log("Moved module '" + plugin.getName() + "' from '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "'");
        }
        else
        {
            throw new ClientIBeansException("Failed module '" + plugin.getName() + "' from '" + from.getPath() + "' to '" + to.getPath() + "'. Contact you're administrator to check file permissions.");
        }
    }

    protected Plugin createModule(File dir) throws Exception
    {
        File[] mod = dir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".pom");
            }
        });
        if (mod.length == 1)
        {
            MavenPomReader reader = new MavenPomReader(mod[0]);
            Plugin plugin = new Plugin();
            plugin.setId(dir.getName());
            plugin.setName(reader.getName());
            plugin.setDescription(reader.getDescription());
            plugin.setVersion(reader.getVersion());
            plugin.setUrl(reader.getUrl());
            plugin.setAuthor(reader.getOrganization());
            plugin.setInstalled(true);
            plugin.setType(Plugin.TYPE_MODULE);
            plugin.setEnabled(!dir.getAbsolutePath().contains(NOT_DEPLOYED_NAME));
            plugin.setLicenseName(reader.getLicenseName());
            plugin.setLicenseUrl(reader.getLicenseUrl());
            plugin.setBundled(reader.isBundled());
            plugin.setRequired(reader.isRequired());
            if ((plugin.getVersion() == null || plugin.getVersion().length() == 0) && plugin.isBundled())
            {
                plugin.setVersion(IBeansInfo.getProductVersion());
            }
            return plugin;
        }
        else
        {
            Plugin plugin = new Plugin();
            plugin.setId(dir.getName());
            plugin.setName(plugin.getId().substring(0, 1).toUpperCase() + plugin.getId().substring(1));
            plugin.setDescription("No module descriptor available");
            plugin.setInstalled(true);
            plugin.setType(Plugin.TYPE_MODULE);
            plugin.setEnabled(!dir.getAbsolutePath().contains(NOT_DEPLOYED_NAME));
            plugin.setWarning("There is no module descriptor for '" + plugin.getName() + "'");
            return plugin;
        }


    }

    protected Plugin createIBean(File jar) throws IOException
    {
        Plugin plugin = new Plugin();
        JarFile file = new JarFile(jar);

        Manifest manifest = file.getManifest();
        plugin.setName(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE));
        final String name = plugin.getName();
        if (name != null && name.length() > 0)
        {
            plugin.setId(name.substring(0, name.indexOf(" ")).toLowerCase());
        }
        else
        {
            plugin.setId(jar.getName());
        }
        plugin.setDescription(manifest.getMainAttributes().getValue("Product-Description"));
        plugin.setVersion(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION));
        plugin.setUrl(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_URL));
        plugin.setInstalled(true);
        plugin.setType(Plugin.TYPE_IBEAN);
        plugin.setEnabled(!jar.getAbsolutePath().contains(NOT_DEPLOYED_NAME));
        plugin.setFilename(jar.getName());

        String artifact = manifest.getMainAttributes().getValue(Attributes.Name.SPECIFICATION_TITLE);
        String groupID = manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VENDOR_ID);
        JarEntry pom = file.getJarEntry("META-INF/maven/" + groupID + "/" + artifact + "/pom.xml");

        if (pom != null)
        {
            InputStream is = file.getInputStream(pom);
            try
            {
                MavenPomReader reader = new MavenPomReader(is);
                plugin.setAuthor(reader.getOrganization());
                plugin.setLicenseName(reader.getLicenseName());
                plugin.setLicenseUrl(reader.getLicenseUrl());
            }
            catch (Exception e)
            {
                throw new IOException(e.getMessage());
            }

        }
        return plugin;
    }

    protected Plugin createWar(File war) throws IOException
    {
        Manifest manifest;
        FileInputStream fis = null;
        try
        {
            if (war.isDirectory())
            {
                File file = new File(war, "/META-INF/MANIFEST.MF");
                if (!file.exists())
                {
                    return null;
                }

                fis = new FileInputStream(file);
                manifest = new Manifest(fis);
            }
            else
            {
                JarFile warFile = new JarFile(war);
                manifest = warFile.getManifest();
                if (manifest == null)
                {
                    return null;
                }
            }
            Plugin plugin = new Plugin();
            plugin.setName(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_TITLE));
            if (plugin.getName() == null)
            {
                return null;
            }
            plugin.setId(war.getName().replace(".war", ""));
            plugin.setDescription(manifest.getMainAttributes().getValue("Product-Description"));
            plugin.setFurtherInfo(manifest.getMainAttributes().getValue("Further-Info"));
            plugin.setVersion(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION));
            plugin.setUrl(manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_URL));
            plugin.setInstalled(true);
            plugin.setType(Plugin.TYPE_WEBAPP);
            plugin.setEnabled(true);
            plugin.setFilename((war.getName().endsWith(".war") ? war.getName() : war.getName() + ".war"));
            return plugin;
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }
    }
}