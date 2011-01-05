package org.ibeans.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Will invoke a method on an object using the method parameters as arguments.  This is used by Web Service generated clients
 * to invoke a client method and make a web service call.
 *
 * The advantage of using this method of invoking a web service over just using the generated Java client is that the ibean can beused
 * to from JavaScript directly and be used to integrate with other frameworks more easily.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Invoke
{
    /**
     * The object ref name to invoke the method on. This object must be bound to a property in the invocation context.  Typically a
     * {@link org.ibeans.api.ParamFactory} will be used on a static variable to create the object and add it to the invocation context
     * as a property i.e.
     * <code>
     * &amp;#064;PropertyParam("binding")
     * public static final SoapBindingParamFactory BINDING = new SoapBindingParamFactory();
     * </code>
     *
     * Here the value of 'object' would be 'binding'.
     *
     * @return the object ref to use to find the object
     */
    public String object();

    /**
     * The method name to call on the 'object' reference.  The parameter for the annotated method must match the parameters on the method
     * being called.
     * @return the method name to call
     */
    public String method();


}