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
 * The SMTP transport can be used to connect to and send data to a SMTP mail server. The SMTPS Transport is similar
 * but uses secure connections over SSL/TLS.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Channel(identifer = "smtp", type = ChannelType.Outbound)
@SupportedMEPs(MEP.OutOnly)
public @interface SmtpOutbound
{
    /**
     * The username to use when connecting to the SMTP server. This can be blank if the SMTP server does not require
     * authentication.
     *
     * @return the user ID to authenticate the connector or empty if authentication is not required
     */
    public abstract String user() default "";

    /**
     * The password to use with the username
     *
     * @return The password to use with the username
     */
    public abstract String password() default "";

    /**
     * THe SMTP host name to connect to
     *
     * @return the SMTP host name, this property must be set
     */
    public abstract String host();

    /**
     * The connection port. The default for SMTP is port 25. This is the standard and should not be changed
     * unless your SMTP server is configured to use a different port.
     *
     * @return the server port
     */
    public abstract int port() default 25;


    /**
     * The From email address for the message. This must be a single, valid email address
     *
     * @return The from email address for the message
     */
    public abstract String from();

    /**
     * The recipient email addresses for the message. This can be a comma-separated list of valid email addresses
     *
     * @return One or more recipient email addresses
     */
    public abstract String to() default "";

    /**
     * The carbon-copy (CC) recipient email addresses for the message. This can be a comma-separated list of valid
     * email addresses. Carbon copy email addresses are visible to all recipients.
     *
     * @return One or more CC recipient email addresses
     */
    public abstract String cc() default "";

    /**
     * The blind carbon-copy (BCC) recipient email addresses for the message. This can be a comma-separated list of
     * valid email addresses. Blind carbon copy email addresses are NOT visible to each other or other recipients.
     *
     * @return One or more BCC recipient email addresses
     */
    public abstract String bcc() default "";

    /**
     * The subject for the email address
     *
     * @return The subject for the email address
     */
    public abstract String subject() default "[No Subject]";

    /**
     * The replyTo address that will be used when email replies are sent from this endpoint
     *
     * @return The replyTo address for email messages sent using this endpoint
     */
    public abstract String replyTo() default "";

    /**
     * An optional identifier for this endpoint. This is only used by Mule to identify the endpoint when logging messages,
     * firing notifications, and for JMX management.
     *
     * @return the name associated with this endpoint
     */
    String id() default "";

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
     * @return the MEP configured for this endpoint. The SMTP transport just supports the {@link MEP.OutOnly} MEP.
     */
    public abstract MEP mep() default MEP.OutOnly;

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

}