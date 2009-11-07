package org.mule.ibeans.api.client.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used on an iBean static field whose type is ParamFactory to configure the order in when field-level param factories
 * are executed on the iBean. Ordering can be important, since one ParamFactory may depend on data generated by a previous
 * ParamFactory.
 * <p/>
 * Using this annotation on a field that is not of type {@link org.mule.ibeans.api.client.params.ParamFactory} will have no effect.
 * <p/>
 * The order is specified as an integer, where 1 is first, and there is no fixed order scheme. A ParamFactory field with a smaller
 * order number will be executed before a ParamFactory field with a higher order number.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order
{
    /**
     * A sequential positive integraer starting at 1. The value defines the order in which a {@link org.mule.ibeans.api.client.params.ParamFactory}
     * field argument will get evaluated.
     *
     * @return the order in which a {@link org.mule.ibeans.api.client.params.ParamFactory} field will get evaluated
     */
    public int value();
}