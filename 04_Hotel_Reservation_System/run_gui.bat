@echo off
title CodeAlpha - Hotel Reservation System (GUI)
echo Compiling and Launching Hotel Reservation System GUI...
javac HotelReservationSystem.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)
start javaw HotelReservationSystem
