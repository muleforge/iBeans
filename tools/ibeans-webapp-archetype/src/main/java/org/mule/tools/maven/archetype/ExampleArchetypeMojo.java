/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tools.maven.archetype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.archetype.Archetype;
import org.apache.maven.archetype.ArchetypeDescriptorException;
import org.apache.maven.archetype.ArchetypeNotFoundException;
import org.apache.maven.archetype.ArchetypeTemplateProcessingException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Builds archetype containers.
 *
 * @goal create
 * @description The archetype creation goal looks for an archetype with a given newGroupId, newArtifactId, and
 * newVersion and retrieves it from the remote repository. Once the archetype is retrieve it is process against
 * a set of user parameters to create a working Maven project. This is a modified newVersion for bobber to support additional functionality.
 * @requiresProject false
 */
public class ExampleArchetypeMojo extends AbstractMojo
{
    /**
     * @parameter expression="${component.org.apache.maven.archetype.Archetype}"
     * @required
     */
    private Archetype archetype;

    /**
     * @parameter expression="${localRepository}"
     * @required
     */
    private ArtifactRepository localRepository;

    /**
     * @parameter expression="${archetypeGroupId}" default-value="org.mule.ibeans"
     * @required
     */
    private String archetypeGroupId;

    /**
     * @parameter expression="${archetypeArtifactId}" default-value="ibeans-webapp-archetype"
     * @required
     */
    private String archetypeArtifactId;

    /**
     * @parameter expression="${archetypeVersion}" default-value="${ibeansVersion}"
     * @required
     */
    private String archetypeVersion;

    /**
     * @parameter expression="${ibeansVersion}"
     * @required
     */
    private String ibeansVersion;

    /**
     * @parameter expression="${groupId}" alias="newGroupId" default-value="org.mule.ibeans"
     * @require
     */
    private String groupId;

    /**
     * @parameter expression="${artifactId}" alias="newArtifactId" default-value="my-ibeans-webapp"
     * @require
     */
    private String artifactId;

    /**
     * @parameter expression="${project.version}" alias="newVersion" default-value="1.0-SNAPSHOT"
     * @require
     */
    private String version;

    /**
     * @parameter expression="${packageName}" alias="package"
     */
    private String packageName;

    /**
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @required
     */
    private List remoteRepositories;

    public void execute()
            throws MojoExecutionException
    {

        // ----------------------------------------------------------------------
        // archetypeGroupId
        // archetypeArtifactId
        // archetypeVersion
        //
        // localRepository
        // remoteRepository
        // parameters
        // ----------------------------------------------------------------------

        String basedir = System.getProperty("user.dir");

        if (packageName == null)
        {
            getLog().info("Defaulting package to group ID: " + groupId);

            packageName = groupId;
        }

        // TODO: context mojo more appropriate?
        Map map = new HashMap();

        map.put("basedir", basedir);

        map.put("package", packageName);

        map.put("packageName", packageName);

        map.put("groupId", groupId);

        map.put("artifactId", artifactId);

        map.put("version", version);
        map.put("ibeansVersion", ibeansVersion);
        map.put("user", System.getProperty("user.name"));


        try
        {
            archetype.createArchetype(archetypeGroupId, archetypeArtifactId, archetypeVersion, localRepository, remoteRepositories, map);
        }
        catch (ArchetypeNotFoundException e)
        {
            throw new MojoExecutionException("Error creating from archetype", e);
        }
        catch (ArchetypeDescriptorException e)
        {
            throw new MojoExecutionException("Error creating from archetype", e);
        }
        catch (ArchetypeTemplateProcessingException e)
        {
            throw new MojoExecutionException("Error creating from archetype", e);
        }
    }


}
