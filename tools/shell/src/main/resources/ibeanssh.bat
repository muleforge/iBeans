@REM ##############################################################################
@REM ##                                                                          ##
@REM ##  iBeans Shell script for Windows                                         ##
@REM ##                                                                          ##
@REM ##############################################################################

@REM ##
@REM ## $Revision: $
@REM ## $Id: $
@REM ##

@echo off
SETLOCAL

IF "%CATALINA_HOME%"==""  (
  echo CATALINA_HOME is not set in your environment, assuming this script is being run from the iBeans shell directory.
  set CATALINA_HOME=..\..
)

echo CATALINA HOME IS: %CATALINA_HOME%

set SHELL_HOME=%CATALINA_HOME%\mule-ibeans\tools\shell
set SHELL_MODULE=%SHELL_HOME%\ibeans-shell-full.jar

IF EXIST %SHELL_MODULE% (
    echo iBeans Shell module installed, starting...
) ELSE (
    goto installshell
)

@REM need to add the servlet jar separately since iBeans does not ship with it when running in Tomcat
set CP=%SHELL_MODULE%;%CATALINA_HOME%\lib\servlet-api.jar

set SHELL_CONF=%SHELL_HOME%\launcher.conf
java -Dcatalina.home=%CATALINA_HOME% -Dibeans.shell.home=%SHELL_HOME% -cp "%CP%" org.codehaus.groovy.tools.GroovyStarter --main org.mule.ibeans.shell.Main --conf %SHELL_CONF% "%*"

goto end

:installshell
echo You need to have the iBeans Shell module for iBeans installed. Go to http://localhost:8080/ibeans (or where ever you have iBeans running) and install the iBeans Shell module.

:end
ENDLOCAL
