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

IF "%IBEANS_HOME%"==""  (
    IF "%CATALINA_HOME%"==""  (
      echo CATALINA_HOME is not set in your environment, assuming this script is being run from CATALINA_HOME/mule-ibeans/bin.
      set CATALINA_HOME=..\..
    ) ELSE (
      set IBEANS_HOME=%CATALINA_HOME%\mule-ibeans
    )
)


IF "%CATALINA_HOME%"==""  (
  set CATALINA_HOME=%IBEANS_HOME%\..
)

echo CATALINA HOME IS: %CATALINA_HOME%

set SHELL_HOME=%IBEANS_HOME%\tools\shell
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
echo Could not find the shell.  Make sure you have your CATALINA_HOME property set.

:end
ENDLOCAL
