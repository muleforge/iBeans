/*
 * Run an archetype and compile it using maven
 *
 * $Id: RunArchetype.groovy 12420 2008-07-29 18:58:27Z tcarlson $
 */

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.SystemUtils

/*
 * Make sure that the archetype can do its job, i.e. remove any leftovers from
 * the last invocation of the archetype
 */
def buildDir = new File(project.build.directory)

if (project.properties.outputDir == null)
{
  fail("Specify a property 'outputDir' in the config section of the groovy-maven-plugin that invokes this script")
}
def existingProjectDir = new File(project.build.directory, project.properties.outputDir)

if (existingProjectDir.exists())
{
  FileUtils.forceDelete(existingProjectDir)
}

// make sure that the output dir is created before the actual Maven run that follows now
existingProjectDir.mkdirs()

/*
 * run Maven archetype
 */
def cmdline = " archetype:generate -B -DarchetypeGroupId=org.mule.ibeans"

if (project.properties.archetype == null)
{
  fail("Specify the archetype to be invoked via a property named 'archetype' in the config section of the groovy-maven-plugin that invokes this script")
}

if (project.properties.archetypeParams != null)
{
  cmdline += project.properties.archetypeParams
}
cmdline += " -DarchetypeArtifactId=" + project.properties.archetype
cmdline += " -DarchetypeVersion=" + project.properties.archetypeVersion
cmdline += " -DgroupId=org.mule.ibeans"
cmdline += " -Dversion=" + project.version
cmdline += " -Dpackage=org.mule.ibeans"
cmdline += " -DartifactId=" + project.properties.outputDir
cmdline += " -Dinteractive=false"
cmdline += " -o" //run offline to speed things up a bit
cmdline += " -X" //debug
runMaven(cmdline, buildDir)

// now that the source is generated, compile it using Maven
cmdline = "test clean"
runMaven(cmdline, existingProjectDir)

def runMaven(String commandline, File directory)
{
  //TODO build server not picking mvn up in the path
  // def maven = "/opt/maven/current/bin/mvn"
  def maven = "mvn"
  if (SystemUtils.IS_OS_WINDOWS)
  {
    maven = "mvn.bat"
  }
  commandline = maven + " " + commandline

  log.info("***** commandline: '" + commandline + "'")

  // null means inherit parent's env ...
  def process = commandline.execute(null, directory)

  // consume all output of the forked process. Otherwise it may lock up
  process.in.eachLine { log.info(it) }
  process.err.eachLine { log.error(it) }

  process.waitFor()
  def exitCode = process.exitValue()
  if (exitCode != 0)
  {
    fail("command did not execute properly: " + exitCode)
  }
}


