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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

/**
 * TODO
 */
public class ExternalHyperlink extends Widget
{
    private final Element anchorElem;

    public ExternalHyperlink(final String text, final String link)
    {
        this(text, link, null);
    }

    public ExternalHyperlink(final String text, final String link,
                             final String target)
    {
        super();

            setElement(DOM.createDiv());
            this.anchorElem = DOM.createAnchor();
            DOM.appendChild(getElement(), this.anchorElem);
            setLink(link);
            setText(text);

            if (target != null)
            {
                setTarget(target);
            }
    }

    public final void setText(final String text)
    {
        DOM.setInnerHTML(this.anchorElem, text);
    }

    public final void setLink(final String link)
    {
        DOM.setAttribute(this.anchorElem, "href", link);
    }

    public final String getText()
    {
        return DOM.getInnerHTML(this.anchorElem);
    }

    public final String getLink()
    {
        return DOM.getAttribute(this.anchorElem, "href");
    }

    public final String getTarget()
    {
        return DOM.getAttribute(this.anchorElem, "target");
    }

    public final void setTarget(final String target)
    {
        DOM.setAttribute(this.anchorElem, "target", target);
    }

    
}

