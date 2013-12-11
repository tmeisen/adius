@echo off
xjc -extension -b xjc-binding.xml ..\sdl-1.0.xsd -d ..\..\java\
xjc -extension -b xjc-binding.xml ..\spdl-1.0.xsd -d ..\..\java\
xjc -extension -b xjc-binding.xml ..\pdl-1.0.xsd -d ..\..\java\