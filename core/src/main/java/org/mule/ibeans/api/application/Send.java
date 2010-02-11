package org.mule.ibeans.api.application;

import org.mule.config.annotations.endpoints.Channel;
import org.mule.config.annotations.endpoints.ChannelType;
import org.mule.config.annotations.endpoints.SupportedMEPs;
import org.mule.impl.endpoint.MEP;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that allows developers to configure an outbound channel for the service. Developers can use more than
 * one annotation to add more channels to a component.
 * <p/>
 * For some types of channels such as JMS, a connector must be available in the registry when this iBean is initialized.
 * If you have multiple connectors in the registry for the same channel, such as one transactional JMS connector and one
 * non-transactional JMS connector, you can specify the connector name on the channel configuration builder and specify
 * that builder using the config argument on the Send iBean.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Channel(identifer = "send", type = ChannelType.Outbound)
@SupportedMEPs(MEP.OutOnly)
public @interface Send
{
    /**
     * The URI address of the channel to use. Using a literal URI is not necessibly good practice, since you will be
     * embedding configuration information that may make your bean location-dependent. However, for some beans, this doesn't
     * matter. One option is to use a property placeholder for the URI, and iBeans will inject the real value at runtime. For
     * example:
     * <code>@Send(uri = "${my.channel}")</code>
     * The URI would then be resolved to a property called 'my.channel' that is registered with the registry and would contain the
     * real URI value, such as imap://foo:bar@mail.myhost.com.
     * <p/>
     * Note that some channels such as JMS need additional configuration. Use the 'config' element instead of 'uri' to
     * configure the annotation with a {@link org.mule.ibeans.config.ChannelConfigBuilder}. Do not use both elements,
     * or an {@link org.mule.ibeans.api.IBeansException} will be thrown.
     *
     * @return A string representation of the channel URI, name, or property placeholder
     */
    String uri() default "";

    /**
     * A reference to the {@link org.mule.ibeans.config.ChannelConfigBuilder} object that is used to configure this channel.
     * <p/>
     * The channel configuration builder is used to describe the properties of the channel used by this annotation. This allows
     * channel configuration to be separated from your code, or for channel configuration to be changed for different environments.
     * Additionally, some channels such as JMS and JDBC need additional configuration such as data sources or connection factories,
     * which can be set on the connector element of the {@link org.mule.ibeans.config.ChannelConfigBuilder}.
     * <p/>
     * This attribute can be used to reference a local channel configuration builder using the name of the builder, or the user
     * can specify the location of the channel configuration builder inside the Mule service registry:
     * <code>
     * galaxy:/Applications/iBeans/Foo/MyScheduler
     * </code>
     * <p/>
     * Note that the base URL for the Mule service registry must be set when iBeans is started: TODO describe how
     * <p/>
     * The 'config' attribute and the 'uri' attribute are mutually exclusive. If both are set, an {@link org.mule.ibeans.api.IBeansException} will be thrown.
     *
     * @return The name of a local channel configuration object or the location of a remote channel configuration builder
     */
    String config() default "";


    /**
     * An optional identifier for this channel. This is only used by iBeans when the uri attribute is used and identifies
     * the endpoint when logging messages, when firing notifications, and for JMX management.
     * <p/>
     * You can access the ID from your code via the {@link org.mule.ibeans.IBeansContext}.
     *
     * @return The name associated with this channel
     */
    String id() default "";

    /**
     * An expression that can be used to split the outgoing message. There are two modes:
     * <ol>
     * <li>If the annotated method returns a {@link java.util.List}, the user can set this attribute to 'default' and iBeans will
     * send each entry of the returned list as a separate message.</li>
     * <li>Users can also define a Mule expression here. So if the method returns an XML document, an XPath split expression can be
     * used to break the message up, e.g., #[xpath:/Batch/Trade].
     * <p/>
     * NOTE: This is an experimental feature that may or may not be included in the iBeans 1.0 release
     *
     * @return The split expression to use when sending the outbound message or an empty string if no split expression was set.
     */
    String split() default "";

    /**
     * Defines one or more key/value pairs of data that will be used for adding properties to the channel. Properties
     * define settings that the underlying transport can use to configure how the listener is registered. Properties are expressed as
     * a comma-separated list of key/value pairs, such as:
     * <code>properties = HTTP.GET</code>
     * <p/>
     * These properties will be channel-specific and are documented in the transports section of the
     * <a href="http://www.mulesoft.org/display/MULE2USER/Home">Mule User Guide</a>.
     * <p/>
     * Note that simple conversion for types including boolean, String, int, long, float and double will be handled implicitly. Other types
     * can be passed in using property placeholders. For example, but adding a property "default.http.method=GET to the iBeans context, that
     * property can be referenced using -
     * <p/>
     * <code>properties = HTTP.METHOD_KEY + "=${default.http.method}"</code>
     * <p/>
     * Note that the placeholder starts with a '$' this is because it is resolved by iBeans by looking in the iBeans context (the property can also be
     * set as a system property).
     *
     * @return An array list of key/value pairs or an empty string if no
     *         properties are set
     */
    String[] properties() default {""};
}
