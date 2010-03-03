package org.mule.ibeans.dummy

import org.mule.ibeans.api.client.CallException
import org.mule.ibeans.api.client.Template

/**
 * A test iBean used by the create and help commands
 */
public interface DummyIBean {
  @Template("nothing")
  String doNothing() throws CallException
}