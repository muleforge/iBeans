/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A wrapper for the module pom to read values from it
 */
public class MavenPomReader
{
    Document doc;
    XPathExpression nameExpression;
    XPathExpression descriptionExpression;
    XPathExpression versionExpression;
    XPathExpression urlExpression;
    XPathExpression organizationExpression;
    XPathExpression authorExpression;
    XPathExpression licenseNameExpression;
    XPathExpression licenseUrlExpression;
    XPathExpression bundledExpression;
    XPathExpression requiredExpression;

    public MavenPomReader(InputStream is) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException
    {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        init(doc);
    }

    public MavenPomReader(File file) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException
    {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        init(doc);
    }

    private void init(Document doc) throws XPathExpressionException
    {
        this.doc = doc;
        nameExpression = XPathFactory.newInstance().newXPath().compile("/project/name");
        descriptionExpression = XPathFactory.newInstance().newXPath().compile("/project/description");
        versionExpression = XPathFactory.newInstance().newXPath().compile("/project/version");
        urlExpression = XPathFactory.newInstance().newXPath().compile("/project/url");
        organizationExpression = XPathFactory.newInstance().newXPath().compile("/project/organization/name");
        authorExpression = XPathFactory.newInstance().newXPath().compile("/project/developers/developer/name");
        licenseNameExpression = XPathFactory.newInstance().newXPath().compile("/project/licenses/license/name");
        licenseUrlExpression = XPathFactory.newInstance().newXPath().compile("/project/licenses/license/url");
        bundledExpression = XPathFactory.newInstance().newXPath().compile("/project/properties/bundled");
        requiredExpression = XPathFactory.newInstance().newXPath().compile("/project/properties/required");
    }

    public String getName()
    {
        return eval(nameExpression);
    }

    public String getVersion()
    {
        return eval(versionExpression);
    }

    public String getDescription()
    {
        return eval(descriptionExpression);
    }

    public String getUrl()
    {
        return eval(urlExpression);
    }

    public String getOrganization()
    {
        return eval(organizationExpression);
    }

    public String getAuthor()
    {
        String author = eval(authorExpression);
        if (author == null)
        {
            author = getOrganization();
        }
        else
        {
            author = author + ", " + getOrganization();
        }

        return author;
    }

    public boolean isBundled()
    {
        return "TRUE".equalsIgnoreCase(eval(bundledExpression));
    }

    public boolean isRequired()
    {
        return "TRUE".equalsIgnoreCase(eval(requiredExpression));
    }

    public String getLicenseName()
    {
        return eval(licenseNameExpression);
    }

    public String getLicenseUrl()
    {
        return eval(licenseUrlExpression);
    }

    protected String eval(XPathExpression exp)
    {
        try
        {
            return exp.evaluate(doc);
        }
        catch (XPathExpressionException e)
        {
            return null;
        }
    }
}
