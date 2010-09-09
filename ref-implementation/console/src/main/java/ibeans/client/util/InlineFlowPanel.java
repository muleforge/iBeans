/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package ibeans.client.util;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO
 */
public class InlineFlowPanel extends FlowPanel
{

    public void add(Widget w) {
        DOM.setStyleAttribute(w.getElement(), "display", "inline");
        super.add(w);
    }

    public void insert(Widget w, int beforeIndex) {
        DOM.setStyleAttribute(w.getElement(), "display", "inline");
        super.insert(w, beforeIndex);
    }

}
