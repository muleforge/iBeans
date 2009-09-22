/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ibeans.aws.ec2;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.params.PropertyParam;
import org.mule.ibeans.api.client.params.UriParam;
import org.mule.ibeans.api.client.params.Order;
import org.mule.ibeans.aws.ec2.EC2Iso8601DateFactory;
import org.mule.ibeans.aws.ec2.EC2SignatureFactory;

import java.util.List;

@Usage("Some AWS usage text")
public interface EC2IBean
{
    @UriParam("SignatureMethod")
    final String DEFAULT_SIGNATURE_METHOD = "HmacSHA256";

    @UriParam("SignatureVersion")
    final String DEFAULT_SIGNATURE_VERSION = "2";

    @UriParam("Expires") @Order(1)
    final EC2Iso8601DateFactory EXPIRES_DATE_FACTORY = new EC2Iso8601DateFactory();

    @UriParam("Signature") @Order(2)
    final EC2SignatureFactory SIGNATURE_FACTORY = new EC2SignatureFactory();

    @State
    void init(@PropertyParam("aws_secret_key") byte[] awsKey, @UriParam("AWSAccessKeyId") String awsAccessKeyId, @UriParam("Version") String version);

    // Note that param placeholder name must match param name exactly
    @Call(uri = "https://aws.amazonaws.com/?Action=DescribeImages&AWSAccessKeyId={AWSAccessKeyId}&ImageId.1={ImageId.1}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    Image describeImage(@UriParam("ImageId.1") String imageId) throws CallException;

    @Call(uri = "https://aws.amazonaws.com/?Action=DescribeImages&AWSAccessKeyId={AWSAccessKeyId}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    List<Image> describeImages() throws CallException;


    @Call(uri = "https://aws.amazonaws.com/?Action=DescribeInstances&AWSAccessKeyId={AWSAccessKeyId}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    List<Instance> describeInstances() throws CallException;

    // Note that param placeholder name must match param name exactly
    @Call(uri = "https://aws.amazonaws.com/?Action=RunInstances&ImageId={ImageId}&MinCount={MinCount}&MaxCount={MaxCount}&KeyName={KeyName}&AWSAccessKeyId={AWSAccessKeyId}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    Instance runInstance(
            @UriParam("ImageId") String imageId,
            @UriParam("MinCount") int minCount,
            @UriParam("MaxCount") int maxCount,
            @UriParam("KeyName") String keyName) throws CallException;

    // Note that param placeholder name must match param name exactly
    @Call(uri = "https://aws.amazonaws.com/?Action=TerminateInstances&InstanceId.1={InstanceId.1}&AWSAccessKeyId={AWSAccessKeyId}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    Instance terminateInstance(@UriParam("InstanceId.1") String instanceId) throws CallException;
}
