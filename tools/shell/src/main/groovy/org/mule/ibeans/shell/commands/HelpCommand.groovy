package org.mule.ibeans.shell.commands

import org.codehaus.groovy.tools.shell.Command
import org.codehaus.groovy.tools.shell.CommandRegistry
import org.codehaus.groovy.tools.shell.CommandSupport
import org.codehaus.groovy.tools.shell.Shell
import org.codehaus.groovy.tools.shell.util.SimpleCompletor
import org.mule.api.MuleContext
import org.mule.ibeans.internal.config.IBeansInfo
import org.mule.ibeans.config.IBeanHolder

/**
 * TODO
 */

/**
 * The 'help' command.
 *
 * @version $Id: HelpCommand.groovy 8493 2007-10-08 18:57:58Z user57 $
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class HelpCommand
extends CommandSupport
{
  HelpCommand(final Shell shell)
  {
    super(shell, 'help', '\\h')
    registry = shell.getRegistry()

    alias('?', '\\?')
  }

  protected List createCompletors()
  {
    return [
            new HelpCommandCompletor(registry),
            null
    ]
  }

  Object execute(final List args)
  {
    assert args != null

    if (args.size() > 1)
    {
      fail(messages.format('error.unexpected_args', args.join(' ')))
    }

    if (args.size() == 1)
    {
      help(args[0])
    }
    else
    {
      list()
    }
  }

  private void help(final String name)
  {
    assert name

    Command command = registry[name]
    String usage = null
    if (!command)
    {
      MuleContext mc = (MuleContext) getBinding().getVariable("muleContext")
      Collection<IBeanHolder> col = mc.getRegistry().lookupObjects(IBeanHolder.class)
      for (ib in col)
      {
        if (ib.id == name)
        {
          usage = ib.usage
          break
        }
      }

      if (!usage)
      {
//    io.err.println('@|red ERROR:| There is no iBean with id: ' + id + ' available in the container. USe the \'list\' command to see what you have.')
//    return ""
        fail("No such command: $name") // TODO: i18n
      }
    }
    else
    {
      usage = command.usage
    }

    io.out.println()
    io.out.println("usage: @|bold ${name}| $usage") // TODO: i18n
    io.out.println()
    if (command)
    {
      io.out.println(command.help)
      io.out.println()
    }
  }

  private void list()
  {
    // Figure out the max command name and shortcut length dynamically
    int maxName = 0
    int maxShortcut

    for (command in registry)
    {
      if (command.hidden)
      {
        continue
      }

      if (command.name.size() > maxName)
      {
        maxName = command.name.size()
      }

      if (command.shortcut.size() > maxShortcut)
      {
        maxShortcut = command.shortcut.size()
      }
    }

    io.out.println()
    io.out.println messages.format('more.info.0', IBeansInfo.getProductName(), IBeansInfo.getProductUrl())
    io.out.println messages.format('more.info.1', IBeansInfo.getProductUrl())
    io.out.println()
    io.out.println('Available commands:') // TODO: i18n

    for (command in registry)
    {
      if (command.hidden)
      {
        continue
      }

      def n = command.name.padRight(maxName, ' ')
      def s = command.shortcut.padRight(maxShortcut, ' ')

      //
      // TODO: Wrap description if needed
      //

      def d = command.description

      io.out.println("  @|bold ${n}|  (@|bold ${s}|) $d")
    }

    io.out.println()
    io.out.println('For help on a specific command type:') // TODO: i18n
    io.out.println('    help @|bold command| ')
    io.out.println()
  }
}

/**
 * Completor for the 'help' command.
 *
 * @version $Id: HelpCommand.groovy 8493 2007-10-08 18:57:58Z user57 $
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class HelpCommandCompletor
extends SimpleCompletor
{
  private final CommandRegistry registry

  HelpCommandCompletor(final CommandRegistry registry)
  {
    assert registry

    this.registry = registry
  }

  SortedSet getCandidates()
  {
    def set = new TreeSet()

    for (command in registry)
    {
      if (command.hidden)
      {
        continue
      }

      set << command.name
      set << command.shortcut
    }

    return set
  }
}