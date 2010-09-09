package org.ibeans.annotation.param;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A parameter annotation for use with the {@link org.mule.ibeans.api.client.Call} annotation that will configure a parameter defined in a @Call
 * or @Get URI. This allows users to construct dynamic URIs based on what the user passes into the method, which is useful for
 * REST calls where values in the path must be substituted. For example:
 * <code>
 * <p/>
 * &amp;#064;Call(uri = "http://twitter.com/statuses/update.{format}")
 * public String updateStatus(@BodyParam("status") String status, @UriParam("format") String format) throws CallException;
 * </code>
 * The format param tells Twitter which format to use in the response (json, xml, rss, atom). The format param is substituted when
 * the method is called.
 * <p/>
 * Note that fields on the client iBean interface can also have this annotation configured. This allows constants to be configured:
 * <code>
 * &amp;#064;UriParam("default_format")
 * public static final String DEFAULT_TWITTER_FORMAT = "json";
 * <p/>
 * &amp;#064;Call(uri = "{twitter_url}/statuses/update.{default_format}")
 * public String updateStatus(@BodyParam("status") String status) throws CallException;
 * </code>
 * <p/>
 * Some APIs have many optional arguments and it may not be practical (or desirable) to create UriParam parameters for each option
 * instead the @UriParam annotation can be used to annotate a {@link java.util.Map} method argument that contains one or
 * more optional parameters. This value on this annotation must be set to a comma separated list of param names that can be set inside the map.
 * <p/>
 * For example, lets say our API accepts 4 optional parameters, we can define a UriParam Map that users can create to pass in multiple param values
 * <code>
 * &amp;#064;Call(uri = "http://api.mine.com/doStuff?foo={foo}&bar={bar}&baz={baz}&faz={faz}")
 * public String updateStatus(@UriParam("foo, bar, baz, faz") Map params) throws CallException;
 * </code>
 * At runtime the user can create a map containing values for 'foo=fooValue' and 'faz=fazValue' and the resulting request
 * URI would look like: "http://api.mine.com/doStuff?foo=fooValue&faz=fazValue
 *
 * @see org.mule.ibeans.api.client.Call
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UriParam
{
    /**
     * The param The name of the parameter. This name is referenced in the URI surrounded using '{' and '}'. For example -
     * <code>
     * &amp;#064;ReceiveAndReply(uri='http://my.com/api/{user}')
     * public User getUser(@UriParam("user") String username)
     * </code>
     * <p/>
     * Some APIs have many optional arguments and it may not be practical (or desirable) to create UriParam parameters for each option
     * instead the @UriParam annotation can be used to annotate a {@link java.util.Map} method argument that contains one or
     * more optional parameters. This value must be set to a comma separated list of param names that can be set inside the map.
     *
     * @return the param The name of this annotated method parameter.
     */
    public String value();
}