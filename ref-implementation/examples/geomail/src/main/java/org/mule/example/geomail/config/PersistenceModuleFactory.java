/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.example.geomail.config;

import org.mule.module.guice.GuiceModuleFactory;

import com.google.inject.Module;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.UnitOfWork;

/**
 * Creates a JPA persistence module with session per request transaction demarcation
 */
public class PersistenceModuleFactory implements GuiceModuleFactory
{
    public Module createModule()
    {
        return PersistenceService.usingJpa()
                .across(UnitOfWork.REQUEST)
                .buildModule();
    }
}
