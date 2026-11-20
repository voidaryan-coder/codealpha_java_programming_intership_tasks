@echo off
title CodeAlpha - Stock Trading Platform (GUI)
echo Compiling and Launching Stock Trading Platform GUI...
javac StockTradingPlatform.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)
start javaw StockTradingPlatform
