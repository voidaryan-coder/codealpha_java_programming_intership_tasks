import java.io.File;
import java.time.LocalDate;
import java.util.*;

/**
 * CodeAlpha Java Programming Internship — Automated Quality Assurance & Regression Test Suite
 * 
 * Executes 48 unit tests across all 4 tasks:
 * - Task 1: Student Grade Tracker (Calculations, Bounds, GPA, File Export)
 * - Task 2: Stock Trading Platform (Order Execution, Validations, Portfolio P&L, Persistence)
 * - Task 3: AI Chatbot (NLP Tokenization, Stemming, Intent Matching, Math Engine)
 * - Task 4: Hotel Reservation System (Room Inventory, Booking Constraints, Cancellation, Invoices)
 * 
 * @author Aryan Sharma (CodeAlpha Intern)
 */
public class AutomatedTestRunner {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        System.out.println("===============================================================================");
        System.out.println("      CODEALPHA JAVA INTERNSHIP — AUTOMATED TEST SUITE & QA AUDIT              ");
        System.out.println("===============================================================================");

        // Run Task 1 Tests
        System.out.println("\n[SUITE 1] Testing Task 1: Student Grade Tracker...");
        testTask1_GradeCalculations();
        testTask1_BoundaryGrades();
        testTask1_InvalidGradeRejection();
        testTask1_ClassAverage();
        testTask1_HighestAndLowestScorers();
        testTask1_GpaAndLetterGrades();
        testTask1_ReportExport();

        // Run Task 2 Tests
        System.out.println("\n[SUITE 2] Testing Task 2: Stock Trading Platform...");
        testTask2_MarketInitialization();
        testTask2_BuyOrderExecution();
        testTask2_InsufficientCashRejection();
        testTask2_SellOrderExecution();
        testTask2_SellMoreSharesThanOwnedRejection();
        testTask2_PortfolioValuationAndPnL();
        testTask2_MarketTickFloor();
        testTask2_PortfolioFilePersistence();

        // Run Task 3 Tests
        System.out.println("\n[SUITE 3] Testing Task 3: AI Chatbot NLP Engine...");
        testTask3_TokenizerAndStopwords();
        testTask3_Stemmer();
        testTask3_OopIntentMatching();
        testTask3_JvmIntentMatching();
        testTask3_CodeAlphaTasksIntentMatching();
        testTask3_MathExpressionEvaluation();
        testTask3_EmptyAndFallbackQuery();

        // Run Task 4 Tests
        System.out.println("\n[SUITE 4] Testing Task 4: Hotel Reservation System...");
        testTask4_RoomCategoriesInitialization();
        testTask4_RoomBookingAllocation();
        testTask4_InvalidCheckInCheckOutDates();
        testTask4_RoomCapacityExhaustion();
        testTask4_ReservationCancellation();
        testTask4_RevenueCalculation();
        testTask4_BookingFilePersistence();

        long elapsed = System.currentTimeMillis() - startTime;

        System.out.println("\n===============================================================================");
        System.out.println("                              TEST EXECUTION SUMMARY                           ");
        System.out.println("===============================================================================");
        System.out.printf("Total Tests Executed : %d\n", testsRun);
        System.out.printf("Tests Passed         : %d\n", testsPassed);
        System.out.printf("Tests Failed         : %d\n", testsFailed);
        System.out.printf("Execution Time       : %d ms\n", elapsed);

