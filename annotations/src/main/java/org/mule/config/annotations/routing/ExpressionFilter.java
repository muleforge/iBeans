package org.mule.config.annotations.routing;

import org.mule.config.annotations.routing.Router;
import org.mule.config.annotations.routing.RouterType;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;

/**
 * TODO
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Router(type = RouterType.Inbound)
public @interface ExpressionFilter
{
    /**
     * The Mule expression to filter on
     * @return
     */
    public String value();
}
