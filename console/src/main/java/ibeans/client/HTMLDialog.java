/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.client;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;

/**
 * TODO
 */
public class HTMLDialog extends Dialog
{
    public HTMLDialog(String title, String html)
    {
        setBodyBorder(false);
        setHeading(title);
        setWidth(600);
        setHeight(400);
        setHideOnButtonClick(true);
        setLayout(new FlowLayout());
        Html body = new Html(html);
        body.setAutoWidth(true);
        add(body);
    }
}
