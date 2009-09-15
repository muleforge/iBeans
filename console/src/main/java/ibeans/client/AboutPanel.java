/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.client;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.Style;

/**
 * TODO
 */
public class AboutPanel extends LayoutContainer
{

    public AboutPanel() {
        final Dialog simple = new Dialog();
        simple.setHeading("About...");
        simple.setButtons(Dialog.OK);
        simple.setBodyStyleName("pad-text");
        simple.add(getAboutText());
        simple.setScrollMode(Style.Scroll.AUTO);
        simple.setHideOnButtonClick(true);
        simple.setWidth(500);
        simple.show();
    }

    // FIXME:
    // we need version info, license info, support info, etc...
    public Html getAboutText() {
        Html html = new Html();
        html.setHtml("<b>Mule iBeans</b>" +
                "<br><br> " +
                "TODO Add Version info, support info, URL etc");

        return html;
    }

}
