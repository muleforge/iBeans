package org.ibeans.annotation.param;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A parameter annotation for use with the {@link org.mule.ibeans.api.client.Call} annotation that will
 * configure a header on an outbound call. Headers can be HTTP headers, JMS headers, Email headers, and so on. For example:
 * <code>
 * &amp;#064;Call(uri = "smtp://{fromAddress}:{password}@{smtp_host}:{smtp_port}")
 * public void send(@HeaderParam("toAddresses") String toAddresses, @HeaderParam("subject") String subject, @Body Object body) throws CallException;
 * </code>
 * <p/>
 * Note that fields on the iBean interface can also have this annotation configured. This allows constants to be configured:
 * <code>
 * &amp;#064;HeaderParam("subject")
 * public static final String DEFAULT_SUBJECT = "Message from iBeans";
 * </code>
 * This means that this header will always be set for every call, but it can be overridden at the call level.
 * <p/>
 * Note that you can specify a {@link java.util.Map} of headers where each entry in the map argument will be added as a
 * HeaderParam. To do this, specify the parameter as a Map and provide an empty string as an argument to this annotation. For example:
 * <code>
 * &amp;#064;Call(uri = "smtp://{fromAddress}:{password}@{smtp_host}:{smtp_port}")
 * public void send(@HeaderParam("") Map mailHeaders, @Body Object body) throws CallException;
 * </code>
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HeaderParam
{
    /**
     * The value of this annotation defines the header name, whereas the actual parameter defines the property value. If the annotated parameter is a
     * {@link java.util.Map}, the value can be set to an empty string.
     *
     * @return The header name to associate with the parameter value
     */
    public String value();
}