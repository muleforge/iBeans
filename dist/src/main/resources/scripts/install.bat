@REM ##############################################################################
@REM ##                                                                          ##
@REM ##  iBeans install script for Windows                                     ##
@REM ##                                                                          ##
@REM ##############################################################################

@REM ##
@REM ## $Revision: $
@REM ## $Id: $
@REM ##

@echo off
SETLOCAL

IF "%CATALINA_HOME%"==""  (
  echo CATALINA_HOME is not set in your environment, assuming this script is being run from the CATALINA/bin directory.
  set CATALINA_HOME=..\..
)


java -Dcatalina.home=%CATALINA_HOME% -jar %CATALINA_HOME%\mule-ibeans\tools\updater-1.0.jar
echo Install complete.  Start Tomcat/Tcat and go to http://localhost:8080/ibeans
