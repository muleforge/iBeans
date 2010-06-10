/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ibeans.module.guice;

import org.mule.util.pool.CommonsPoolObjectPool;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * Currently not used, this is just a spike to figure out how pooling config could be associated with the scope
 * leaving it in the codebase for reference only
 */
public class MuleScopes
{
    private static Scope POOLED = new PooledScope();

    public static Scope pooledScope(int size, PooledScope.INIT_POLICY initPolicy, PooledScope.EXHAUSTED_POLICY exhaustedPolicy)
    {
        return new PooledScope(size, initPolicy, exhaustedPolicy);
    }

    public static Scope pooledScope()
    {
        return POOLED;
    }

    static class PooledScope implements Scope
    {
        public static enum INIT_POLICY
        {
            InitialiseOne,
            InitialiseAll
        }

        public static enum EXHAUSTED_POLICY
        {
            Wait,
            Fail,
            Grow
        }

        private int size = 10;
        private INIT_POLICY initPolicy = INIT_POLICY.InitialiseOne;
        private EXHAUSTED_POLICY exhaustedPolicy = EXHAUSTED_POLICY.Grow;
        private CommonsPoolObjectPool pool;

        private PooledScope()
        {
        }

        private PooledScope(int size, INIT_POLICY initPolicy, EXHAUSTED_POLICY exhaustedPolicy)
        {
            this.size = size;
            this.initPolicy = initPolicy;
            this.exhaustedPolicy = exhaustedPolicy;
        }

        public <T> Provider<T> scope(Key<T> tKey, Provider<T> creator)
        {
            //TODO create a Provider that manages a Pool of objects, using the creator as a factory
            return creator;
        }

        @Override
        public String toString()
        {
            return "MuleScopes.POOLED";
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            PooledScope that = (PooledScope) o;

            return size == that.size && exhaustedPolicy == that.exhaustedPolicy && initPolicy == that.initPolicy;

        }

        @Override
        public int hashCode()
        {
            int result = size;
            result = 31 * result + (initPolicy != null ? initPolicy.hashCode() : 0);
            result = 31 * result + (exhaustedPolicy != null ? exhaustedPolicy.hashCode() : 0);
            return result;
        }
    }
}
