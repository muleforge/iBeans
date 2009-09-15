package org.mule.ibeans.api.application.params;

import org.mule.config.annotations.expressions.Evaluator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used on methods that have a {@link org.mule.ibeans.api.application.Send} or {@link org.mule.ibeans.api.application.ReceiveAndReply} annotation, 
 * this parameter annotation passes in a reference to a {@link java.util.Map} that can be used to populate 
 * outbound headers that will be set with the outgoing message. For example, when sending an email message, 
 * you may want to set properties such as "from" or "subject" as a sender header.
 * <p/>
 * This annotation must only be defined on a parameter of type {@link java.util.Map}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Evaluator("sendHeaders")
public @interface SendHeaders
{
}