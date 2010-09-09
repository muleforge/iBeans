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

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import ibeans.client.util.InlineFlowPanel;

/**
 * TODO
 */
public class StatusPanel extends InlineFlowPanel
{
    Image image = new Image();
    Label label = new Label();

    public StatusPanel()
    {
        this.setStyleName("status-bar");
        //this.add(image);
        this.add(label);
        reset();
    }

    public void setError(String error)
    {
        label.setStyleName("error-icon");
        label.setText(error);
    }

    public void setWarning(String warning)
    {
        label.setStyleName("warning-icon");
        label.setText(warning);
    }

    public void setInfo(String message)
    {
        label.setStyleName("info-icon");
        label.setText(message);
    }

    public void reset()
    {
        setInfo("Ready for Action");
    }

    public String getCurrentIconStyle()
    {
        return label.getStyleName();
    }

}