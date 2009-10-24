@REM ##############################################################################
@REM ##                                                                          ##
@REM ##  iBeans Uninstall script for Windows                                     ##
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
  set CATALINA_HOME=../..
)


java -Dcatalina.home=%CATALINA_HOME% -jar %CATALINA_HOME%\mule-ibeans\tools\updater-1.0.jar -u
echo Removing the iBeans directory..
rmdir /S /Q %CATALINA_HOME%/mule-ibeans
echo Uninstall complete.

