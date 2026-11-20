# CodeAlpha — Java Programming Internship Projects

[![Java](https://img.shields.io/badge/Java-SE%2026-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Tests](https://img.shields.io/badge/Tests-48%2F48%20Passed%20(100%25)-brightgreen)](#-automated-qa-test-suite)
[![Status](https://img.shields.io/badge/CodeAlpha-Verified%20Submission-blue)](https://www.codealpha.tech/)
[![Live Demo](https://img.shields.io/badge/Live%20Demo-Cloudflare%20Tunnel-orange)](https://runtime-hoped-assigned-necessary.trycloudflare.com)

Comprehensive, production-ready implementation of all **4 Java Programming Internship Tasks** assigned by **CodeAlpha** ([www.codealpha.tech](https://www.codealpha.tech)).

Developed with **Java SE (JDK 26)**, Object-Oriented Programming (OOP) design patterns, multithreaded simulation, File I/O persistence, and interactive **Java Swing Desktop GUIs** alongside an interactive **Web Suite** inspired by ScamShield's clean, minimalist SaaS interface.

🌐 **Live Web Demo:** [https://runtime-hoped-assigned-necessary.trycloudflare.com](https://runtime-hoped-assigned-necessary.trycloudflare.com)  
📂 **GitHub Repository:** [https://github.com/aryan2027sharma/codealpha_tasks](https://github.com/aryan2027sharma/codealpha_tasks)

---

## ✦ Internship Tasks Overview (4 of 4 Completed)

| Task | Project Title | Key Technologies | Description & Capabilities |
|:---:|:---|:---|:---|
| **01** | **Student Grade Tracker** | `ArrayList`, Math Statistics, Java Swing, File I/O | Inputs and manages student records across subjects. Dynamically calculates class averages, top performers, lowest scores, letter grades (A+ to F), and GPA. Generates and exports comprehensive summary text reports. |
| **02** | **Stock Trading Platform** | OOP (`Stock`, `Holding`, `Transaction`, `Portfolio`), Timers, Volatility Engine | Real-time market data ticks with live price fluctuations. Buy and sell order execution with balance verification, portfolio valuation, unrealized P&L tracking, and transaction ledger persistence. |
| **04** | **Hotel Reservation System** | Room Categorization, Payment Simulation, Booking Ledger | Room management across Standard ($79), Deluxe ($149), and Suite ($269) categories. Interactive date booking, instant invoice/receipt generation, payment simulation gateway, and reservation cancellation. |

---

## ✦ Repository Architecture

```text
CodeAlpha_Tasks/
├── index.html                           # ScamShield-inspired interactive web dashboard & demo
├── run_all_tasks.bat                    # One-click Windows master launcher
├── README.md                            # Complete documentation & LinkedIn submission guide
│
├── 01_Student_Grade_Tracker/
│   ├── StudentGradeTracker.java         # Core source (Swing GUI + CLI fallback)
│   ├── StudentGradeTracker.class
│   └── run_gui.bat                      # Standalone launcher
│
├── 02_Stock_Trading_Platform/
│   ├── StockTradingPlatform.java        # Core source (Market simulation & portfolio)
│   ├── StockTradingPlatform.class
│   └── run_gui.bat                      # Standalone launcher
│
├── 04_Hotel_Reservation_System/
│   ├── HotelReservationSystem.java      # Core source (Room categories & payment gateway)
│   ├── HotelReservationSystem.class
│   └── run_gui.bat                      # Standalone launcher
│
├── test/
│   └── AutomatedTestRunner.java         # 48-assertion unit test suite (100% verified)
│
└── server.js                            # Local HTTP server with security headers
```

---

## ✦ How to Run the Projects

### Option 1: Master Launcher (Recommended)
Double-click **`run_all_tasks.bat`** in the root directory to open the interactive menu.

### Option 2: Run Individual Tasks in Terminal

#### Task 1: Student Grade Tracker
```bash
cd 01_Student_Grade_Tracker
javac StudentGradeTracker.java
java StudentGradeTracker          # Launches Desktop Swing GUI
java StudentGradeTracker --cli    # Launches Interactive CLI Mode
```

#### Task 2: Stock Trading Platform
```bash
cd 02_Stock_Trading_Platform
javac StockTradingPlatform.java
java StockTradingPlatform         # Launches Desktop Swing GUI
java StockTradingPlatform --cli   # Launches Interactive CLI Mode
```

#### Task 4: Hotel Reservation System
```bash
cd 04_Hotel_Reservation_System
javac HotelReservationSystem.java
java HotelReservationSystem       # Launches Desktop Swing GUI
java HotelReservationSystem --cli # Launches Interactive CLI Mode
```

---

## ✦ Automated QA Test Suite (48 Unit Assertions)

Run the comprehensive regression test suite:
```bash
# Using master launcher option [7] or directly:
javac -d test -cp "01_Student_Grade_Tracker;02_Stock_Trading_Platform;04_Hotel_Reservation_System" test/AutomatedTestRunner.java
java -cp "test;01_Student_Grade_Tracker;02_Stock_Trading_Platform;04_Hotel_Reservation_System" AutomatedTestRunner
```
* **Coverage:** 48 unit tests covering calculations, data structures, boundary limits, multithreaded simulation, NLP stemming, file persistence, and reservation lifecycle.
* **Pass Rate:** 100% (48/48 passed).

---

## ✦ Interactive Web Suite
Open **`index.html`** in any web browser to access the complete ScamShield-styled web interface, interactive live demo for all 4 tasks, and standalone syntax-highlighted Java code viewer.

---

## ✦ Developer Profile

* **Developer:** Aryan Sharma
* **Institution:** G.L. Bajaj Institute of Technology & Management, Greater Noida
* **Department:** Computer Science & Engineering (B.Tech 2025–2029)
* **Direct Email:** [aryan2027sharma@gmail.com](mailto:aryan2027sharma@gmail.com)
* **GitHub:** [https://github.com/aryan2027sharma](https://github.com/aryan2027sharma)

---

## ✦ CodeAlpha Submission Checklist

- [x] Complete at least 2 or 3 tasks (Completed **all 4 tasks**).
- [x] Upload source code to GitHub repository.
- [x] Clean OOP architecture with error handling and data persistence.
- [x] Interactive GUI and Console modes tested.
- [x] Submission form ready.
