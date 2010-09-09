package org.ibeans.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applies an expression to a response message from a Call or Template method to easily extract specific data from the response. For example,
 * if a service sends a reply as an XML document, you can extract the value you need from that document.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Return
{
    String value();
}
