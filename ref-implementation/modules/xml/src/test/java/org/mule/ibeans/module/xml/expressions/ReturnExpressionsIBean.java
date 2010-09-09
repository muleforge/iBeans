/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.xml.expressions;

import org.mule.ibeans.api.client.Call;
import org.mule.ibeans.api.client.CallException;
import org.mule.ibeans.api.client.ExceptionListenerAware;
import org.mule.ibeans.api.client.Return;
import org.mule.ibeans.api.client.Template;
import org.mule.ibeans.api.client.params.ReturnType;

import org.w3c.dom.Document;

public interface ReturnExpressionsIBean extends ExceptionListenerAware
{
    @Template("<foo><bar>true</bar></foo>")
    @Return("xpath2:[boolean]/foo/bar")
    public Boolean testBooleanReturn() throws CallException;

    @Template("<foo><bar>true</bar></foo>")
    @Return("xpath2:[string]/foo/bar")
    public String testStringReturn() throws CallException;

    @Template("<foo><bar>14</bar></foo>")
    @Return("xpath2:[number]/foo/bar")
    public Integer testNumberReturn() throws CallException;

    @Template("<foo><bar>true</bar></foo>")
    @Return("xpath2:[node]/foo/bar")
    public Document testDomReturn() throws CallException;

    //For mock testing only
    @ReturnType
    public static final Class DEFAULT_RETURN_TYPE = Document.class;

    @Call(uri = "http://erm.co.uk")
    @Return("xpath2:[string]/foo/bar")
    public String getSomeValue() throws CallException;
}