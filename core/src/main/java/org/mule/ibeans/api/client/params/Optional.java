package org.mule.ibeans.api.client.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used to mark a {@link org.mule.ibeans.api.client.Call} or {@link org.mule.ibeans.api.client.State} method parameter as optional so that users can pass in null
 * as a valid argument. By default all method parameters are required, and if set to null an exception will be thrown.
 * Usage:
 * <code>
 * &amp;#064;Call(uri = "http://www.twitter.com/statuses/update.{format}")
 * public &amp;lt;T&amp;gt; T updateStatus(@PayloadParam("status") String status, @Optional @PayloadParam("in_reply_to_status_id") String replyId) throws CallException;
 * </code>
 * <p/>
 * Here the 'status' parameter is required, but the 'replyId' is marked as optional.
 * <p/>
 * Note that when UriParam or ComplexUriParam are optional the parameter is removed from the call URI when the optional param is part of the query string for example -
 * <code>
 * &amp;#064;Call(uri = "http://www.foo.com?param1={foo}&param2={bar}")
 * public String doStuff(@UriParam("foo") String foo, @Optional @UriParam("bar") String bar) throws CallException;
 * </code>
 * If the doStuff method is -
 * <p/>
 * <code>
 * String result = doStuff("myValue", null);
 * </code>
 * <p/>
 * The URI will be resolved as -
 * <p/>
 * <code>
 * http://www.foo.com?param1=myValue
 * </code>
 * If you want to keep the empty param on the URI, just use an empty string instead of a null value.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Optional
{
}
