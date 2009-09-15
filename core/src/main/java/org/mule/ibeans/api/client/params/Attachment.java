package org.mule.ibeans.api.client.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies one or more attachments to be added to an outgoing message. Transports such as HTTP and SMTP allow attachments
 * to be sent with the message payload. There are several ways to specify attachments, which is controlled by the the type of the parameter
 * annotated. Valid return types are -
 * <ul>
 * <li>{@link java.io.File} - A file to add as an attachment</li>
 * <li>{@link java.io.File[]} - A file array where each file is added as an attachment</li>
 * <li>{@link java.net.URL} - A URL to a file to add as an attachment</li>
 * <li>{@link java.net.URL[]} - A URL array where each URL is a file location and will be added as an attachment</li>
 * <li>{@link javax.activation.DataSource} - An attachment data source. This allows users to attach {@link java.io.InputStream} data and associate it with a mime type and a name</li>
 * <li>{@link javax.activation.DataSource[]} - Same as above where each element of the array will be added as an attachment</li>
 * </ul>
 * <p/>
 * Note that the mime type for attachments that are specified as {@link java.net.URL} or {@link java.io.File} will be added
 * based on the file extension. If you want to override the default, you should use {@link javax.activation.DataSource} objects instead.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Attachment
{

}
