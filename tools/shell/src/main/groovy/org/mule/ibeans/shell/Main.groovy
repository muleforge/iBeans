package org.mule.ibeans.shell

import org.codehaus.groovy.tools.shell.Command
import org.codehaus.groovy.tools.shell.IBeansGroovysh
import org.codehaus.groovy.tools.shell.IO
import org.codehaus.groovy.tools.shell.IO.Verbosity
import org.codehaus.groovy.tools.shell.Main
import org.codehaus.groovy.tools.shell.util.HelpFormatter
import org.codehaus.groovy.tools.shell.util.Logger
import org.codehaus.groovy.tools.shell.util.MessageSource
import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager
import org.mule.ibeans.internal.config.IBeansInfo
import org.mule.ibeans.shell.commands.CreateIBeanCommand
import org.mule.ibeans.shell.commands.HelpCommand
import org.mule.ibeans.shell.commands.ListIBeanCommand
import org.mule.ibeans.shell.commands.RegisterObjectCommand

/**
 * Main CLI entry-point for <tt>ibeanssh</tt>.
 *
 */
class Main extends org.codehaus.groovy.tools.shell.Main
{
  private static final MessageSource messages = new MessageSource(Main.class)

  static void main(final String[] args)
  {
    IO io = new IO()
    Logger.io = io

    def cli = new CliBuilder(usage: 'ibeanssh [options] [...]', formatter: new HelpFormatter(), writer: io.out)

    cli.h(longOpt: 'help', messages['cli.option.help.description'])
    cli.V(longOpt: 'version', messages['cli.option.version.description'])
    cli.v(longOpt: 'verbose', messages['cli.option.verbose.description'])
    cli.q(longOpt: 'quiet', messages['cli.option.quiet.description'])
    cli.d(longOpt: 'debug', messages['cli.option.debug.description'])
    cli.C(longOpt: 'color', args: 1, argName: 'FLAG', optionalArg: true, messages['cli.option.color.description'])
    cli.D(longOpt: 'define', args: 1, argName: 'NAME=VALUE', messages['cli.option.define.description'])
    cli.T(longOpt: 'terminal', args: 1, argName: 'TYPE', messages['cli.option.terminal.description'])

    def options = cli.parse(args)

    if (options.h)
    {
      cli.usage()
      System.exit(0)
    }

    if (options.V)
    {
      io.out.println(messages.format('cli.info.version', IBeansInfo.getProductName(), IBeansInfo.getProductVersion()))
      System.exit(0)
    }

    if (options.hasOption('T'))
    {
      def type = options.getOptionValue('T')
      setTerminalType(type)
    }

    if (options.hasOption('D'))
    {
      def values = options.getOptionValues('D')

      values.each {
        setSystemProperty(it as String)
      }
    }

    if (options.v)
    {
      io.verbosity = IO.Verbosity.VERBOSE
    }

    if (options.d)
    {
      io.verbosity = IO.Verbosity.DEBUG
    }

    if (options.q)
    {
      io.verbosity = IO.Verbosity.QUIET
    }

    if (options.hasOption('C'))
    {
      def value = options.getOptionValue('C')
      setColor(value)
    }

    def code

    // Add a hook to display some status when shutting down...
    addShutdownHook {
      //
      // FIXME: We need to configure JLine to catch CTRL-C for us... if that is possible
      //

      if (code == null)
      {
        // Give the user a warning when the JVM shutdown abnormally, normal shutdown
        // will set an exit code through the proper channels

        io.err.println()
        io.err.println('@|red WARNING:| Abnormal JVM shutdown detected')
      }

      io.flush()
    }

    // Boot up the shell... :-)
    Binding binding = new Binding();
    IBeansGroovysh shell = new IBeansGroovysh(binding, io)
    //Temporary var
    binding.setVariable("shell", shell)


    SecurityManager psm = System.getSecurityManager()
    System.setSecurityManager(new NoExitSecurityManager())

    Verbosity verbosity = io.getVerbosity()
    //Silence this output
    io.setVerbosity(Verbosity.QUIET)
    shell.execute("load initIbeans.groovy")
    io.setVerbosity(verbosity)

    //Remove shell
    binding.getVariables().remove("shell")

    //Add our custom commands

    //Remove the existing Help command and replace so that we can offer help for ibeans in the same way
    Command command = null
    for (c in shell.getRegistry().getCommands())
    {
      if (c.name.equals("help"))
      {
        command = c
        break
      }
    }
    shell.getRegistry().remove(command)
    //Our custom help command
    shell.getRegistry().getCommands().add(new CreateIBeanCommand(shell))
    shell.getRegistry().getCommands().add(new ListIBeanCommand(shell))
    shell.getRegistry().getCommands().add(new RegisterObjectCommand(shell))
    shell.getRegistry().getCommands().add(new HelpCommand(shell))

    try
    {
      code = shell.run(options.arguments() as String[])
    }
    finally
    {
      System.setSecurityManager(psm)
    }

    // Force the JVM to exit at this point, since shell could have created threads or
    // popped up Swing components that will cause the JVM to linger after we have been
    // asked to shutdown

    System.exit(code)
  }
}