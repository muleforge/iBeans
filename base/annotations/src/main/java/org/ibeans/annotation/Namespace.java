package org.ibeans.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a namespace that will be used when parsing or evaluating XPATH expressions.  This annotation can
 * be sued on an iBean field to declare a namespaces for return data from a service
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Namespace
{
    /**
     * The namespace prefix, leave blank for default namespace
     *
     * @return
     */
    String value() default "";
}
