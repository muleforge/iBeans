package org.ibeans.annotation;



import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be added to an interface method to proxy the method call through the given channel URI. Interfaces
 * with this annotation are referred to as client iBeans and can be used as re-usable services that will run in any iBeans
 * container. For this reason, expect to find lots of client iBeans for social media and web APIs such as Twitter, Amazon,
 * Facebook, etc.
 * <p/>
 * Typically, a Call annotation will access a REST/HTTP service but could access other services such as XMPP. Certain
 * channels cannot be used, since their configuration will have dependencies on the container they're running. Channels like
 * JMS or JDBC need additional configuration such as connection factories or data sources, respectively.
 * <p/>
 * Methods annotated with Call must throw an exception. iBeans defines a {@link org.mule.ibeans.api.client.CallException} that you can use.
 * This exception type has an error code that is specific to the channel. For example, for HTTP the error code will be the
 * return code. If you do not want to use the {@link org.mule.ibeans.api.client.CallException}, you can use {@link Exception} instead.
 * The exception thrown must be assignable to {@link org.mule.ibeans.api.client.CallException}.
 * Note that if the exception is thrown, the return error and any information about the response will be available in this exception.
 * <p/>
 * Obviously, most public services need authentication such as HTTP Basic or OAuth. Authentication capabilities can be added
 * to client iBeans by extending interfaces that introduce usually a single method where the authentication information (such
 * as username, password, or API key) can be set first. For example, by extending {@link org.mule.ibeans.api.client.authentication.HttpBasicAuthentication}
 * on a client iBean, a new <code>setCredentials(user, pass)</code> method will be added to the interface that users can call,
 * and iBeans will handle the credentials for each request.
 *
 * @see org.ibeans.annotation.param.HeaderParam
 * @see org.ibeans.annotation.param.Body
 * @see org.ibeans.annotation.param.BodyParam
 * @see org.ibeans.annotation.param.UriParam
 * @see Template
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
//@Channel(identifer = "call", type = ChannelType.Outbound)
//@SupportedMEPs({MEP.OutIn, MEP.OutOnly, MEP.OutOptionalIn})
public @interface Call
{
    /**
     * The resource URI address of the channel to use. Parameters can be used to configure the URI, and these parameters can be
     * passed into the method call. For example:
     * <code>
     * &amp;#064;Call(uri = "http://twitter.com/statuses/update.{format}")
     * public String updateStatus(@BodyParam("status") String status, @UriParam("format") String format) throws CallException;
     * </code>
     * Here the 'format' is passed in as a URI parameter and the 'status' is sent as a payload parameter in the HTTP POST call
     * (iBeans uses POST by default)
     * <p/>
     * There are other parameter annotations that can be used for specifying the payload and headers of the request.
     * <p/>
     *
     * @return A string representation of the channel URI to call.
     */
    String uri();

    /**
     * Defines one or more key/value pairs of data that will be used by the channel for sending the outgoing call. Properties
     * define settings that the underlying transport can use to configure how the call is made. Properties are expressed as
     * a comma-separated list of key/value pairs, such as:
     * <code>properties = "http.method=GET"</code>
     * <p/>
     * These properties will be channel-specific and are documented in the transports section of the
     * <a href="http://www.mulesoft.org/display/MULE2USER/Home">Mule User Guide</a>.
     * <p/>
     * Note that simple conversion for types including boolean, String, int, long, float, and double will be handled implicitly.
     * Other types can be passed in using property placeholders. For example, by adding a property default.http.method=GET to
     * the iBeans context, that property can be referenced using the following:
     * <p/>
     * <code>properties = "http.method=${default.http.method}"</code>
     * <p/>
     * Note that the placeholder starts with a '$', which is resolved by iBeans looking in the iBeans context (the property
     * can also be set as a system property).
     *
     * @return A comma-separated list of key/value pairs or an empty string if no
     *         properties are set
     */
    String[] properties() default {};
    
    /**
     * 
     * 
     * @return A comma-separated list of @BodyParam elements that are applied to the call
     */
    String [] bodyParamFilter() default {};
    
    /**
     * 
     * @return A comma-separated list of @HeaderParam elements that are applied to the call
     */
    String [] headerParamFilter() default {};

}
