/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.impl.concept;

import org.mule.api.MuleException;
import org.mule.config.annotations.concept.VmInbound;
import org.mule.config.annotations.concept.VmOutbound;
import org.mule.impl.endpoint.AbstractEndpointAnnotationParser;
import org.mule.impl.endpoint.AnnotatedEndpointData;
import org.mule.transport.vm.VMConnector;

import java.lang.annotation.Annotation;

/**
 * TODO
 */
public class VMAnnotationParser extends AbstractEndpointAnnotationParser
{
    public static final String VM_CHANNEL = "_ibeansVmChannel";

    protected AnnotatedEndpointData createEndpointData(Annotation annotation) throws MuleException
    {
        if (annotation instanceof VmInbound)
        {
            VmInbound vm = (VmInbound) annotation;
            String uri = "vm://" + vm.path();
            AnnotatedEndpointData epData = new AnnotatedEndpointData(vm.mep());
            epData.setProperties(convertProperties(vm.properties()));
            epData.setAddress(uri);
            epData.setFilter(vm.filter());
            epData.setConnector(getConnector());
            epData.setName(vm.id());
            return epData;

        }
        else
        {
            VmOutbound vm = (VmOutbound) annotation;
            String uri = "vm://" + vm.path();
            AnnotatedEndpointData epData = new AnnotatedEndpointData(vm.mep());
            epData.setProperties(convertProperties(vm.properties()));
            epData.setAddress(uri);
            epData.setFilter(vm.filter());
            epData.setConnector(getConnector());
            epData.setName(vm.id());
            return epData;
        }
    }

    protected String getIdentifier()
    {
        return VMConnector.VM;
    }

    protected VMConnector getConnector() throws MuleException
    {

        VMConnector connector = (VMConnector) muleContext.getRegistry().lookupConnector(VM_CHANNEL);
        if (connector == null)
        {
            connector = new VMConnector();
            connector.setName(VM_CHANNEL);
            muleContext.getRegistry().registerConnector(connector);
        }
        return connector;
    }

}
