package org.mule.ibeans.api.client.filters;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used on an iBean client interface, this class-level annotation is used to check any responses from a call to see if it
 * contains an error response from the server. Note that iBeans will check the return code for HTTP calls, but some services
 * do not set the correct error code. Also, many services define their own error codes because the HTTP response codes are not
 * application-specific enough, so a valid HTTP response may contain error information from the server.
 * <p/>
 * This filters allows for any valid Mule expression including RegEx, Groovy, Wildcard, Header, etc.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ErrorFilter
public @interface GenericErrorFilter
{
    public abstract String expr();

    public String mimeType() default "*";
}