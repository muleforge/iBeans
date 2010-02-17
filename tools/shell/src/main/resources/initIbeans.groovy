import org.codehaus.groovy.tools.shell.Shell
import org.mule.config.builders.DefaultsConfigurationBuilder
import org.mule.ibeans.IBeansContext
import org.mule.ibeans.config.IBeanHolderConfigurationBuilder
import org.mule.ibeans.internal.config.IBeansMuleContextBuilder
import org.mule.ibeans.internal.config.IBeansMuleContextFactory

//Usful to have avaialable in the shell
Shell shell = (Shell) binding.getVariable("shell")
assert shell != null

//No support for Guice right now
muleContext = new IBeansMuleContextFactory().createContext([new DefaultsConfigurationBuilder(), new IBeanHolderConfigurationBuilder(shell.interp.classLoader)], new IBeansMuleContextBuilder())
ibeans = muleContext.getRegistry().lookupObject(IBeansContext.class)
