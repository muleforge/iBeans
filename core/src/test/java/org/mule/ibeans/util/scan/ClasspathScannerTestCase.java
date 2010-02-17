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

import org.junit.Assert;
import org.junit.Test;

public class ClasspathScannerTestCase
{
    @Test
    public void interfaceScanCompiledClasspath() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner("");
        Set<Class> set = s.scanFor(Fruit.class);

        Assert.assertFalse(set.contains(Orange.class));
        Assert.assertTrue(set.contains(Grape.class));
        Assert.assertTrue(set.contains(SeedlessGrape.class));
    }

    //This is slow
    @Test
    public void interfaceScanClasspathAndJars() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner("org/mule");
        Set<Class> set = s.scanFor(Fruit.class);

        Assert.assertTrue(set.contains(Apple.class));
        Assert.assertTrue(set.contains(RedApple.class));
        Assert.assertTrue(set.contains(BloodOrange.class));
        Assert.assertTrue(set.contains(OrangeInterface.class));
        Assert.assertTrue(set.contains(Orange.class));
        Assert.assertTrue(set.contains(Grape.class));
        Assert.assertTrue(set.contains(SeedlessGrape.class));
    }

    //This will be a lot more efficient
    @Test
    public void interfaceScanClasspathAndJarsMultipleBasePaths() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner("org/mule/tck", "org/mule/ibeans/util");
        Set<Class> set = s.scanFor(Fruit.class);

        Assert.assertTrue(set.contains(Apple.class));
        Assert.assertTrue(set.contains(RedApple.class));
        Assert.assertTrue(set.contains(BloodOrange.class));
        Assert.assertTrue(set.contains(OrangeInterface.class));
        Assert.assertTrue(set.contains(Orange.class));
        Assert.assertTrue(set.contains(Grape.class));
        Assert.assertTrue(set.contains(SeedlessGrape.class));
        Assert.assertTrue(set.contains(MadridOrange.class));
    }

    @Test
    public void implementationScanCompiledClasspath() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner("");
        Set<Class> set = s.scanFor(Grape.class);

        Assert.assertEquals(1, set.size());
        Assert.assertFalse(set.contains(Grape.class));
        Assert.assertTrue(set.contains(SeedlessGrape.class));
    }

    @Test
    public void implementationScanClasspathAndJarsMultipleBasePaths() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner("org/mule/tck", "org/mule/ibeans/util");
        Set<Class> set = s.scanFor(Orange.class);

        Assert.assertFalse(set.contains(Apple.class));
        Assert.assertTrue(set.contains(BloodOrange.class));
        Assert.assertFalse(set.contains(OrangeInterface.class));
        Assert.assertFalse(set.contains(Orange.class));
        Assert.assertFalse(set.contains(Grape.class));
        Assert.assertTrue(set.contains(MadridOrange.class));
    }

    @Test
    public void annotationMetaScanClasspathAndJarsMultipleBasePaths() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner("org/mule/ibeans/util");
        Set<Class> set = s.scanFor(Channel.class);

        Assert.assertEquals(2, set.size());
        Assert.assertTrue(set.contains(SampleBeanWithAnnotations.class));
        Assert.assertTrue(set.contains(SubscribeBean.class));
    }

    @Test
    public void annotationScanClasspathAndJarsMultipleBasePaths() throws Exception
    {
        ClasspathScanner s = new ClasspathScanner("org/mule/ibeans");
        Set<Class> set = s.scanFor(Receive.class);

        Assert.assertTrue(set.contains(SampleBeanWithAnnotations.class));
        Assert.assertTrue(set.contains(SubscribeBean.class));

        set = s.scanFor(Send.class);
        //assertEquals(1, set.size());
        Assert.assertTrue(set.contains(SampleBeanWithAnnotations.class));

    }
}