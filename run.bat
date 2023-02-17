@echo off
:: Game Runner
cd starter-pack/runner-publish/
start "" dotnet GameRunner.dll

:: Game Engine
cd ../engine-publish/
timeout /t 1
start "" dotnet Engine.dll

:: Game Logger
cd ../logger-publish/
timeout /t 1
start "" dotnet Logger.dll

:: Bots
timeout /t 3
cd ../reference-bot-publish/
start "" dotnet ReferenceBot.dll
timeout /t 3
start "" dotnet ReferenceBot.dll
timeout /t 3
cd ../../target/ 
start "" java -jar JavaBot.jar
timeout /t 3


pause