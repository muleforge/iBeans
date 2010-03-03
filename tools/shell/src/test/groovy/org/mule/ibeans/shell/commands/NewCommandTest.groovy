package org.mule.ibeans.shell.commands

import org.codehaus.groovy.tools.shell.CommandException

/**
 * TODO
 */

class NewCommandTest extends CommandTestSupport
{
    void testCreateNew() {
        shell << 'new dummy'
    }

  void testCreateNewWithAlias() {
        shell << 'new dummy alias dum'
    }

  void testCreateNonExistent() {
    try {
                shell << 'new foo'
                fail("Foo ibean does not exist")
            }
            catch (CommandException expected) {}

    }

}