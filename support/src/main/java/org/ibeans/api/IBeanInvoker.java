/*
 * $Id:  $
 * -------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.api;

import org.ibeans.impl.TemplateAnnotationHandler;

/**
 * TODO
 */

public interface IBeanInvoker<C extends ClientAnnotationHandler, T extends TemplateAnnotationHandler> extends CallInterceptor
{
    C getCallHandler();

    T getTemplateHandler();
}
