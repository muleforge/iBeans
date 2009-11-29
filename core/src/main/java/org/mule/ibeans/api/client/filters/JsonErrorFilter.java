package org.mule.ibeans.api.client.filters;

import org.mule.ibeans.channels.MimeTypes;

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
 * This filter is only used on responses that have an 'application/json' mime type. The expression for this filter use the
 * Mule Json query language (see {@link org.mule.module.json.JsonData}).
 * The expression should be a boolean expression. If the expression is not boolean (e.g., returns a node or string), the
 * filter will return true for a non-null result or false otherwise.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ErrorFilter
public @interface JsonErrorFilter
{
    String expr();

    String errorCode() default "";

    String mimeType() default MimeTypes.JSON;

    final String eval = "json";
}
