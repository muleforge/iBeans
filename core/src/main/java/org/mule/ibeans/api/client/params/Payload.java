package org.mule.ibeans.api.client.params;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This parameter annotation can be used with the {@link org.mule.ibeans.api.client.Call} annotation and specifies that a parameter 
 * passed into a method will be set as the payload of the outgoing message. Unlike the {@link PayloadParam} iBean, which is usually 
 * used with HTTP POST calls, the Payload iBean sets the object to the outgoing message as-is.  
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Payload
{

}