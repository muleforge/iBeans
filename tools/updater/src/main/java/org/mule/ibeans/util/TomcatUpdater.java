/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.util;

import org.mule.util.FilenameUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A Utility class for updating the the catalina.properties to load the deployed ibeans modules
 */
public class TomcatUpdater
{
    public static final String SHARED_LOADER_PROPERTY = "shared.loader";
    public static final String OLD_SHARED_LOADER_PROPERTY = "#" + SHARED_LOADER_PROPERTY;

    public static void main(String[] args)
    {
        String basepath = null;

        boolean libsOnly = false;
        boolean webappsOnly = false;
        boolean uninstall = false;

        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            if (arg.equals("-b"))
            {
                basepath = args[++i];
            }
            else if (arg.equals("-l"))
            {
                libsOnly = true;
            }
            else if (arg.equals("-w"))
            {
                webappsOnly = true;
            }
            else if (arg.equals("-u"))
            {
                uninstall = true;
            }
            else if (arg.equals("-?"))
            {
                usage();
                System.exit(0);
            }
            else
            {
                System.err.println("Unknown argument:" + arg);
                usage();
                System.exit(1);
            }

        }


        if (System.getProperty("catalina.home") != null && basepath == null)
        {
            basepath = System.getProperty("catalina.home");
        }
        //This is our fallback, it assumes this class is run in ${CATALINA_HOME}/mule-ibeans/bin
        if (basepath == null)
        {
            basepath = "../..";
        }

        try
        {
            basepath = new File(basepath).getCanonicalPath();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        String filename = basepath + "/conf/catalina.properties";
        File catalinaProps = new File(filename);
        if (!catalinaProps.exists())
        {
            System.err.println("Could not find catalina.properties at: " + filename + ". Make sure you have the -Dcatalina.home=[CATALINA_HOME] JVM parameter set or pass in the Catalina home path when running the updater.");
            System.exit(1);
        }

        if (uninstall)
        {
            try
            {
                revertCatalinaProps(catalinaProps);
            }
            catch (IOException e)
            {
                System.err.println("Failed to revert conf/catalina.properties, please check this file");
            }

            uncopyWebApps(basepath);
            System.out.println("Uninstall completed. The 'mule-ibeans' directory can be removed.");
            return;
        }

        System.out.println("Base path is: " + basepath);
        if (!webappsOnly)
        {
            addModulesClasspath(basepath, catalinaProps);
        }

        if (!libsOnly)
        {
            copyWebApps(basepath);
        }
    }

    public static void usage()
    {
        System.err.println("Valid options for the iBeans Tomcat updater tool:");
        System.err.println("-b [basepath] : The location of the Tcat or Tomcat root directory. This can also be set by defining a VM parameter: -Dcatalina.home=[basepath]");
        System.err.println("-l : Only update the lib classpath entries (do not copy bundled webapps)");
        System.err.println("-w : Copy the bundled webapps (do not update classpath entries)");
        System.err.println("-u : Runs the uninstaller");
        System.err.println("-? : Display this information");
    }

