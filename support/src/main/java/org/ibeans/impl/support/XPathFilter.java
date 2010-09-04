/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.support;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.ibeans.api.IBeansException;
import org.ibeans.spi.Filter;

/**
 * TODO
 */
public class XPathFilter<Document> implements Filter<Document>
{
    protected XPath xpath;
    protected String expression;

    public XPathFilter(String expression, NamespaceMap namespaces) throws XPathExpressionException
    {
        this.xpath = XPathFactory.newInstance().newXPath();
        this.xpath.setNamespaceContext(namespaces);
        this.expression = expression;
    }

    public boolean accept(Document object) throws IBeansException
    {
        try
        {
            return (Boolean)xpath.evaluate(expression, object, XPathConstants.BOOLEAN);
        }
        catch (XPathExpressionException e)
        {
            throw new IBeansException(e);
        }
    }
}
