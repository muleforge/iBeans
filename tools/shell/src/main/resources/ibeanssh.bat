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
  echo CATALINA_HOME is not set, assuming we're running in the iBeans shell directory.
  set CATALINA_HOME=..\..\..
)

echo CATALINA HOME IS: %CATALINA_HOME%

set SHELL_MODULE=%CATALINA_HOME%\mule-ibeans\lib\modules\deployed\shell

IF EXIST %SHELL_MODULE% (
    echo iBeans Shell module installed, starting...
) ELSE (
    goto installshell
)

set CP=.
for %%i in (%SHELL_MODULE%\*.jar) do call cp.bat %%i

@REM need to add the servlet jar separately since iBeans does not ship with it when running in Tomcat
set CP=%CP%;%CATALINA_HOME%\lib\servlet-api.jar

set SHELL_CONF=%CATALINA_HOME%\mule-ibeans\tools\shell\launcher.conf
java -Dcatalina.home=%CATALINA_HOME% -cp "%CP%" org.codehaus.groovy.tools.GroovyStarter --main org.mule.ibeans.shell.Main --conf %SHELL_CONF% "%*"

goto end

:installshell
echo You need to have the iBeans Shell module for iBeans installed. Go to http://localhost:8080/ibeans (or where ever you have iBeans running) and install the iBeans Shell module.

:end
ENDLOCAL