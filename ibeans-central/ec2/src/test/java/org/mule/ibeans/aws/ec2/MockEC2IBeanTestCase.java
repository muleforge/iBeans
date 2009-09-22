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

import org.mule.ibeans.test.AbstractIBeansTestCase;
import org.mule.ibeans.api.client.MockIntegrationBean;
import org.mule.ibeans.aws.ec2.Image;
import org.mule.ibeans.aws.ec2.Instance;
import org.mule.ibeans.aws.ec2.EC2Transformers;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * A mock test for EC2.   tested once on the real server and it works.
 */
public class MockEC2IBeanTestCase extends AbstractIBeansTestCase
{
    @MockIntegrationBean
    private EC2IBean ec2;

    private static final byte[] AWS_SECRET_KEY = "my.key".getBytes();
    private static final String ACCESS_KEY_ID = "my.key.id";
    private static final String KEYPAIR_NAME = "demo";
    private static final String API_VERSION = "2009-04-04";

    private final String testImageId = "aki-cfaf4ba6";

    // java + tomcat + alfresco demo image
    final String javaTomcatImageId = "ami-60947109";

    private final String javaTomcatInstanceId = "i-01e33769";

    @Override
    protected void doSetUp() throws Exception
    {
        registerBeans(new EC2Transformers());

        ec2.init(AWS_SECRET_KEY, ACCESS_KEY_ID, API_VERSION);

        when(ec2.describeImage(testImageId)).thenAnswer(withXmlData("describe-images-response.xml", ec2));

        when(ec2.runInstance(javaTomcatImageId, 1, 1, KEYPAIR_NAME)).thenAnswer(withXmlData("run-instance-response.xml", ec2));

        when(ec2.terminateInstance(javaTomcatInstanceId)).thenAnswer(withXmlData("terminate-instance-response.xml", ec2));

        when(ec2.describeInstances()).thenAnswer(withXmlData("describe-instances-response.xml", ec2));
    }

    public void testDescribeImage() throws Exception
    {
        Image img = ec2.describeImage(testImageId);
        assertNotNull(img);
        assertEquals("i386", img.getArchitecture());
    }


    public void testRunInstance() throws Exception
    {
        // java + tomcat + alfresco demo image
        Instance instance = ec2.runInstance(javaTomcatImageId,1,1, KEYPAIR_NAME);
        assertNotNull(instance);

        Instance termInstance = ec2.terminateInstance(instance.getInstanceId());
        assertNotNull(termInstance);

    }

    public void testListInstances() throws Exception
    {
        final List<Instance> myInstances = ec2.describeInstances();
        if (myInstances.isEmpty())
        {
            System.out.println("Currently there are no running instances owned by you");
        }
        for (Instance instance : myInstances)
        {
            System.out.println(instance);
        }
    }
}