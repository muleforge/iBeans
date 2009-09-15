/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ibeans.aws;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.State;
import org.mule.ibeans.api.client.Usage;
import org.mule.ibeans.api.client.params.PropertyParam;
import org.mule.ibeans.api.client.params.UriParam;

import org.w3c.dom.Document;

@Usage("Some AWS usage text")
public interface AwsIBean
{
    @UriParam("SignatureMethod")
    String DEFAULT_SIGNATURE_METHOD = "HmacSHA256";

    @UriParam("SignatureVersion")
    String DEFAULT_SIGNATURE_VERSION = "2";

    @UriParam("Expires")
    AwsIso8601DateFactory EXPIRES_EVALUATOR = new AwsIso8601DateFactory();

    @UriParam("Signature")
    AwsSignatureFactory SIGNATURE_EVALUATOR = new AwsSignatureFactory();

    @State
    void init(@PropertyParam("aws_secret_key") byte[] awsKey, @UriParam("AWSAccessKeyId") String awsAccessKeyId, @UriParam("Version") String version);

    // Note that param placeholder name must match param name exactly
    @Call(uri = "https://ec2.amazonaws.com/?Action=DescribeImages&AWSAccessKeyId={AWSAccessKeyId}&ImageId.1={ImageId.1}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    Document describeImages(@UriParam("ImageId.1") String imageId) throws CallException;

    // Note that param placeholder name must match param name exactly
    @Call(uri = "https://ec2.amazonaws.com/?Action=DescribeImages&AWSAccessKeyId={AWSAccessKeyId}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    Document describeImages() throws CallException;


    // TODO leverage @Optional. Also, what about multiple @Optional?
    // Note that param placeholder name must match param name exactly
    @Call(uri = "https://ec2.amazonaws.com/?Action=DescribeInstances&AWSAccessKeyId={AWSAccessKeyId}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    Document describeInstances() throws CallException;

    // Note that param placeholder name must match param name exactly
    @Call(uri = "https://ec2.amazonaws.com/?Action=RunInstances&ImageId={ImageId}&MinCount={MinCount}&MaxCount={MaxCount}&KeyName={KeyName}&AWSAccessKeyId={AWSAccessKeyId}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    Document runInstance(
            @UriParam("ImageId") String imageId,
            @UriParam("MinCount") int minCount,
            @UriParam("MaxCount") int maxCount,
            @UriParam("KeyName") String keyName) throws CallException;

    // Note that param placeholder name must match param name exactly
    @Call(uri = "https://ec2.amazonaws.com/?Action=TerminateInstances&InstanceId.1={InstanceId.1}&AWSAccessKeyId={AWSAccessKeyId}&Signature={Signature}&SignatureMethod={SignatureMethod}&SignatureVersion={SignatureVersion}&Expires={Expires}&Version={Version}")
    Document terminateInstance(@UriParam("InstanceId.1") String instanceId) throws CallException;
}
