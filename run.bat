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
cd ../../subsetRPL10/target/ 
start "" java -jar JavaBot.jar
timeout /t 3
cd ../../avoidBot/target/
start "" java -jar JavaBot.jar
timeout /t 3
cd ../../hungryBot/target/
start "" java -jar JavaBot.jar
timeout /t 3

pause