package org.mule.config.annotations.endpoints;

import org.mule.impl.endpoint.MEP;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that allows developers to configure an outbound endpoint for the service.
 * For some types of endpoints such as JMS, a JMS connection is required to be available in the registry when
 * this service is initialized. You can specify the connector name directly if there is more than one Jms connector
 * in the registry (e.g., one transactional, the other not).
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Channel(identifer = "in", type = ChannelType.Inbound)
public @interface In
{
    /**
     * The resource name or URI address of the endpoint to use. The URI would be literal, which is not recommended.
     * Instead, you can use either the name of a global endpoint that is already available in the registry,
     * or you can use a property placeholder and the real value will be injected at runtime. For example:
     * <code>@InboundEndpoint(endpoint = "${my.endpoint}")</code>
     * The endpoint would then be resolved to a property called 'my.endpoint' that is registered with the registry.
     *
     * @return A string representation of the endpoint URI, name, or property placeholder.
     */
    String uri();

    /**
     * Determines if requests originating from this endpoint should be synchronous,
     * i.e., execute in a single thread and possibly return an result. This property
     * is only used when the endpoint is of type 'receiver'.
     *
     * @return whether requests on this endpoint should execute in a single thread.
     *         This property is only used when the endpoint is of type 'receiver'.
     */
    //boolean synchronous() default false;

    MEP mep() default MEP.InOnly;

    /**
     * The connector reference that will be used to create this endpoint. It is important that
     * the endpoint protocol and the connector correlate. For example, if your endpoint is a JMS
     * queue, your connector must be a JMS connector.
     * Many transports such as HTTP do not need a connector to be present, since Mule can create
     * a default one as needed.
     * <p/>
     * The connector reference can be a reference to a connector in the local registry or a reference
     * to an object in Galaxy.
     * <p/>
     * TODO: describe how connectors are created
     *
     * @return the connector name associated with the endpoint
     */
    String connector() default "";

    /**
     * Decides the encoding to be used for events received by this endpoint. Note that the encoding is only used where any
     * transformations are made that require encoding.
     *
     * @return the encoding set on the endpoint or null if no coding has been
     *         specified. When the encoding is not specified, the Mule default will be used from
     *         {@link #MuleContext.getConfiguration()}.
     */
    String encoding() default "";

    /**
     * A comma-separated list of key/value pairs, e.g,
     * <code>"apple=green, banana=yellow"</code>
     * Property placeholders can be used in these values:
     * <code>"apple=${apple.color}, banana=yellow"</code>
     *
     * @return A comma-separated list of key/value pairs or an empty string if no
     *         properties are set.
     */
    String properties() default "";

    /**
     * Transformers are responsible for transforming data when it is received
     * by the annotated service.
     * Transformers should be listed as a comma-separated list of registered transformers.
     * Property placeholders can be used to load transforms based on external values.
     *
     * @return the transformers to use when receiving data or empty string if not defined.
     */
    String transformers() default "";

    /**
     * Used when doing a request/response message style.
     * Transformers are responsible for transforming data when the event is being returned back to the caller.
     * Transformers should be listed as a comma-separated list of registered transformers.
     * Property placeholders can be used to load transforms based on external values.
     *
     * @return the transformers to use when receiving data or an empty string if not defined.
     */
    String responseTransformers() default "";

    /**
     * An expression filter used to filter out unwanted messages. The filter syntax uses familiar Mule expression
     * syntax:
     * <code>
     * filter = "wildcard:*.txt"
     * </code>
     * or
     * <code>
     * filter = "xpath:count(Batch/Trade/GUID) > 0"
     * </code>
     * <p/>
     * Filter expressions must result in a boolean or null to mean false.
     *
     * @return the filter string or empty string if not defined
     */
    String filter() default "";

    /**
     * An optional name for this endpoint. This is only used by Mule to identify the endpoint when logging messages,
     * firing notifications, and for JMX management.
     *
     * @return the name associated with this endpoint
     */
    String id() default "";
}
