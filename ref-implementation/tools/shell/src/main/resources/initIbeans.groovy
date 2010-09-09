import org.codehaus.groovy.tools.shell.Shell
import org.mule.config.builders.DefaultsConfigurationBuilder
import org.mule.ibeans.IBeansContext
import org.mule.ibeans.internal.config.IBeansMuleContextBuilder
import org.mule.ibeans.internal.config.IBeansMuleContextFactory
import org.mule.module.ibeans.config.IBeanHolderConfigurationBuilder

//Usful to have avaialable in the shell
Shell shell = (Shell) binding.getVariable("shell")
assert shell != null

//No support for Guice right now
muleContext = new IBeansMuleContextFactory().createMuleContext([new DefaultsConfigurationBuilder(), new IBeanHolderConfigurationBuilder(shell.interp.classLoader)], new IBeansMuleContextBuilder())
ibeans = muleContext.getRegistry().lookupObject(IBeansContext.class)
