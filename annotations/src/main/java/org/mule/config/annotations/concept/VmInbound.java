package org.mule.config.annotations.concept;

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
 * The VM transport is used for intra-VM communication between components managed by Mule. The
 * transport provides options for configuring VM transient or persistent queues.
 * <p/>
 * This annotation is used to receive events from an intra-VM channel.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Channel(identifer = "vm", type = ChannelType.Inbound)
@SupportedMEPs({MEP.InOnly, MEP.InOptionalOut, MEP.InOut})
public @interface VmInbound
{
    /**
     * The path on which events will be received or sent. This in analogous to a queue name, but given that VM has a
     * queueing and non-queueing mode, you use 'path' to define where events will be sent or received.
     */
    String path();

    /**
     * The Message Exchange Pattern (MEP) determines how events through this endpoint will behave by defining whether
     * a response will be returned or when it will receive or send events. Understanding the behavior of each of the MEPs
     * will help you decide which is the right one to use for a given scenario.
     * The MEPs supported by Mule are fully documented here: http://www.mulesoft.org/display/MULE2USER/MEPs.
     * <p/>
     * Following is a brief overview of the MEPs:
     * <p/>
     * <ul>
     * <li>{@link MEP.InOnly}: an inbound endpoint, no response is expected</li>
     * <li>{@link org.mule.impl.endpoint.MEP.InOut}: an inbound endpoint, a response is always expected</li>
     * <li>{@link org.mule.impl.endpoint.MEP.InOptionalOut}: an inbound endpoint, a response may be expected</li>
     * <li>{@link org.mule.impl.endpoint.MEP.OutOnly}: an outbound endpoint, no response is expected</li>
     * <li>{@link MEP.OutIn}: an outbound endpoint, a response is always expected after a message is sent from this endpoint</li>
     * <li>{@link MEP.OutOptionalIn}: an outbound endpoint, and after a message is dispatched, a response message
     * may be returned.</li>
     * <p/>
     * Different endpoints support different MEPs. The supported MEPs for each endpoint are defined on the endpoint annotation using the
     * {@link org.mule.config.annotations.endpoints.SupportedMEPs} annotation.
     *
     * @return the MEP configured for this endpoint.
     */
    public MEP mep();

    /**
     * An expression filter used to filter out unwanted messages. Filters can be used for content-based routing.
     * The filter syntax uses familiar Mule expression syntax:
     * <code>
     * filter = "#[wildcard:*.txt]"
     * </code>
     * or
     * <code>
     * filter = "#[xpath:count(Batch/Trade/GUID) > 0]"
     * </code>
     * <p/>
     * Filter expressions must result in a boolean or null to mean false
     *
     * @return
     */
    String filter() default "";

    /**
     * The name associated with this endpoint. A name is not required but can be useful for JMX monitoring.
     *
     * @return the name associated with this endpoint. If one is not set, Mule will generate a unique name.
     */
    public String name() default "";

    /**
     * Determines whether queues should be set up for listeners on the connector. If set to false, the connector simply
     * forwards messsages to components via the Mule server. If it is set to true, the queues are configured using the
     * queuing profile.
     *
     * @return true if events will be queued by this endpoint, false otherwise
     */
    public boolean queueEvents() default false;

    /**
     * A comma-separated list of key/value pairs, e.g.,
     * <code>"apple=green, banana=yellow"</code>
     * Property placeholders can be used in these values:
     * <code>"apple=${apple.color}, banana=yellow"</code>
     * <p/>
     * A properties location can also be specified. The two supported modes are:
     * <ol>
     * <li>file:/resources/my.properties - load this file from the classpath or file system</li>
     * <li>galaxy:/myApp/my.properties - loads the properties resource from a galaxy registry</li>
     * </ol>
     *
     * @return A comma-separated list of key/value pairs or an empty string if no properties are set
     */
    String properties() default "";

    /**
     * An optional identifier for this endpoint. This is only used by Mule to identify the endpoint when logging messages,
     * firing notifications, and for JMX management.
     *
     * @return the name associated with this endpoint
     */
    String id() default "";
}
