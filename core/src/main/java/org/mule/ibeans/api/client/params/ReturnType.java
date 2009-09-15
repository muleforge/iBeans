package org.mule.ibeans.api.client.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a {@link Class} field or parameter as the default return type for method calls that are assignable to the
 * Class type that has this annotation.
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReturnType
{
    //Bpublic Class[] supported() default {Object.class};
}