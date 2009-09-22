/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.aws.ec2;

import static org.mule.ibeans.IBeansSupport.selectValue;
import static org.mule.ibeans.IBeansSupport.select;
import org.mule.ibeans.api.application.Transformer;
import org.mule.ibeans.IBeansContext;

import java.util.List;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.InputStream;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.inject.Inject;

import org.w3c.dom.Node;
import org.w3c.dom.Document;

/**
 * Transformer to convert from Amazon EC2 Xml to  Instance and Image objects
 */
public class EC2Transformers
{
    @Inject
    private IBeansContext iBeansContext;

    @Transformer
      public Image xmlStream2Image(InputStream in) throws org.mule.api.transformer.TransformerException
      {
          Node node = iBeansContext.transform(in, Document.class);
          return dom2Image(node);
      }

      @Transformer
      public Image dom2Image(Node node)
      {
          Image img = new Image();
          img.setImagePublic(Boolean.parseBoolean(selectValue("//*[name()='isPublic']", node)));
          img.setLocation(selectValue(".//*[name()='imageLocation']", node));
          img.setArchitecture(selectValue(".//*[name()='architecture']", node));
          img.setImageType(selectValue(".//*[name()='imageType']", node));
          return img;
      }

    
    @Transformer
    public Instance xmlStream2Instance(InputStream in) throws org.mule.api.transformer.TransformerException
    {
        Node node = iBeansContext.transform(in, Document.class);
        return dom2Instance(node);
    }

    @Transformer
    public Instance dom2Instance(Node node)
    {
        Instance inst = new Instance();
        inst.setInstanceId(selectValue(".//*[name()='instanceId']", node));
        inst.setImageId(selectValue(".//*[name()='imageId']", node));
        //It seems instance state code can be empty
        String stateCode = selectValue(".//*[name()='instanceState']/*[name()='code']", node);
        if(stateCode!=null && stateCode.length() >0)
        {
            inst.setInstanceStateCode(Integer.parseInt(stateCode));
        }
        inst.setInstanceStateName(selectValue(".//*[name()='instanceState']/*[name()='name']", node));
        inst.setPrivateDnsName(selectValue(".//*[name()='privateDnsName']", node));
        inst.setPublicDnsName(selectValue(".//*[name()='dnsName']", node));
        inst.setKeyName(selectValue(".//*[name()='keyName']", node));
        String launchIndex = selectValue(".//*[name()='amiLaunchIndex']", node);
        if(launchIndex!=null && launchIndex.length() > 0)
        {
            inst.setAmiLaunchIndex(Integer.parseInt(launchIndex));
        }
        inst.setImageType(selectValue(".//*[name()='instanceType']", node));
        inst.setLaunchTime(selectValue(".//*[name()='launchTime']", node));
        inst.setAvailabilityZone(selectValue(".//*[name()='placement']/*[name()='availabilityZone']", node));
        inst.setKernelId(selectValue(".//*[name()='kernelId']", node));
        inst.setRamDiskId(selectValue(".//*[name()='ramdiskId']", node));

        return inst;
    }

    @Transformer
    public List<Instance> dom2Instances(InputStream in) throws org.mule.api.transformer.TransformerException
    {
        Node doc = iBeansContext.transform(in, Document.class);
        List<Instance> instances = new ArrayList<Instance>();
        List<Node> nodes = select("//*[name()='instancesSet']/*[name()='item']", doc);
        for (Node node : nodes)
        {
            instances.add(dom2Instance(node));
        }
        return instances;
    }
}
