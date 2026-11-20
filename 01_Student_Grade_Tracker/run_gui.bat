@echo off
title CodeAlpha - Student Grade Tracker (GUI)
echo Compiling and Launching Student Grade Tracker GUI...
javac StudentGradeTracker.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)
start javaw StudentGradeTracker
