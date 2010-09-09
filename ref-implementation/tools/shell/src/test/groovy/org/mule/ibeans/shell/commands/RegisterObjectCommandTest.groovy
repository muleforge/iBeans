package org.mule.ibeans.shell.commands

import org.codehaus.groovy.tools.shell.CommandException

class RegisterObjectCommandTest extends CommandTestSupport
{
  void testRegister()
  {
    shell << 'reg 3'
  }

  void testRegisterWithId()
  {
    shell << 'reg 3 three'
  }

  void testRegisterWithNoObject()
  {
    try
    {
      shell << 'reg'
    }
    catch (CommandException ce)
    {
      //expected
    }
  }
}