package org.mule.ibeans.shell.commands

import org.codehaus.groovy.tools.shell.CommandSupport
import org.codehaus.groovy.tools.shell.Shell
import org.mule.api.MuleContext
import org.mule.module.ibeans.config.IBeanHolder

/**
 * TODO
 */

public class ListIBeanCommand extends CommandSupport
{
  ListIBeanCommand(final Shell shell)
  {
    super(shell, 'list', '\\ls')
  }

  Object execute(final List args)
  {
    assertNoArguments(args)
    MuleContext mc = (MuleContext) getBinding().getVariable("muleContext")
    Collection<IBeanHolder> col = mc.getRegistry().lookupObjects(IBeanHolder.class)
    Set<IBeanHolder> beans = new TreeSet<IBeanHolder>(col);
    println()
    io.out.println("Available iBeans: " + beans.size())
    beans.each {io.out.println("@|yellow " + it.id + "|@ : " + it.ibeanClass.name)}
    return ""
  }

}