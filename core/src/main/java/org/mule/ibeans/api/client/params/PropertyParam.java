package org.mule.ibeans.api.client.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A general purpose parameter annotation that can be used to add meta data to an iBean's state or individual calls.  When used on
 * a method such as a {@link org.mule.ibeans.api.client.Call} method a PropertyParam can be used to pass in data to a {@link org.mule.ibeans.api.client.params.ParamFactory} or
 * {@link org.mule.ibeans.api.client.params.PayloadFactory}.  Note that in this scenario, the PropertyParam cannot be an instance of
 * a Factory.
 * <p/>
 * When with the {@link org.mule.ibeans.api.client.State} annotation that will configure a property value defined in
 * a @Call 'property' attribute. These properties are used to configure the channel (defined in the 'uri' attribute)
 * and thus can only be configured on methods marked with the {@link org.mule.ibeans.api.client.State} annotation. They are usually called before
 * any other methods on the iBean. For example:
 * <code>
 * &amp;#064;State
 * public void init(@HeaderParam("user") String user, @HeaderParam("password") String password, @PropertyParam("followRedirects") boolean followRedirects);
 * </code>
 * <p/>
 * Note that fields on the client iBean interface can also have this annotation configured. This allows constants to be configured.
 * <code>
 * &amp;#064;PropertyParam("followRedirects")
 * public static final boolean DEFAULT_FOLLOW_REDIRECTS = true;
 * </code>
 * These properties can then be referenced on the 'properties' attribute of {@link org.mule.ibeans.api.client.Call} annotations -
 * <code>
 * &amp;#064;Call(uri = "http://www.twitter.com/statuses/friends_timeline.xml?count={count}", properties = "followRedirects={doRedirects}")
 * public String getFriendTimeline(@UriParam("count") int count) boolean doRedirects) throws CallException;
 * </code>
 * <p/>
 * Note that you can specify a {@link java.util.Map} of properties where each entry in the map argument will be added as a PropertyParam.
 * To do this, specify the parameter as a Map and provide an empty string as an argument to this annotation.
 * For example:
 * <code>
 * &amp;#064;State
 * public void init(@HeaderParam("user") String user, @HeaderParam("password") String password, @Optional @PropertyParam("") Map props);
 * </code>
 *
 * @see org.mule.ibeans.api.client.State
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertyParam
{
    /**
     * The value of this annotation defines the property name, whereas the actual parameter defines the property value. If the annotated parameter is a
     * {@link java.util.Map}, the value can be set to an empty string.
     *
     * @return The property name to associate with the parameter value
     */
    public abstract String value();
}