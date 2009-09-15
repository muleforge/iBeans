package org.mule.config.annotations.expressions;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;

/**
 * TODO
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Evaluator("custom")
public @interface CustomEvaluator
{
    public abstract String value();

    public abstract String evaluator();

    public abstract boolean required() default true;
}