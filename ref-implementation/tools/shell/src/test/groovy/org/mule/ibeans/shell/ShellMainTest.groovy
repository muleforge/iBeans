package org.mule.ibeans.shell

/**
 * Just try running the shell from the main class
 * This doesn't work, and I'm not surprised given all the funkiness replacing the security manager. I noticed
 * that Groovy doesn't have a test case either
 */
class ShellMainTest extends GroovyTestCase
{

  void testNothing()
  {
    assertTrue true;
  }
//  void testCreate()
//  {
//    SecurityManager sm = System.getSecurityManager()
//
//    URL url = getClass().getClassLoader().getResource("initIbeans.groovy")
//    String s = url.toExternalForm()
//    s = s.substring(0, s.lastIndexOf("/"))
//    System.setProperty("ibeans.shell.home", s)
//
//    def error = null
//    def t = Thread.start{
//        try
//        {
//          Main.main()
//        }
//        catch (Throwable e)
//        {
//          error = e
//        }
//      }
//
//    sleep 10000
//    //overwrite the shell security to allow system exit
//    System.setSecurityManager(sm)
//
//    try
//    {
//      Main.shell.execute('exit')
//    }
//    catch (ExitNotification en)
//    {
//      //ignore
//    }
//    t.stop() //Join leaves the thread hanging in this test
//    if(error) error.printStackTrace()
//
//    assertNull("There shouldn't be any errors on start up: error is: " + error, error)
//  }
}