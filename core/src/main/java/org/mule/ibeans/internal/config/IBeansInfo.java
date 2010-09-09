package org.mule.ibeans.internal.config;

import org.mule.api.config.MuleConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IBeansInfo
{
    /**
     * logger used by this class
     */
    protected static transient final Log logger = LogFactory.getLog(IBeansInfo.class);

    private static Manifest manifest;

    public static String getProductVersion()
    {
        return getManifestProperty("Implementation-Version");
    }

    public static String getVendorName()
    {
        return getManifestProperty("Implementation-Vendor");
    }

    public static String getVendorUrl()
    {
        return getManifestProperty("Implementation-Vendor-Url");
    }

    public static String getProductUrl()
    {
        return getManifestProperty("Implementation-Url");
    }

    public static String getProductName()
    {
        return getManifestProperty("Implementation-Title");
    }

    public static String getProductMoreInfo()
    {
        return getManifestProperty("More-Info");
    }

    public static String getProductSupport()
    {
        return getManifestProperty("Support");
    }

    public static String getProductLicenseInfo()
    {
        return getManifestProperty("License-Title");
    }


    public static String getProductDescription()
    {
        return getManifestProperty("Product-Description");
    }

    public static String getBuildNumber()
    {
        return getManifestProperty("Build-Revision");
    }

    public static String getBuildDate()
    {
        return getManifestProperty("Build-Date");
    }

    public static String getDevListEmail()
    {
        return getManifestProperty("Dev-List-Email");
    }

    public static Manifest getManifest()
    {
        if (manifest == null)
        {
            manifest = new Manifest();

            InputStream is = null;
            try
            {
                // We want to load the MANIFEST.MF from the mule-core jar. Sine we
                // don't know the version we're using we have to search for the jar on the classpath
                URL url = AccessController.doPrivileged(new PrivilegedAction<URL>()
                {
                    public URL run()
                    {
                        try
                        {
                            Enumeration e = MuleConfiguration.class.getClassLoader().getResources(
                                    ("META-INF/MANIFEST.MF"));
                            while (e.hasMoreElements())
                            {
                                URL url = (URL) e.nextElement();
                                if ((url.toExternalForm().indexOf("ibeans-core") > -1 && url.toExternalForm()
                                        .indexOf("tests.jar") < 0))
                                {
                                    return url;
                                }
                            }
                        }
                        catch (IOException e1)
                        {
                            logger.warn("Failure reading manifest: " + e1.getMessage(), e1);
                        }
                        return null;
                    }
                });

                if (url != null)
                {
                    is = url.openStream();
                }

                if (is != null)
                {
                    manifest.read(is);
                }
            }
            catch (IOException e)
            {
                logger.warn("Failed to read manifest Info, Manifest information will not display correctly: "
                        + e.getMessage());
            }
        }
        return manifest;
    }

    protected static String getManifestProperty(String name)
    {
        return getManifest().getMainAttributes().getValue(new Attributes.Name(name));
    }
}
