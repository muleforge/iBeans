package org.ibeans.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p/>
 * Defines the logic needed to create a URL. Since the URL format will be specific to a service, it makes sense to provide a method 
 * in the client iBean to help users. In this example, the user just needs to loop through all the photo nodes in the search document 
 * and call the following method on each:
 * <code>
 * &amp;#064;Template("http://static.flickr.com/#[xpath2:@server]/#[xpath2:@id]_#[xpath2:@secret]_{image_size}.{image_type}")
    public URL getPhotoURL(@Body Node photoNode) throws CallException;
 * </code>
 * <p/>
 * In this example, a {@link org.w3c.dom.Node} is passed in, and then three Mule XPath expressions are used to query the node and construct a URL
 * that will be returned from the method. Any of the Mule expression evaluators can be used such as Groovy or OGNL, allowing
 * users to implement simple logic in their client iBeans.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Template
{
    /**
     * An expression string that will be evaluated against the method argument passed in.
     * Note that only one parameter can be passed in.
     *
     * @return An expression string
     */
    public abstract String value();


}