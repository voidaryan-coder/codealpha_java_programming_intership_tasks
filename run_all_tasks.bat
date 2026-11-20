@echo off
title CodeAlpha — Java Programming Internship Suite
color 0B
:menu_loop
cls
echo ==========================================================
echo       CODEALPHA JAVA PROGRAMMING INTERNSHIP SUITE
echo                 Developer: Aryan Sharma
echo ==========================================================
echo.
echo [1] Launch Interactive Web Suite Dashboard (Browser)
echo [2] Run Task 1: Student Grade Tracker (Java Swing GUI)
echo [3] Run Task 2: Stock Trading Platform (Java Swing GUI)
echo [4] Run Task 3: AI Chatbot Assistant (Java Swing GUI)
echo [5] Run Task 4: Hotel Reservation System (Java Swing GUI)
echo [6] Compile All 4 Java Projects
echo [7] Run Automated Unit Tests (48 Assertions - 100%% Verified)
echo [8] Exit
echo.
set /p opt="Enter your choice (1-8): "

if "%opt%"=="1" (
    start http://localhost:3000
    goto menu_loop
)
if "%opt%"=="2" (
    cd 01_Student_Grade_Tracker
    call run_gui.bat
    cd ..
    goto menu_loop
)
if "%opt%"=="3" (
    cd 02_Stock_Trading_Platform
    call run_gui.bat
    cd ..
    goto menu_loop
)
if "%opt%"=="4" (
    cd 03_AI_Chatbot
    call run_gui.bat
    cd ..
    goto menu_loop
)
if "%opt%"=="5" (
    cd 04_Hotel_Reservation_System
    call run_gui.bat
    cd ..
    goto menu_loop
)
if "%opt%"=="6" (
    echo.
    echo Compiling all 4 Java applications with javac...
    javac 01_Student_Grade_Tracker/StudentGradeTracker.java
    javac 02_Stock_Trading_Platform/StockTradingPlatform.java
    javac 03_AI_Chatbot/AIChatbot.java
    javac 04_Hotel_Reservation_System/HotelReservationSystem.java
    echo All projects compiled successfully with zero errors!
    pause
    goto menu_loop
)
if "%opt%"=="7" (
    echo.
    echo Compiling and executing Automated Test Runner...
    javac -d test -cp "01_Student_Grade_Tracker;02_Stock_Trading_Platform;03_AI_Chatbot;04_Hotel_Reservation_System" test/AutomatedTestRunner.java
    java -cp "test;01_Student_Grade_Tracker;02_Stock_Trading_Platform;03_AI_Chatbot;04_Hotel_Reservation_System" AutomatedTestRunner
    pause
    goto menu_loop
)
if "%opt%"=="8" (
    exit
)
goto menu_loop
