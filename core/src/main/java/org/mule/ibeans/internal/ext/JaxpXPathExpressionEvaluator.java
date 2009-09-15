/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.internal.ext;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.expression.ExpressionEvaluator;
import org.mule.api.expression.ExpressionRuntimeException;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.registry.RegistrationException;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.xml.i18n.XmlMessages;
import org.mule.module.xml.stax.MapNamespaceContext;
import org.mule.module.xml.util.NamespaceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Uses JAXP XPath processing to evaluate xpath expressions against Xml fragments and documents
 */
public class JaxpXPathExpressionEvaluator implements ExpressionEvaluator, Disposable, MuleContextAware
{

    private Map cache = new WeakHashMap(8);

    private MuleContext muleContext;
    private NamespaceManager namespaceManager;
    private QName returnType = XPathConstants.STRING;

    public JaxpXPathExpressionEvaluator()
    {

    }

    public String getName()
    {
        return "xpath2";
    }

    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
        try
        {
            namespaceManager = muleContext.getRegistry().lookupObject(NamespaceManager.class);
        }
        catch (RegistrationException e)
        {
            throw new ExpressionRuntimeException(CoreMessages.failedToLoad("NamespaceManager"), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object evaluate(String expression, MuleMessage message)
    {
        //TODO FIXME
        QName retType = (QName) message.removeProperty("xpath.return");
        if (retType == null)
        {
            retType = returnType;
        }
        try
        {
            Node payload = message.getPayload(Node.class);

            XPathExpression xpath = getXPath(expression);

            Object res = xpath.evaluate(payload, retType);
            List result = extractResultsFromNodes(res);
            if (result.size() == 1)
            {
                return result.get(0);
            }
            else if (result.size() == 0)
            {
                return null;
            }
            else
            {
                return result;
            }
        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(XmlMessages.failedToProcessXPath(expression), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void setName(String name)
    {
        throw new UnsupportedOperationException("setName");
    }

    protected XPathExpression getXPath(String expression) throws XPathExpressionException
    {
        XPathExpression xpath = (XPathExpression) cache.get(expression + getClass().getName());
        if (xpath == null)
        {
            xpath = createXPath(expression);
            cache.put(expression + getClass().getName(), xpath);
        }
        return xpath;
    }

    protected XPathExpression createXPath(String expression) throws XPathExpressionException
    {
        XPath xp = XPathFactory.newInstance().newXPath();
        if (getNamespaceManager() != null)
        {
            xp.setNamespaceContext(new MapNamespaceContext(getNamespaceManager().getNamespaces()));
        }
        return xp.compile(expression);
    }

    protected List extractResultsFromNodes(Object results)
    {
        List newResults = null;
        if (results instanceof NodeList)
        {

            NodeList nl = (NodeList) results;
            newResults = new ArrayList(nl.getLength());
            for (int i = 0; i < nl.getLength(); i++)
            {
                Node n = nl.item(i);
                newResults.add(extractResultFromNode(n));
            }
        }
        else if (results != null)
        {
            newResults = new ArrayList(1);
            newResults.add(extractResultFromNode(results));
        }

        return newResults;
    }

    /**
     * A lifecycle method where implementor should free up any resources. If an
     * exception is thrown it should just be logged and processing should continue.
     * This method should not throw Runtime exceptions.
     */
    public void dispose()
    {
        cache.clear();
    }

    public NamespaceManager getNamespaceManager()
    {
        return namespaceManager;
    }

    public void setNamespaceManager(NamespaceManager namespaceManager)
    {
        this.namespaceManager = namespaceManager;
    }

    public MuleContext getMuleContext()
    {
        return muleContext;
    }

    protected Object extractResultFromNode(Object result)
    {
        if (result instanceof Node)
        {
            return ((Node) result).getNodeValue();
        }
        else
        {
            return result;
        }
    }

    public QName getReturnType()
    {
        return returnType;
    }

    public void setReturnType(QName returnType)
    {
        this.returnType = returnType;
    }
}
