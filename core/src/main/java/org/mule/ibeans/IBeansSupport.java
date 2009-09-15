/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * TODO
 */
public class IBeansSupport
{
    /**
     * logger used by this class
     */
    protected static transient final Log logger = LogFactory.getLog(IBeansSupport.class);

    private static XPath createXPath(Node node)
    {
        XPath xp = XPathFactory.newInstance().newXPath();
        if (node instanceof Document)
        {
            xp.setNamespaceContext(new XPathNamespaceContext((Document) node));
        }
        return xp;
    }

    public static Node selectOne(String xpath, Node node)
    {
        try
        {
            XPath xp = createXPath(node);
            return (Node) xp.evaluate(xpath, node, XPathConstants.NODE);
        }
        catch (XPathExpressionException e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static String selectValue(String xpath, Node node)
    {
        try
        {
            XPath xp = createXPath(node);
            return (String) xp.evaluate(xpath, node, XPathConstants.STRING);
        }
        catch (XPathExpressionException e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static List<Node> select(String xpath, Node node)
    {
        try
        {
            XPath xp = createXPath(node);
            NodeList nl = (NodeList) xp.evaluate(xpath, node, XPathConstants.NODESET);
            List<Node> nodeList = new ArrayList<Node>(nl.getLength());
            for (int i = 0; i < nl.getLength(); i++)
            {
                nodeList.add(nl.item(i));
            }
            return nodeList;
        }
        catch (XPathExpressionException e)
        {
            logger.error(e.getMessage());
            return null;
        }
    }

    static class XPathNamespaceContext implements NamespaceContext
    {
        private Document document;

        public XPathNamespaceContext(Document document)
        {
            this.document = document;
        }

        public String getNamespaceURI(String prefix)
        {
            if (prefix == null || prefix.equals(""))
            {
                return document.getDocumentElement().getNamespaceURI();
            }
            else
            {
                return document.lookupNamespaceURI(prefix);
            }
        }

        public String getPrefix(String namespaceURI)
        {
            return document.lookupPrefix(namespaceURI);
        }

        public Iterator<String> getPrefixes(String namespaceURI)
        {
            List<String> list = new ArrayList<String>();
            list.add(getPrefix(namespaceURI));
            return list.iterator();
        }
    }

}
