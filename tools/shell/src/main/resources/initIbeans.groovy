import org.codehaus.groovy.tools.shell.Shell
import org.mule.config.builders.DefaultsConfigurationBuilder
import org.mule.context.DefaultMuleContextBuilder
import org.mule.context.DefaultMuleContextFactory
import org.mule.ibeans.IBeansContext
import org.mule.ibeans.config.IBeanHolderConfigurationBuilder

//Usful to have avaialable in the shell
Shell shell = (Shell) binding.getVariable("shell")
assert shell != null

//No support for Guice right now
muleContext = new DefaultMuleContextFactory().createMuleContext([new DefaultsConfigurationBuilder(), new IBeanHolderConfigurationBuilder(shell.interp.classLoader)], new DefaultMuleContextBuilder())
ibeans = new IBeansContext(muleContext)
