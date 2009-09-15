package org.mule.config.annotations.expressions;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;

/**
 * This evaluator allows for predefined functions
 * to be called and returns a result. The functions it supports are:
 * <ul>
 * <li>now - Returns a {@link java.sql.Timestamp} with the current time.</li>
 * <li>date - Returns a {@link java.util.Date} with the current time.</li>
 * <li>dateStamp - Returns a {@link java.lang.String} that contains the current date formatted according to {@link #DEFAULT_DATE_FORMAT}.</li>
 * <li>dateStamp(dd-MM-yyyy) - Returns a {@link java.lang.String} that contains the current date formatted according to the format passed into the function.</li>
 * <li>uuid - Returns a globally unique identifier</li>
 * <li>hostname - Returns the hostname of the machine Mule is running on</li>
 * <li>ip - Returns the IP address of the machine Mule is running on</li>
 * <li>count - Returns a local count that will increment for each call. If the server is restarted, the counter will return to zero.</li>
 * <li>payloadClass - Returns a fuly qualified class name of the payload as a string.</li>
 * <li>shortPayloadClass - Returns just the class name of the payload as a string.</li>
 * </ul>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Evaluator("function")
public @interface Function
{
    public abstract String value();

    public abstract boolean required() default true;
}