    protected static void addModulesClasspath(String basepath, File catalinaProps)
    {
        try
        {

            String modulesPath = basepath + "/mule-ibeans/lib/modules";
            File modules = new File(modulesPath);
            if (!modules.exists())
            {
                System.err.println("Could not find Mule iBeans modules: " + modulesPath + ". Make sure you have the -Dcatalina.home=[CATALINA_HOME] JVM parameter set or pass in the Catalina home path when running the updater.");
                System.exit(1);
            }
            StringBuffer sharedLoader = new StringBuffer();
            sharedLoader.append(SHARED_LOADER_PROPERTY).append("=");
            sharedLoader.append("${catalina.home}/mule-ibeans/conf,${catalina.home}/mule-ibeans/lib/*.jar,${catalina.home}/mule-ibeans/lib/ibeans/deployed/*.jar,${catalina.home}/mule-ibeans/lib/modules/deployed/*.jar");
//            File deployed = new File(modulesPath, "deployed");
//            File[] fileModules = deployed.listFiles();
//            for (int i = 0; i < fileModules.length; i++)
//            {
//                File fileModule = fileModules[i];
//                if (fileModule.isDirectory())
//                {
//                    sharedLoader.append(",${catalina.home}/mule-ibeans/lib/modules/deployed/").append(fileModule.getName()).append("/*.jar");
//                }
//            }

            File newConfig = new File(catalinaProps.getAbsolutePath() + ".new");
            if (newConfig.exists())
            {
                newConfig.delete();
                if (!newConfig.createNewFile())
                {
                    System.err.println("Failed to create temporary catalina.properties");
                    System.exit(1);
                }
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(newConfig));

            BufferedReader reader = new BufferedReader(new FileReader(catalinaProps));
            String s;
            try
            {
                while ((s = reader.readLine()) != null)
                {
                    if (s.startsWith(SHARED_LOADER_PROPERTY))
                    {
                        //rem out the current value
                        writer.write("#" + s);
                        writer.newLine();
                        System.out.println("Setting shared loader to: " + sharedLoader + ". Old value is: " + s);
                        writer.write(sharedLoader.toString());
                        writer.newLine();

                    }
                    else
                    {
                        writer.write(s);
                        writer.newLine();
                    }
                }
                writer.flush();
                writer.close();

                safeCopyFile(newConfig, catalinaProps);
            }
            finally
            {
                writer.close();
                reader.close();
                newConfig.delete();
            }


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    protected static void copyWebApps(String basepath)
    {
        File wa = new File(basepath, "mule-ibeans/webapps");
        File[] apps = wa.listFiles();
        for (int i = 0; i < apps.length; i++)
        {
            File app = apps[i];
            File to = new File(basepath, "webapps/" + app.getName());
            try
            {
                if (to.createNewFile() || to.exists())
                {
                    System.out.println("Copying app: " + app.getAbsolutePath() + " to " + to.getAbsolutePath());
                    safeCopyFile(app, to);
                }
                else
                {
                    System.err.println("Unable to create file: " + to.getAbsolutePath());
                }
            }
            catch (IOException e)
            {
                System.err.println("Unable to create file: " + to.getAbsolutePath() + ", " + e.getMessage());
            }
        }
    }

    public static void safeCopyFile(File in, File out)
    {
        try
        {
            FileInputStream fis = new FileInputStream(in);
            FileOutputStream fos = new FileOutputStream(out);
            try
            {
                byte[] buf = new byte[1024];
                int i = 0;
                while ((i = fis.read(buf)) != -1)
                {
                    fos.write(buf, 0, i);
                }
            }
            catch (IOException e)
            {
                throw e;
            }
            finally
            {
                try
                {
                    fis.close();
                    fos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace(System.err);
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
        }
    }

    private static void revertCatalinaProps(File catalinaProps) throws IOException
    {
        File newConfig = new File(catalinaProps.getAbsolutePath() + ".new");
        if (newConfig.exists())
        {
            newConfig.delete();
            if (!newConfig.createNewFile())
            {
                System.err.println("Failed to create temporary catalina.properties");
                System.exit(1);
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(newConfig));

        BufferedReader reader = new BufferedReader(new FileReader(catalinaProps));
        String s;
        try
        {
            while ((s = reader.readLine()) != null)
            {
                if (s.startsWith(OLD_SHARED_LOADER_PROPERTY))
                {
                    //make the old value the current one
                    writer.write(s.substring(1));
                    writer.newLine();

                }
                else if (s.startsWith(SHARED_LOADER_PROPERTY))
                {
                    //skip
                }
                else
                {
                    writer.write(s);
                    writer.newLine();
                }
            }
            writer.flush();
            writer.close();

            safeCopyFile(newConfig, catalinaProps);
        }
        finally
        {
            writer.close();
            reader.close();
            newConfig.delete();
        }

    }

    protected static void uncopyWebApps(String basepath)
    {
        File wa = new File(basepath, "mule-ibeans/webapps");
        File catWa = new File(basepath, "webapps");
        File[] apps = wa.listFiles();
        for (int i = 0; i < apps.length; i++)
        {
            File app = apps[i];
            File remove = new File(catWa, app.getName());
            if (remove.exists())
            {
                //Remove war file
                if (!remove.delete())
                {
                    System.err.println("Failed to remove webapp: " + remove.getAbsolutePath());
                }
            }

            remove = new File(catWa, app.getName().substring(0, app.getName().length() - 4));
            if (remove.exists())
            {
                //Remvoe exploded directory if there is one
                if (!deleteTree(remove))
                {
                    System.err.println("Failed to remove webapp: " + remove.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Delete a file tree recursively. This method additionally tries to be
     * gentle with specified top-level dirs. E.g. this is the case when a
     * transaction manager asynchronously handles the recovery log, and the test
     * wipes out everything, leaving the transaction manager puzzled.
     *
     * @param dir                  dir to wipe out
     * @param topLevelDirsToIgnore which top-level directories to ignore,
     *                             if null or empty then ignored
     * @return false when the first unsuccessful attempt encountered
     */
    private static boolean deleteTree(File dir, final String[] topLevelDirsToIgnore)
    {
        if (dir == null || !dir.exists())
        {
            return true;
        }
        File[] files = dir.listFiles();
        if (files != null)
        {
            for (int i = 0; i < files.length; i++)
            {
                OUTER:
                if (files[i].isDirectory())
                {
                    if (topLevelDirsToIgnore != null)
                    {
                        for (int j = 0; j < topLevelDirsToIgnore.length; j++)
                        {
                            String ignored = topLevelDirsToIgnore[j];
                            if (ignored.equals(FilenameUtils.getBaseName(files[i].getName())))
                            {
                                break OUTER;
                            }
                        }
                    }
                    if (!deleteTree(files[i]))
                    {
                        return false;
                    }
                }
                else
                {
                    if (!files[i].delete())
                    {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * Delete a file tree recursively.
     *
     * @param dir dir to wipe out
     * @return false when the first unsuccessful attempt encountered
     */
    private static boolean deleteTree(File dir)
    {
        return deleteTree(dir, null);
    }
}
