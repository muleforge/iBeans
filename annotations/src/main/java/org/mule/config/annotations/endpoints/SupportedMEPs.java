package org.mule.config.annotations.endpoints;

import org.mule.impl.endpoint.MEP;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE })
public @interface SupportedMEPs
{
    public MEP[] value();
}
