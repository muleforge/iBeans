/*
 * $Id: Interceptor.java 115 2009-10-22 04:08:33Z dfeist $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.ibeans.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides an extension point that can be used when the use of standard IBeans
 * client annotations are not enough to implement what is required and some code is
 * required. Intercepters can be used with @Call and @Template annotations and must
 * implement the {@link CallInterceptor}. {@link AbstractCallInterceptor} can be
 * extended and provides simple methods for doing things before and after
 * inovocation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Interceptor
{
    Class value();

}
