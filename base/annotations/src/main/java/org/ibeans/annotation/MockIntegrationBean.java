package org.ibeans.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field annotation used to tell Mule iBeans that it should inject a client iBean as the field value. The bean injected will
 * be a Mock iBean that can be used for testing.  Assertions can be made on the bean by using a simple testing dsl called Mockito.
 * To mock method calls and return data, use the When() command i.e.
 * <code>
 * protected void doSetUp() throws Exception
 * {
 * google.init(DEVELOPER_KEY, Feed.class);
 * <p/>
 * when(google.search("Mule ESB")).thenReturn(answerWithData("mule-esb-search.atom", google));
 * }
 * </code>
 * will create a proxy for the client iBean and make it available to the user.
 *
 * @see org.mule.ibeans.test.MockIBean
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockIntegrationBean
{

}