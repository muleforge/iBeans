package org.mule.ibeans.shell.commands

import org.codehaus.groovy.tools.shell.CommandSupport
import org.codehaus.groovy.tools.shell.Shell
import org.mule.api.MuleContext
import org.mule.ibeans.config.IBeanHolder

/**
 * A command that can be used for getting an instance of an installed ibean or searching the store and installing and ibean.
 */
public class CreateIBeanCommand extends CommandSupport
{
  CreateIBeanCommand(final Shell shell)
  {
    super(shell, 'new', '\\+')
  }

  Object execute(final List args)
  {
    assert args.size() > 0
    String id = (String) args[0]
    String alias = (String) (2 == args.size() ? args[1] : null)
    if (getBinding().getVariables().containsKey(id) && alias == null)
    {
      io.err.println('@|red ERROR:|@ There is already a bean registered with name: ' + id + '. Specify an alias to create a new instance.')
      return ""
    }
    MuleContext mc = (MuleContext) getBinding().getVariable("muleContext")
    IBeanHolder ibeanHolder = null
    String name
    if (id.indexOf('.') > -1)
    {
      clazz = getClassLoader().loadClass(id)
      ibeanHolder = new IBeanHolder(clazz)
      name = ibeanHolder.getId()
    }
    else
    {
      name = id
      Collection<IBeanHolder> col = mc.getRegistry().lookupObjects(IBeanHolder.class)
      for (ibh in col)
      {
        if (ibh.getId().equals(id))
        {
          ibeanHolder = ibh
          break
        }
      }
      if (ibeanHolder == null)
      {
        fail('There is no iBean with id: ' + id + ' available in the container. Use the \'list\' command to see what you have running.');
      }
    }
    name = (alias != null ? alias : name)
    def bean = ibeanHolder.create(mc)
    getBinding().setVariable(name, bean)
    if (!io.quiet) io.out.println("Loaded iBean: " + id + " as " + name)
    return ""
  }
}
