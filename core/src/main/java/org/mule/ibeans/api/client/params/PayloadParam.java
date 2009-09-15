package org.mule.ibeans.api.client.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can only be used with the {@link org.mule.ibeans.api.client.Call} annotation and is used to set the parameter as
 * a payload object on the outgoing message. Typically, this is only used when performing HTTP POST where the payload of the
 * outgoing call is a set of key/value objects.
 * <p/>
 * The value of this annotation defines the name of the parameter. The actual parameter is used as the value.
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PayloadParam
{
    /**
     * The name to use for the paylaod parameter
     *
     * @return the name of the paylaod param
     */
    public abstract String value();
}