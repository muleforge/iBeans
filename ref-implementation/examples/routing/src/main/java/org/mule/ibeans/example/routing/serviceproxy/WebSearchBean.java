/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.example.routing.serviceproxy;

import org.mule.module.annotationx.api.ReceiveAndReply;

import org.ibeans.annotation.IntegrationBean;

/**
 * TODO
 */
public class WebSearchBean
{
    @IntegrationBean
    private WebFindIBean webFindIBean;


    @ReceiveAndReply(uri = "vm://find")
    public String searchStuff(String term, SearchEngine engine) throws Exception
    {
        switch (engine)
        {
            case YAHOO:
                return webFindIBean.searchYahoo(term);
            case ASK:
                return webFindIBean.searchAsk(term);
            default:
                return webFindIBean.searchGoogle(term);
        }
    }
}
