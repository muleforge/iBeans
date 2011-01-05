package org.ibeans.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker annotation for use with iBeans client interfaces that tells the client to store the parameters passed into the
 * method and make those parameters available to subsequent calls on the iBeans client. The method with
 * this annotation can mark its parameters using {@link org.mule.ibeans.api.client.params.UriParam},
 * {@link org.mule.ibeans.api.client.params.HeaderParam}, {@link org.mule.ibeans.api.client.params.PayloadParam}, or 
 * {@link org.mule.ibeans.api.client.params.PropertyParam}.
 * <p/>
 * A state method should have a void return type since nothing will be returned, and it should not have an exception signature
 * since no exception other than an iBeans runtime exception will be thrown. An iBeans runtime exception will only be thrown
 * if there is an error in the definition of the method parameter annotations.
 *
 * @see org.mule.ibeans.api.client.Call
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface State
{
}
