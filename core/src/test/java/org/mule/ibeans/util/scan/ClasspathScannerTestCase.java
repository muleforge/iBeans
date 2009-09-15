/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.util.scan;

import org.mule.config.annotations.endpoints.Channel;
import org.mule.ibeans.api.application.Receive;
import org.mule.ibeans.api.application.Send;
import org.mule.ibeans.util.scan.annotations.SampleBeanWithAnnotations;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.tck.testmodels.fruit.BloodOrange;
import org.mule.tck.testmodels.fruit.Fruit;
import org.mule.tck.testmodels.fruit.Orange;
import org.mule.tck.testmodels.fruit.OrangeInterface;
import org.mule.tck.testmodels.fruit.RedApple;
import org.mule.util.scan.ClasspathScanner;

import java.util.Set;

import junit.framework.TestCase;

public class ClasspathScannerTestCase extends TestCase
{
    public void testInterfaceScanCompiledClasspath() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner(new String[]{""});
        Set<Class> set = s.scanFor(Fruit.class);

        assertFalse(set.contains(Orange.class));
        assertTrue(set.contains(Grape.class));
        assertTrue(set.contains(SeedlessGrape.class));
    }

    //This is slow
    public void testInterfaceScanClasspathAndJars() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner(new String[]{"org/mule"});
        Set<Class> set = s.scanFor(Fruit.class);

        assertTrue(set.contains(Apple.class));
        assertTrue(set.contains(RedApple.class));
        assertTrue(set.contains(BloodOrange.class));
        assertTrue(set.contains(OrangeInterface.class));
        assertTrue(set.contains(Orange.class));
        assertTrue(set.contains(Grape.class));
        assertTrue(set.contains(SeedlessGrape.class));
    }

    //This will be a lot more efficient
    public void testInterfaceScanClasspathAndJarsMultipleBasePaths() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner(new String[]{"org/mule/tck", "org/mule/ibeans/util"});
        Set<Class> set = s.scanFor(Fruit.class);

        assertTrue(set.contains(Apple.class));
        assertTrue(set.contains(RedApple.class));
        assertTrue(set.contains(BloodOrange.class));
        assertTrue(set.contains(OrangeInterface.class));
        assertTrue(set.contains(Orange.class));
        assertTrue(set.contains(Grape.class));
        assertTrue(set.contains(SeedlessGrape.class));
        assertTrue(set.contains(MadridOrange.class));
    }

    public void testImplementationScanCompiledClasspath() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner(new String[]{""});
        Set<Class> set = s.scanFor(Grape.class);

        assertEquals(1, set.size());
        assertFalse(set.contains(Grape.class));
        assertTrue(set.contains(SeedlessGrape.class));
    }

    public void testImplementationScanClasspathAndJarsMultipleBasePaths() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner(new String[]{"org/mule/tck", "org/mule/ibeans/util"});
        Set<Class> set = s.scanFor(Orange.class);

        assertFalse(set.contains(Apple.class));
        assertTrue(set.contains(BloodOrange.class));
        assertFalse(set.contains(OrangeInterface.class));
        assertFalse(set.contains(Orange.class));
        assertFalse(set.contains(Grape.class));
        assertTrue(set.contains(MadridOrange.class));
    }

    public void testAnnotationMetaScanClasspathAndJarsMultipleBasePaths() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner(new String[]{"org/mule/ibeans/util"});
        Set<Class> set = s.scanFor(Channel.class);

        assertEquals(2, set.size());
        assertTrue(set.contains(SampleBeanWithAnnotations.class));
        assertTrue(set.contains(SubscribeBean.class));
    }

    public void testAnnotationScanClasspathAndJarsMultipleBasePaths() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner(new String[]{"org/mule/ibeans"});
        Set<Class> set = s.scanFor(Receive.class);

        assertTrue(set.contains(SampleBeanWithAnnotations.class));
        assertTrue(set.contains(SubscribeBean.class));

        set = s.scanFor(Send.class);
        //assertEquals(1, set.size());
        assertTrue(set.contains(SampleBeanWithAnnotations.class));

    }
}