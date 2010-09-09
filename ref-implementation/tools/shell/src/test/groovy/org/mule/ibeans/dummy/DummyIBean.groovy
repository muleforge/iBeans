package org.mule.ibeans.dummy

import org.ibeans.annotation.Template
import org.ibeans.api.CallException

/**
 * A test iBean used by the create and help commands
 */
public interface DummyIBean {
  @Template("nothing")
  String doNothing() throws CallException
}