package org.mule.ibeans.api.application;

import org.mule.impl.annotations.ObjectScope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that defines a Mule service. Objects registered with this annotation will
 * be configured as a service in Mule
 * <p/>
 * NOTICE: This annotaiton is experimental, and may change or be removed.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BeanConfig
{
    /**
     * The name of this service
     *
     * @return the name of this service. If not set the class name will  be used with a '.service' prefix
     */
    String name() default "";

    /**
     * Determines if this service will be a singleton or prototype.
     * Note this refers to the actual service object instance i.e. your annotated object.
     * <p/>
     * By default singleton is used so that any fields in your objects will retain thier values, making stateful
     * services possible by default. Note that any operations on field variables will need to be thread-safe.
     *
     * @return true if the service is a singleton
     */
    ObjectScope scope() default ObjectScope.SINGLETON;

    /**
     * If the channel that triggered a call on this object is not expecting a response, how many threads can process the inbound
     * emessages simultaneously
     *
     * @return the number of concurrent threads to be used for async processing
     */
    int maxAsyncThreads() default 8;
}
