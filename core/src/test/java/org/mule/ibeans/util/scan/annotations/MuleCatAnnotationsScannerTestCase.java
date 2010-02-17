/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.util.scan.annotations;

import org.mule.config.annotations.endpoints.Channel;
import org.mule.util.scan.annotations.AnnotationTypeFilter;
import org.mule.util.scan.annotations.AnnotationsScanner;
import org.mule.util.scan.annotations.MetaAnnotationTypeFilter;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;

public class MuleCatAnnotationsScannerTestCase
{
    @Test
    public void scanAnnotationsWithFilter() throws Exception
    {
        ClassReader r = new ClassReader(SampleClassWithAnnotations.class.getName());
        AnnotationsScanner scanner = new AnnotationsScanner(new AnnotationTypeFilter(MultiMarker.class));

        r.accept(scanner, 0);

        Assert.assertEquals(1, scanner.getAllAnnotations().size());
    }

    @Test
    public void scanMetaAnnotations() throws Exception
    {
        ClassReader r = new ClassReader(SampleBeanWithAnnotations.class.getName());
        AnnotationsScanner scanner = new AnnotationsScanner(new MetaAnnotationTypeFilter(Channel.class));

        r.accept(scanner, 0);

        Assert.assertEquals(2, scanner.getMethodAnnotations().size());
    }
}
