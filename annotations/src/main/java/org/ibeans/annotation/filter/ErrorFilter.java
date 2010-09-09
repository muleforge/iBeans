package org.ibeans.annotation.filter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marker interface that tells Mule iBeans that an annotation is an Error Filter and should be treated as such.
 * <p/>
 * unfortunately, annotations do not support inheritance so every Expression-based ErrorFilter the following rules
 * have to be observed -
 * 1. The filter must have an 'expr' String param that is used to set the expression
 * 2. The filter must have an 'mimeType' String param that is used to set the mimeType supported by the filter. This
 * should have a default value set.
 * 3. The filter must have an 'errorCode' String param that sets the expression used to extract the error code if any.
 * This should be an optional param
 * 4. There should either be a param or final field called 'eval' that set the expression eval string i.e. 'groovy', 'xpath'
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ErrorFilter
{

}
