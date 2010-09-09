package org.mule.ibeans.shell.commands

import org.codehaus.groovy.tools.shell.CommandException

class HelpCommandTest extends CommandTestSupport
{
//  void testList()
//  {
//    shell << 'help'
//  }

  void testCommandHelp()
  {
    shell << 'help exit'
  }

  void testIBeanHelp()
  {
    shell << 'help dummy'
  }

  void testCommandHelpInvalidCommand()
  {
    try
    {
      shell << 'help no-such-command'
      fail("'no-such-command' is not a valid command for help");
    }
    catch (CommandException e)
    {
      // expected
    }
  }

}