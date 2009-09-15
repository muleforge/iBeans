package org.mule.ibeans.api.client.authentication;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker annotation that tells iBeans that the method called is used for setting security credentials
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthenticationMethod
{
    /**
     * A descriptive name for the type of authentication being implmented, either 'HTTP Basic' or 'OAuth'
     * @return A descriptive name for the type of authentication being implmented, either 'HTTP Basic' or 'OAuth'
     */
    public String value();
}
