package org.mule.ibeans.api.client;

import org.mule.config.annotations.endpoints.Channel;
import org.mule.config.annotations.endpoints.ChannelType;
import org.mule.config.annotations.endpoints.SupportedMEPs;
import org.mule.impl.endpoint.MEP;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field annotation used to tell Mule iBeans that it should inject a client iBean as the field value. The field must be a client
 * iBean interface (that is, it must have at least one method with a {@link org.mule.ibeans.api.client.Call} annotation). At runtime, iBeans
 * will create a proxy for the client iBean and make it available to the user.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Channel(identifer = "integrationBean", type = ChannelType.Binding)
@SupportedMEPs({MEP.OutOnly, MEP.OutIn})
public @interface IntegrationBean
{

}