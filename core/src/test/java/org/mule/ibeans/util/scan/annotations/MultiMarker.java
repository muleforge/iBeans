package org.mule.ibeans.util.scan.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MultiMarker
{
    String value();

    String param1();

    String param2();
}