/*
 * $Id$
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.impl.support;


import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.ibeans.api.IBeansException;
import org.ibeans.spi.ErrorFilter;

/**
 * TODO
 */
public class XPathErrorFilter<Document> extends XPathFilter<Document> implements ErrorFilter<Document>
{
    private String errorCode;
    private String errorCodeExpression;

    public XPathErrorFilter(String xpath, NamespaceMap namespaces, String errorCodeExpression) throws XPathExpressionException
    {
        super(xpath, namespaces);
        this.errorCodeExpression = errorCodeExpression;
    }

    @Override
    public boolean accept(Document document) throws IBeansException
    {
        boolean accept = super.accept(document);
        if(accept && errorCodeExpression!=null)
        {
            try
            {
                errorCode = (String)xpath.evaluate(errorCodeExpression, document, XPathConstants.STRING);
            }
            catch (XPathExpressionException e)
            {
                throw new IBeansException(e);
            }
        }
        return accept;
    }

    public String getErrorCodeExpression()
    {
        return errorCode;
    }

    public String getErrorExpression()
    {
        return errorCodeExpression;
    }

    public String getType()
    {
        return "xpath";
    }
}