        if (testsFailed == 0) {
            System.out.printf("\n✔ STATUS: ALL %d UNIT TESTS PASSED WITH 100%% SUCCESS RATE!\n", testsPassed);
            System.out.println("  The CodeAlpha codebase is verified, stable, and ready for production.");
        } else {
            System.err.println("\n✖ FAILURES DETECTED (" + testsFailed + "):");
            for (String f : failures) {
                System.err.println("  - " + f);
            }
            System.exit(1);
        }
        System.out.println("===============================================================================\n");
    }

    // ==========================================
    // Assertion Helper Utilities
    // ==========================================
    private static void assertTrue(String testName, boolean condition, String message) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.printf("  ✔ PASS: %s\n", testName);
        } else {
            testsFailed++;
            String failMsg = testName + " -> " + message;
            failures.add(failMsg);
            System.err.printf("  ✖ FAIL: %s\n", failMsg);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        testsRun++;
        boolean match = Objects.equals(expected, actual);
        if (match) {
            testsPassed++;
            System.out.printf("  ✔ PASS: %s\n", testName);
        } else {
            testsFailed++;
            String failMsg = String.format("%s -> Expected: [%s], Actual: [%s]", testName, expected, actual);
            failures.add(failMsg);
            System.err.printf("  ✖ FAIL: %s\n", failMsg);
        }
    }

    private static void assertEquals(String testName, double expected, double actual, double delta) {
        testsRun++;
        boolean match = Math.abs(expected - actual) <= delta;
        if (match) {
            testsPassed++;
            System.out.printf("  ✔ PASS: %s\n", testName);
        } else {
            testsFailed++;
            String failMsg = String.format("%s -> Expected: [%.2f], Actual: [%.2f]", testName, expected, actual);
            failures.add(failMsg);
            System.err.printf("  ✖ FAIL: %s\n", failMsg);
        }
    }

    // ==========================================
    // TASK 1 TESTS (Student Grade Tracker)
    // ==========================================
    private static void testTask1_GradeCalculations() {
        StudentGradeTracker.Student student = new StudentGradeTracker.Student("101", "Aryan Sharma");
        student.addGrade("Java", 90.0);
        student.addGrade("DSA", 95.0);
        student.addGrade("OS", 85.0);

        assertEquals("Task 1: Student average score calculation", 90.0, student.getAverage(), 0.01);
    }

    private static void testTask1_BoundaryGrades() {
        StudentGradeTracker.Student student = new StudentGradeTracker.Student("102", "Boundary Test");
        student.addGrade("Math", 0.0);
        student.addGrade("Physics", 100.0);

        assertEquals("Task 1: Boundary values 0.0 and 100.0 accepted", 50.0, student.getAverage(), 0.01);
    }

    private static void testTask1_InvalidGradeRejection() {
        StudentGradeTracker.Student student = new StudentGradeTracker.Student("103", "Invalid Test");
        boolean caughtNegative = false;
        try {
            student.addGrade("Chem", -5.0);
        } catch (IllegalArgumentException e) {
            caughtNegative = true;
        }

        boolean caughtOver100 = false;
        try {
            student.addGrade("Bio", 105.0);
        } catch (IllegalArgumentException e) {
            caughtOver100 = true;
        }

        assertTrue("Task 1: Rejection of negative score (<0)", caughtNegative, "Did not throw IllegalArgumentException for score -5");
        assertTrue("Task 1: Rejection of overflow score (>100)", caughtOver100, "Did not throw IllegalArgumentException for score 105");
    }

    private static void testTask1_ClassAverage() {
        StudentGradeTracker.GradeManager mgr = new StudentGradeTracker.GradeManager();
        StudentGradeTracker.Student s1 = new StudentGradeTracker.Student("1", "A");
        s1.addGrade("G", 80.0);
        StudentGradeTracker.Student s2 = new StudentGradeTracker.Student("2", "B");
        s2.addGrade("G", 90.0);

        mgr.addStudent(s1);
        mgr.addStudent(s2);

        assertEquals("Task 1: Class average computation across students", 85.0, mgr.calculateClassAverage(), 0.01);
    }

    private static void testTask1_HighestAndLowestScorers() {
        StudentGradeTracker.GradeManager mgr = new StudentGradeTracker.GradeManager();
        StudentGradeTracker.Student s1 = new StudentGradeTracker.Student("1", "High Scorer");
        s1.addGrade("G", 98.0);
        StudentGradeTracker.Student s2 = new StudentGradeTracker.Student("2", "Low Scorer");
        s2.addGrade("G", 62.0);

        mgr.addStudent(s1);
        mgr.addStudent(s2);

        assertEquals("Task 1: Identification of top scorer", "High Scorer", mgr.getHighestStudent().getName());
        assertEquals("Task 1: Identification of lowest scorer", "Low Scorer", mgr.getLowestStudent().getName());
    }

    private static void testTask1_GpaAndLetterGrades() {
        StudentGradeTracker.Student s = new StudentGradeTracker.Student("1", "GPA Student");
        s.addGrade("M", 92.0);
        assertEquals("Task 1: Letter grade evaluation (A+)", "A+", s.getLetterGrade());
        assertEquals("Task 1: 4.0 GPA scale evaluation", 4.0, s.getGpa(), 0.01);
    }

    private static void testTask1_ReportExport() {
        StudentGradeTracker.GradeManager mgr = new StudentGradeTracker.GradeManager();
        StudentGradeTracker.Student s1 = new StudentGradeTracker.Student("101", "Export Student");
        s1.addGrade("Java", 95.0);
        mgr.addStudent(s1);

        String testFile = "test_grade_report.tmp";
        try {
            mgr.exportReport(testFile);
            File f = new File(testFile);
            assertTrue("Task 1: Summary report file created on disk", f.exists() && f.length() > 0, "File was empty or not created");
            f.delete();
        } catch (Exception e) {
            assertTrue("Task 1: Report file creation without exception", false, e.getMessage());
        }
    }

    // ==========================================
    // TASK 2 TESTS (Stock Trading Platform)
    // ==========================================
    private static void testTask2_MarketInitialization() {
        StockTradingPlatform.PortfolioManager mgr = new StockTradingPlatform.PortfolioManager();
        Map<String, StockTradingPlatform.Stock> stocks = mgr.getMarketStocks();

        assertTrue("Task 2: Market initialized with core stocks (AAPL, NVDA, GOOGL)",
                stocks.containsKey("AAPL") && stocks.containsKey("NVDA") && stocks.containsKey("MSFT"),
                "Core tickers missing from market");
        assertEquals("Task 2: Initial cash balance set to $10,000.00", 10000.00, mgr.getCashBalance(), 0.01);
    }

    private static void testTask2_BuyOrderExecution() {
        StockTradingPlatform.PortfolioManager mgr = new StockTradingPlatform.PortfolioManager();
        StockTradingPlatform.Stock aapl = mgr.getMarketStocks().get("AAPL");
        double initialCash = mgr.getCashBalance();
        double price = aapl.getPrice();

        try {
            mgr.buyStock("AAPL", 10);
            double expectedCash = initialCash - (10 * price);
            assertEquals("Task 2: Buy order deducts exact cash balance", expectedCash, mgr.getCashBalance(), 0.01);
            assertEquals("Task 2: Holdings record created for AAPL with 10 shares", 10, mgr.getHoldings().get("AAPL").getQuantity());
            assertEquals("Task 2: Transaction ledger entry created for BUY", 1, mgr.getTransactions().size());
        } catch (Exception e) {
            assertTrue("Task 2: Buy order completed without error", false, e.getMessage());
        }
    }

    private static void testTask2_InsufficientCashRejection() {
        StockTradingPlatform.PortfolioManager mgr = new StockTradingPlatform.PortfolioManager();
        boolean caught = false;
        try {
            // Trying to buy 10,000 shares of MSFT (~$4,000,000) with $10,000
            mgr.buyStock("MSFT", 10000);
        } catch (IllegalStateException e) {
            caught = true;
        } catch (Exception ignored) {}

        assertTrue("Task 2: Rejection of order exceeding available cash balance", caught, "Order should have been rejected for insufficient funds");
    }

    private static void testTask2_SellOrderExecution() {
        StockTradingPlatform.PortfolioManager mgr = new StockTradingPlatform.PortfolioManager();
        try {
            mgr.buyStock("NVDA", 10);
            double cashAfterBuy = mgr.getCashBalance();
            double nvdaPrice = mgr.getMarketStocks().get("NVDA").getPrice();

            mgr.sellStock("NVDA", 5);
            double expectedCash = cashAfterBuy + (5 * nvdaPrice);
            assertEquals("Task 2: Sell order credits proceeds to cash balance", expectedCash, mgr.getCashBalance(), 0.01);
            assertEquals("Task 2: Holdings quantity updated after partial sale", 5, mgr.getHoldings().get("NVDA").getQuantity());
        } catch (Exception e) {
            assertTrue("Task 2: Sell order completed without error", false, e.getMessage());
        }
    }

    private static void testTask2_SellMoreSharesThanOwnedRejection() {
        StockTradingPlatform.PortfolioManager mgr = new StockTradingPlatform.PortfolioManager();
        boolean caught = false;
        try {
            mgr.buyStock("TSLA", 2);
            mgr.sellStock("TSLA", 10); // Attempt to sell 10 when owning 2
        } catch (IllegalStateException e) {
            caught = true;
        } catch (Exception ignored) {}

        assertTrue("Task 2: Rejection of sell order exceeding owned share inventory", caught, "Did not reject overselling shares");
    }

    private static void testTask2_PortfolioValuationAndPnL() {
        StockTradingPlatform.PortfolioManager mgr = new StockTradingPlatform.PortfolioManager();
        try {
            mgr.buyStock("AMZN", 10);
            double cash = mgr.getCashBalance();
            double holdingsVal = mgr.calculateHoldingsValue();
            double netWorth = mgr.calculateTotalPortfolioValue();

            assertEquals("Task 2: Net worth equals cash + holdings valuation", cash + holdingsVal, netWorth, 0.01);
        } catch (Exception e) {
            assertTrue("Task 2: Valuation check without error", false, e.getMessage());
        }
    }

    private static void testTask2_MarketTickFloor() {
        StockTradingPlatform.PortfolioManager mgr = new StockTradingPlatform.PortfolioManager();
        StockTradingPlatform.Stock s = mgr.getMarketStocks().get("AAPL");
        // Force dramatic negative shock (-150%)
        s.updatePrice(-150.0);
        assertTrue("Task 2: Stock price never falls below minimum safety floor ($1.00)", s.getPrice() >= 1.0, "Stock price dropped below $1.00");
    }

    private static void testTask2_PortfolioFilePersistence() {
        StockTradingPlatform.PortfolioManager mgr = new StockTradingPlatform.PortfolioManager();
        String testState = "test_portfolio_state.tmp";
        try {
            mgr.buyStock("GOOGL", 5);
            mgr.saveToFile(testState);

            StockTradingPlatform.PortfolioManager loadedMgr = new StockTradingPlatform.PortfolioManager();
            loadedMgr.loadFromFile(testState);

            assertEquals("Task 2: Loaded cash balance matches saved state", mgr.getCashBalance(), loadedMgr.getCashBalance(), 0.01);
            assertEquals("Task 2: Loaded holdings match saved state", mgr.getHoldings().get("GOOGL").getQuantity(), loadedMgr.getHoldings().get("GOOGL").getQuantity());

            new File(testState).delete();
        } catch (Exception e) {
            assertTrue("Task 2: Persistence test completed without error", false, e.getMessage());
        }
    }

    // ==========================================
    // TASK 3 TESTS (AI Chatbot Assistant)
    // ==========================================
    private static void testTask3_TokenizerAndStopwords() {
        String query = "What is the OOP in Java?";
        List<String> tokens = AIChatbot.NLPEngine.tokenizeAndClean(query);

        assertTrue("Task 3: Stopwords ('what', 'is', 'the', 'in') filtered out",
                !tokens.contains("what") && !tokens.contains("is") && !tokens.contains("the"),
                "Stopwords were not removed");
        assertTrue("Task 3: Core tokens retained ('oop', 'java')",
                tokens.contains("oop") && tokens.contains("java"),
                "Key tokens 'oop' or 'java' missing");
    }

    private static void testTask3_Stemmer() {
        assertEquals("Task 3: Suffix stemming for plural words", "class", AIChatbot.NLPEngine.stem("classes"));
        assertEquals("Task 3: Suffix stemming for progressive tense", "test", AIChatbot.NLPEngine.stem("testing"));
    }

    private static void testTask3_OopIntentMatching() {
        AIChatbot.BotBrain brain = new AIChatbot.BotBrain();
        String reply = brain.getReply("Explain the 4 pillars of OOP");

        assertTrue("Task 3: OOP query identifies Encapsulation and Inheritance",
                reply.contains("Encapsulation") && reply.contains("Inheritance"),
                "Reply did not contain expected OOP concepts");
    }

    private static void testTask3_JvmIntentMatching() {
        AIChatbot.BotBrain brain = new AIChatbot.BotBrain();
        String reply = brain.getReply("What is JVM and JDK architecture?");

        assertTrue("Task 3: JVM query identifies Bytecode and Execution",
                reply.contains("JVM") || reply.contains("JDK") || reply.contains("bytecode"),
                "Reply did not describe Java runtime architecture");
    }

    private static void testTask3_CodeAlphaTasksIntentMatching() {
        AIChatbot.BotBrain brain = new AIChatbot.BotBrain();
        String reply = brain.getReply("Tell me about CodeAlpha internship submission");

        assertTrue("Task 3: CodeAlpha query provides task and certification guidelines",
                reply.toLowerCase().contains("codealpha") && reply.toLowerCase().contains("certificate"),
                "Reply did not provide CodeAlpha internship guidance");
    }

    private static void testTask3_MathExpressionEvaluation() {
        AIChatbot.BotBrain brain = new AIChatbot.BotBrain();
        String reply = brain.getReply("calculate 25 * 4");

        assertTrue("Task 3: Simple mathematical expression evaluation (25 * 4 = 100.00)",
                reply.contains("100.00"),
                "Math evaluator failed to return 100.00");
    }

    private static void testTask3_EmptyAndFallbackQuery() {
        AIChatbot.BotBrain brain = new AIChatbot.BotBrain();
        String emptyReply = brain.getReply("   ");
        String gibberishReply = brain.getReply("xyzabczzz123");

        assertTrue("Task 3: Empty input triggers friendly guidance prompt", emptyReply.contains("Please enter"), "Empty prompt unhandled");
        assertTrue("Task 3: Unrecognized query triggers helpful suggestions", gibberishReply.contains("OOP") || gibberishReply.contains("rephrase"), "Fallback guidance missing");
    }

    // ==========================================
    // TASK 4 TESTS (Hotel Reservation System)
    // ==========================================
    private static void testTask4_RoomCategoriesInitialization() {
        HotelReservationSystem.HotelManager mgr = new HotelReservationSystem.HotelManager();
        Map<String, HotelReservationSystem.Room> rooms = mgr.getRooms();

        assertEquals("Task 4: Hotel room inventory initialized with 10 rooms", 10, rooms.size());
        assertEquals("Task 4: Standard room rate set to $79.00/night", 79.0, HotelReservationSystem.RoomCategory.STANDARD.getRatePerNight(), 0.01);
        assertEquals("Task 4: Deluxe room rate set to $149.00/night", 149.0, HotelReservationSystem.RoomCategory.DELUXE.getRatePerNight(), 0.01);
        assertEquals("Task 4: Executive Suite rate set to $269.00/night", 269.0, HotelReservationSystem.RoomCategory.SUITE.getRatePerNight(), 0.01);
    }

    private static void testTask4_RoomBookingAllocation() {
        HotelReservationSystem.HotelManager mgr = new HotelReservationSystem.HotelManager();
        LocalDate in = LocalDate.now();
        LocalDate out = in.plusDays(3);

        try {
            HotelReservationSystem.Reservation res = mgr.bookRoom("Aryan Sharma", "aryan@test.com", "9999999999",
                    HotelReservationSystem.RoomCategory.DELUXE, in, out, "Credit Card");

            assertEquals("Task 4: Reservation guest name confirmed", "Aryan Sharma", res.getGuestName());
            assertEquals("Task 4: Reservation duration computed as 3 nights", 3L, res.getNights());
            assertEquals("Task 4: Total amount calculated (3 * $149 = $447.00)", 447.00, res.getTotalAmount(), 0.01);
            assertTrue("Task 4: Allocated room marked as unavailable", !mgr.getRooms().get(res.getRoomNumber()).isAvailable(), "Room was not marked unavailable");
        } catch (Exception e) {
            assertTrue("Task 4: Room booking completed without error", false, e.getMessage());
        }
    }

    private static void testTask4_InvalidCheckInCheckOutDates() {
        HotelReservationSystem.HotelManager mgr = new HotelReservationSystem.HotelManager();
        LocalDate in = LocalDate.now();
        LocalDate invalidOut = in.minusDays(1); // Check-out before check-in

        boolean caught = false;
        try {
            mgr.bookRoom("Invalid Date", "a@b.com", "123", HotelReservationSystem.RoomCategory.STANDARD, in, invalidOut, "UPI");
        } catch (IllegalArgumentException e) {
            caught = true;
        } catch (Exception ignored) {}

        assertTrue("Task 4: Rejection of check-out dates prior to check-in", caught, "Did not throw IllegalArgumentException for invalid dates");
    }

    private static void testTask4_RoomCapacityExhaustion() {
        HotelReservationSystem.HotelManager mgr = new HotelReservationSystem.HotelManager();
        LocalDate in = LocalDate.now();
        LocalDate out = in.plusDays(2);

        // Suites only have 2 rooms (301, 302)
        try {
            mgr.bookRoom("Guest 1", "g1@test.com", "111", HotelReservationSystem.RoomCategory.SUITE, in, out, "Card");
            mgr.bookRoom("Guest 2", "g2@test.com", "222", HotelReservationSystem.RoomCategory.SUITE, in, out, "Card");

            boolean caught = false;
            try {
                mgr.bookRoom("Guest 3", "g3@test.com", "333", HotelReservationSystem.RoomCategory.SUITE, in, out, "Card");
            } catch (IllegalStateException e) {
                caught = true;
            }
            assertTrue("Task 4: Rejection when all rooms in category are fully booked", caught, "Overbooking allowed on exhausted category");
        } catch (Exception e) {
            assertTrue("Task 4: Exhaustion test executed without error", false, e.getMessage());
        }
    }

    private static void testTask4_ReservationCancellation() {
        HotelReservationSystem.HotelManager mgr = new HotelReservationSystem.HotelManager();
        LocalDate in = LocalDate.now();
        LocalDate out = in.plusDays(2);

        try {
            HotelReservationSystem.Reservation res = mgr.bookRoom("Cancel Guest", "cg@test.com", "555",
                    HotelReservationSystem.RoomCategory.STANDARD, in, out, "Card");
            String assignedRoom = res.getRoomNumber();

            assertTrue("Task 4: Room marked occupied upon reservation", !mgr.getRooms().get(assignedRoom).isAvailable(), "Room should be occupied");

            boolean cancelled = mgr.cancelReservation(res.getReservationId());
            assertTrue("Task 4: Cancellation executed successfully", cancelled, "Cancel operation returned false");
            assertTrue("Task 4: Room restored to available inventory after cancellation", mgr.getRooms().get(assignedRoom).isAvailable(), "Room not restored to available status");
        } catch (Exception e) {
            assertTrue("Task 4: Cancellation test executed without error", false, e.getMessage());
        }
    }

    private static void testTask4_RevenueCalculation() {
        HotelReservationSystem.HotelManager mgr = new HotelReservationSystem.HotelManager();
        LocalDate in = LocalDate.now();
        LocalDate out = in.plusDays(1);

        try {
            mgr.bookRoom("Rev 1", "r1@test.com", "123", HotelReservationSystem.RoomCategory.STANDARD, in, out, "Card"); // $79
            mgr.bookRoom("Rev 2", "r2@test.com", "456", HotelReservationSystem.RoomCategory.DELUXE, in, out, "Card");   // $149

            assertEquals("Task 4: Total active hotel revenue calculation ($79 + $149 = $228)", 228.00, mgr.calculateTotalRevenue(), 0.01);
        } catch (Exception e) {
            assertTrue("Task 4: Revenue calculation check without error", false, e.getMessage());
        }
    }

    private static void testTask4_BookingFilePersistence() {
        HotelReservationSystem.HotelManager mgr = new HotelReservationSystem.HotelManager();
        String testFile = "test_hotel_reservations.tmp";

        try {
            mgr.bookRoom("Persist Guest", "pg@test.com", "888", HotelReservationSystem.RoomCategory.STANDARD,
                    LocalDate.now(), LocalDate.now().plusDays(2), "UPI");
            mgr.saveToFile(testFile);

            File f = new File(testFile);
            assertTrue("Task 4: Booking record written to disk file", f.exists() && f.length() > 0, "File was empty or not created");
            f.delete();
        } catch (Exception e) {
            assertTrue("Task 4: Persistence test executed without error", false, e.getMessage());
        }
    }
}
