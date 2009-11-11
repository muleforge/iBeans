/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.test;

import java.util.List;
import java.util.Set;

import javax.activation.DataSource;

/**
 * The Mock interface that defines methods that can be called on an iBean proxy injected using the {@link org.mule.ibeans.api.client.MockIntegrationBean}
 * annotation in a test case.  The iBean proxy can be cast to this interface to query the state of the iBean.
 * <p/>
 * Note that the method names are all prefixed with 'ibean' this is to enusure that there are no method clashes.
 */
public interface MockIBean
{
    public Class ibeanReturnType();

    public Object ibeanUriParam(String name);

    public Object ibeanPayloadParam(String name);

    public Object ibeanHeaderParam(String name);

    public Object ibeanPropertyParam(String name);

    public List<Object> ibeanPayloads();

    public Set<DataSource> ibeanAttachments();

    public void ibeanErrorCheck(Object data, String mimeType) throws Exception;

    public void ibeanSetMimeType(String mime);
}
