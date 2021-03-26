@echo off
cd /d %CD%
set tmpDir=%CD%\build\libs
explorer %tmpDir%
cmd /k gradlew.bat build
