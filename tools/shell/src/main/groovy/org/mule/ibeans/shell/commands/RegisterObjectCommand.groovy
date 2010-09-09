package org.mule.ibeans.shell.commands

import org.codehaus.groovy.tools.shell.CommandSupport
import org.codehaus.groovy.tools.shell.Shell
import org.mule.api.MuleContext

/**
 * TODO
 */

public class RegisterObjectCommand extends CommandSupport
{
  RegisterObjectCommand(final Shell shell)
  {
    super(shell, 'reg', '\\R')
  }

  Object execute(final List args)
  {
    if(args.size() == 0)
      fail("At least one argument is required")
    Object object = args[0]
    String id = (String) (args[1] != null ? args[1] : "_bean#" + object.hashCode())
    MuleContext mc = (MuleContext) getBinding().getVariable("muleContext")
    mc.getRegistry().registerObject(id, object)
    if (!io.quiet) io.out.println("Registered as " + id)
    return ""
  }

}