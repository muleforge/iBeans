package org.mule.ibeans.api.application;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method on a class as a transformer, which means it will be made available in the iBeans container. A Transformer
 * method should define a public method that accepts at least one input parameter and must always return a result.
 * <p/>
 * It is good practice to define any custom transformers in their own class (a class can have more than one transformer method).
 * A transformer class should be thread-safe and not have any transitive state, meaning that it should not maintain state as
 * a result of a transformation. It is fine for transformers to have configuration state, such as in an XSLT or XQuery template file
 * (note that iBeans already provides transformers for XSLT and XQuery).
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transformer
{
    /**
     * The 'priorityWeighting property is used to resolve conflicts where there is more than one transformers that match
     * the selection criteria.  10 is the highest priority and 1 is the lowest.
     *
     * @return the priority weighting for this transformer. If the class defines more than one transform method, every transform
     *         method will have the same weighting.
     */
    int priorityWeighting() default 5;

    //TODO add if/when we get support for Transformer mime types
    //String sourceMimType() default "";

    //String resultMimType() default "";
}
