package org.mule.ibeans.shell.commands

import org.codehaus.groovy.tools.shell.IBeansGroovysh
import org.codehaus.groovy.tools.shell.IO
import org.codehaus.groovy.tools.shell.IO.Verbosity

/**
 * Support for testing  {@link org.codehaus.groovy.tools.shell.Command}  instances.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
abstract class CommandTestSupport
extends GroovyTestCase
{
  IBeansGroovysh shell

  Object lastResult

  void setUp()
  {
    super.setUp()

    Binding binding = new Binding()
    IO io = new IO()
    io.setVerbosity(Verbosity.QUIET)
    shell = new IBeansGroovysh(binding, io)

    binding.setVariable("shell", shell)

    //InitIBeans container
    URL url = getClass().getClassLoader().getResource("initIbeans.groovy")
    assertNotNull("could not locate the initIbeans.groovy on the classpath", url)
    shell.execute("load " + url.toExternalForm())

    shell.errorHook = {Throwable cause ->
      throw cause
    }

    shell.resultHook = {result ->
      lastResult = result
    }
  }
}
