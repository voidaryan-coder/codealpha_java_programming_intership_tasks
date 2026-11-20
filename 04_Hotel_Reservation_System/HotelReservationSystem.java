import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

/**
 * CodeAlpha Java Programming Internship — Task 4: Hotel Reservation System
 * 
 * Features:
 * - Room categorization (Standard, Deluxe, Executive Suite)
 * - Search, book, and cancel reservations
 * - Payment simulation gateway (Credit/Debit Card, UPI, Net Banking)
 * - Detailed booking receipts & invoices
 * - File I/O persistence for bookings and availability
 * - Modern ScamShield-inspired clean white Swing GUI + interactive CLI mode
 * 
 * @author Aryan Sharma (CodeAlpha Intern)
 */
public class HotelReservationSystem {

    // ==========================================
    // 1. Domain Models & Categorization
    // ==========================================
    public enum RoomCategory {
        STANDARD("Standard Room", 79.0, 2, "Queen Bed, High-speed Wi-Fi, Work Desk, Air Conditioning"),
        DELUXE("Deluxe Room", 149.0, 3, "King Bed, City View, Smart 4K TV, Mini-bar, Breakfast Included"),
        SUITE("Executive Suite", 269.0, 4, "King Bed + Living Area, Panoramic Skyline, Jacuzzi, Free Lounge Access");

        private final String displayName;
        private final double ratePerNight;
        private final int maxGuests;
        private final String amenities;

        RoomCategory(String displayName, double ratePerNight, int maxGuests, String amenities) {
            this.displayName = displayName;
            this.ratePerNight = ratePerNight;
            this.maxGuests = maxGuests;
            this.amenities = amenities;
        }

        public String getDisplayName() { return displayName; }
        public double getRatePerNight() { return ratePerNight; }
        public int getMaxGuests() { return maxGuests; }
        public String getAmenities() { return amenities; }
    }

    public static class Room {
        private final String roomNumber;
        private final RoomCategory category;
        private boolean isAvailable;

        public Room(String roomNumber, RoomCategory category) {
            this.roomNumber = roomNumber;
            this.category = category;
            this.isAvailable = true;
        }

        public String getRoomNumber() { return roomNumber; }
        public RoomCategory getCategory() { return category; }
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { isAvailable = available; }
    }

    public static class Reservation {
        private final String reservationId;
        private final String guestName;
        private final String guestEmail;
        private final String guestPhone;
        private final String roomNumber;
        private final RoomCategory category;
        private final LocalDate checkIn;
        private final LocalDate checkOut;
        private final long nights;
        private final double totalAmount;
        private final String paymentMethod;
        private final String bookingTimestamp;
        private boolean active;

        public Reservation(String guestName, String guestEmail, String guestPhone,
                           String roomNumber, RoomCategory category,
                           LocalDate checkIn, LocalDate checkOut,
                           String paymentMethod) {
            this.reservationId = "BK-" + UUID.randomUUID().toString().substring(0, 7).toUpperCase();
            this.guestName = guestName;
            this.guestEmail = guestEmail;
            this.guestPhone = guestPhone;
            this.roomNumber = roomNumber;
            this.category = category;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.nights = Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
            this.totalAmount = this.nights * category.getRatePerNight();
            this.paymentMethod = paymentMethod;
            this.bookingTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.active = true;
        }

        public Reservation(String id, String guestName, String guestEmail, String guestPhone,
                           String roomNumber, RoomCategory category, LocalDate checkIn, LocalDate checkOut,
                           double totalAmount, String paymentMethod, String timestamp, boolean active) {
            this.reservationId = id;
            this.guestName = guestName;
            this.guestEmail = guestEmail;
            this.guestPhone = guestPhone;
            this.roomNumber = roomNumber;
            this.category = category;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.nights = Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
            this.totalAmount = totalAmount;
            this.paymentMethod = paymentMethod;
            this.bookingTimestamp = timestamp;
            this.active = active;
        }

        public String getReservationId() { return reservationId; }
        public String getGuestName() { return guestName; }
        public String getGuestEmail() { return guestEmail; }
        public String getGuestPhone() { return guestPhone; }
        public String getRoomNumber() { return roomNumber; }
        public RoomCategory getCategory() { return category; }
        public LocalDate getCheckIn() { return checkIn; }
        public LocalDate getCheckOut() { return checkOut; }
        public long getNights() { return nights; }
        public double getTotalAmount() { return totalAmount; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getBookingTimestamp() { return bookingTimestamp; }
        public boolean isActive() { return active; }
        public void cancel() { this.active = false; }
    }

    // ==========================================
    // 2. Hotel Manager Controller
    // ==========================================
    public static class HotelManager {
        private final Map<String, Room> rooms = new LinkedHashMap<>();
        private final List<Reservation> reservations = new ArrayList<>();

        public HotelManager() {
            initializeRooms();
        }

        private void initializeRooms() {
            // Standard Rooms (101 - 104)
            rooms.put("101", new Room("101", RoomCategory.STANDARD));
            rooms.put("102", new Room("102", RoomCategory.STANDARD));
            rooms.put("103", new Room("103", RoomCategory.STANDARD));
            rooms.put("104", new Room("104", RoomCategory.STANDARD));

            // Deluxe Rooms (201 - 204)
            rooms.put("201", new Room("201", RoomCategory.DELUXE));
            rooms.put("202", new Room("202", RoomCategory.DELUXE));
            rooms.put("203", new Room("203", RoomCategory.DELUXE));
            rooms.put("204", new Room("204", RoomCategory.DELUXE));

            // Executive Suites (301 - 302)
            rooms.put("301", new Room("301", RoomCategory.SUITE));
            rooms.put("302", new Room("302", RoomCategory.SUITE));
        }

        public Map<String, Room> getRooms() { return rooms; }
        public List<Reservation> getReservations() { return reservations; }

        public List<Room> getAvailableRooms(RoomCategory category) {
            List<Room> result = new ArrayList<>();
            for (Room r : rooms.values()) {
                if (r.isAvailable() && (category == null || r.getCategory() == category)) {
                    result.add(r);
                }
            }
            return result;
        }

        public synchronized Reservation bookRoom(String guestName, String guestEmail, String guestPhone,
                                                 RoomCategory category, LocalDate checkIn, LocalDate checkOut,
                                                 String paymentMethod) throws Exception {
            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                throw new IllegalArgumentException("Check-out date must be strictly after check-in date.");
            }
            List<Room> available = getAvailableRooms(category);
            if (available.isEmpty()) {
                throw new IllegalStateException("No " + category.getDisplayName() + " is currently available for booking.");
            }

            Room allocatedRoom = available.get(0);
            allocatedRoom.setAvailable(false);

            Reservation res = new Reservation(guestName, guestEmail, guestPhone,
                    allocatedRoom.getRoomNumber(), category, checkIn, checkOut, paymentMethod);
            reservations.add(0, res);
            return res;
        }

        public synchronized boolean cancelReservation(String reservationId) {
            for (Reservation r : reservations) {
                if (r.getReservationId().equalsIgnoreCase(reservationId) && r.isActive()) {
                    r.cancel();
                    Room rm = rooms.get(r.getRoomNumber());
                    if (rm != null) rm.setAvailable(true);
                    return true;
                }
            }
            return false;
        }

        public double calculateTotalRevenue() {
            return reservations.stream().filter(Reservation::isActive).mapToDouble(Reservation::getTotalAmount).sum();
        }

        public void saveToFile(String filePath) throws IOException {
            try (PrintWriter out = new PrintWriter(new FileWriter(filePath))) {
                out.println("# CODEALPHA HOTEL RESERVATION DATA");
                for (Reservation r : reservations) {
                    out.printf("%s|%s|%s|%s|%s|%s|%s|%s|%.2f|%s|%s|%b\n",
                            r.getReservationId(), r.getGuestName(), r.getGuestEmail(), r.getGuestPhone(),
                            r.getRoomNumber(), r.getCategory().name(), r.getCheckIn(), r.getCheckOut(),
                            r.getTotalAmount(), r.getPaymentMethod(), r.getBookingTimestamp(), r.isActive());
                }
            }
        }
    }

    // ==========================================
    // 3. Modern Clean White Swing GUI
    // ==========================================
    public static class HotelGUI extends JFrame {
        private final HotelManager manager = new HotelManager();
        private final DefaultTableModel roomsTableModel;
        private final DefaultTableModel bookingsTableModel;

        private final JLabel totalRoomsLabel = new JLabel("10");
        private final JLabel availableRoomsLabel = new JLabel("10");
        private final JLabel activeBookingsLabel = new JLabel("0");
        private final JLabel revenueLabel = new JLabel("$0.00");

        public HotelGUI() {
            setTitle("CodeAlpha — Hotel Reservation System");
            setSize(1100, 720);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Color white = new Color(255, 255, 255);
            Color bgSurface = new Color(248, 250, 252);
            Color textDark = new Color(23, 32, 51);
            Color primaryBlue = new Color(37, 99, 235);
            Color borderColor = new Color(229, 231, 235);

            JPanel root = new JPanel(new BorderLayout(15, 15));
            root.setBackground(bgSurface);
            root.setBorder(new EmptyBorder(18, 18, 18, 18));

            // Top Header
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(white);
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    new EmptyBorder(14, 18, 14, 18)
            ));
            JLabel title = new JLabel("Hotel Reservation System");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(textDark);
            JLabel subtitle = new JLabel("CodeAlpha Internship Task 4 • Room Categorization, Payment Simulation & Bookings OOP");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(100, 116, 139));
            header.add(title, BorderLayout.WEST);

            JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            headerBtns.setBackground(white);
            JButton newBookingBtn = new JButton("+ Book Room");
            newBookingBtn.setBackground(primaryBlue);
            newBookingBtn.setForeground(Color.WHITE);
            newBookingBtn.setFocusPainted(false);

            JButton exportBtn = new JButton("Save / Export");
            exportBtn.setBackground(white);
            exportBtn.setForeground(textDark);

            headerBtns.add(newBookingBtn);
            headerBtns.add(exportBtn);
            header.add(headerBtns, BorderLayout.EAST);
            root.add(header, BorderLayout.NORTH);

            // Stat Cards
            JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
            statsPanel.setBackground(bgSurface);
            statsPanel.add(createCard("Total Rooms", totalRoomsLabel, textDark));
            statsPanel.add(createCard("Available Rooms", availableRoomsLabel, new Color(22, 163, 74)));
            statsPanel.add(createCard("Active Bookings", activeBookingsLabel, primaryBlue));
            statsPanel.add(createCard("Simulated Revenue", revenueLabel, new Color(124, 58, 237)));

            // Center: Tabbed Pane (Rooms Catalog vs Bookings Ledger)
            JTabbedPane tabs = new JTabbedPane();
            tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

            // Tab 1: Rooms Inventory
            JPanel roomsPanel = new JPanel(new BorderLayout(0, 8));
            roomsPanel.setBackground(white);
            roomsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            String[] roomCols = {"Room No", "Category", "Rate / Night", "Max Guests", "Status", "Included Amenities"};
            roomsTableModel = new DefaultTableModel(roomCols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable roomsTable = new JTable(roomsTableModel);
            styleTable(roomsTable, borderColor, textDark);

            // Status custom color renderer
            roomsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasFoc, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(t, v, isSel, hasFoc, r, c);
                    setHorizontalAlignment(JLabel.CENTER);
                    String s = (v != null) ? v.toString() : "";
                    if ("Available".equalsIgnoreCase(s)) {
                        setForeground(new Color(22, 163, 74));
                    } else {
                        setForeground(new Color(220, 38, 38));
                    }
                    return comp;
                }
            });

            roomsPanel.add(new JScrollPane(roomsTable), BorderLayout.CENTER);
            tabs.addTab("Rooms & Categorization", roomsPanel);

            // Tab 2: Bookings Ledger
            JPanel bookingsPanel = new JPanel(new BorderLayout(0, 8));
            bookingsPanel.setBackground(white);
            bookingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            String[] bookCols = {"Booking ID", "Guest Name", "Room", "Category", "Check-In", "Check-Out", "Nights", "Total Paid", "Status"};
            bookingsTableModel = new DefaultTableModel(bookCols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable bookingsTable = new JTable(bookingsTableModel);
            styleTable(bookingsTable, borderColor, textDark);

            JPanel bookingActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            bookingActionPanel.setBackground(white);
            JButton cancelBtn = new JButton("Cancel Selected Booking");
            cancelBtn.setBackground(new Color(254, 242, 242));
            cancelBtn.setForeground(new Color(220, 38, 38));
            cancelBtn.setFocusPainted(false);

            JButton receiptBtn = new JButton("View Invoice / Receipt");
            receiptBtn.setBackground(white);
            receiptBtn.setForeground(textDark);

            bookingActionPanel.add(receiptBtn);
            bookingActionPanel.add(cancelBtn);

            bookingsPanel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
            bookingsPanel.add(bookingActionPanel, BorderLayout.SOUTH);
            tabs.addTab("Guest Reservations & Ledger", bookingsPanel);

            JPanel center = new JPanel(new BorderLayout(0, 12));
            center.setBackground(bgSurface);
            center.add(statsPanel, BorderLayout.NORTH);
            center.add(tabs, BorderLayout.CENTER);
            root.add(center, BorderLayout.CENTER);

            // Seed sample reservation
            try {
                manager.bookRoom("Aryan Sharma", "aryan@example.com", "+91 9876543210",
                        RoomCategory.DELUXE, LocalDate.now(), LocalDate.now().plusDays(3), "Credit Card (Simulated)");
            } catch (Exception ignored) {}

            refreshAll();

            // Actions
            newBookingBtn.addActionListener(e -> openBookingDialog());
            exportBtn.addActionListener(e -> {
                try {
                    manager.saveToFile("hotel_reservations.txt");
                    JOptionPane.showMessageDialog(this, "Reservations saved to hotel_reservations.txt", "Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelBtn.addActionListener(e -> {
                int row = bookingsTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String bId = (String) bookingsTableModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel reservation " + bId + "?", "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (manager.cancelReservation(bId)) {
                        JOptionPane.showMessageDialog(this, "Reservation cancelled and room marked available.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                        refreshAll();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to cancel or already inactive.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            receiptBtn.addActionListener(e -> {
                int row = bookingsTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a booking to view receipt.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String bId = (String) bookingsTableModel.getValueAt(row, 0);
                Reservation target = manager.getReservations().stream().filter(r -> r.getReservationId().equals(bId)).findFirst().orElse(null);
                if (target != null) {
                    showReceiptDialog(target);
                }
            });

            setContentPane(root);
        }

        private void openBookingDialog() {
            JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField phoneField = new JTextField();
            JComboBox<RoomCategory> catCombo = new JComboBox<>(RoomCategory.values());
            JSpinner nightsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 30, 1));
            JComboBox<String> payCombo = new JComboBox<>(new String[]{"Credit Card (Simulated)", "UPI Simulation", "Debit Card", "Net Banking"});

            panel.add(new JLabel("Guest Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Email Address:"));
            panel.add(emailField);
            panel.add(new JLabel("Phone Number:"));
            panel.add(phoneField);
            panel.add(new JLabel("Room Category:"));
            panel.add(catCombo);
            panel.add(new JLabel("Length of Stay (Nights):"));
            panel.add(nightsSpinner);
            panel.add(new JLabel("Payment Method:"));
            panel.add(payCombo);

            int res = JOptionPane.showConfirmDialog(this, panel, "New Room Booking & Payment Simulation", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                RoomCategory cat = (RoomCategory) catCombo.getSelectedItem();
                int nights = (Integer) nightsSpinner.getValue();
                String payMethod = (String) payCombo.getSelectedItem();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    LocalDate in = LocalDate.now();
                    LocalDate out = in.plusDays(nights);
                    Reservation b = manager.bookRoom(name, email, phone, cat, in, out, payMethod);
                    refreshAll();
                    showReceiptDialog(b);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Rejected", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void showReceiptDialog(Reservation r) {
            String receipt = String.format("""
                =======================================================
                            CODEALPHA LUXURY RESORT
                          OFFICIAL BOOKING INVOICE
                =======================================================
                Booking ID      : %s
                Guest Name      : %s
                Email           : %s
                Phone           : %s
                -------------------------------------------------------
                Room Number     : %s
                Category        : %s
                Check-In Date   : %s
                Check-Out Date  : %s
                Total Nights    : %d
                Rate per Night  : $%.2f
                -------------------------------------------------------
                Total Amount    : $%.2f
                Payment Method  : %s
                Payment Status  : %s
                Booking Time    : %s
                =======================================================
                """,
                    r.getReservationId(), r.getGuestName(), r.getGuestEmail(), r.getGuestPhone(),
                    r.getRoomNumber(), r.getCategory().getDisplayName(),
                    r.getCheckIn(), r.getCheckOut(), r.getNights(), r.getCategory().getRatePerNight(),
                    r.getTotalAmount(), r.getPaymentMethod(),
                    r.isActive() ? "PAID (CONFIRMED)" : "CANCELLED / REFUNDED",
                    r.getBookingTimestamp()
            );

            JTextArea area = new JTextArea(receipt);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Booking Invoice #" + r.getReservationId(), JOptionPane.INFORMATION_MESSAGE);
        }

        private void styleTable(JTable table, Color borderColor, Color textDark) {
            table.setRowHeight(26);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(241, 245, 249));
            table.getTableHeader().setForeground(textDark);
            table.setGridColor(borderColor);
        }

        private JPanel createCard(String title, JLabel label, Color accent) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            JLabel t = new JLabel(title);
            t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            t.setForeground(new Color(100, 116, 139));
            label.setFont(new Font("Segoe UI", Font.BOLD, 17));
            label.setForeground(accent);
            card.add(t, BorderLayout.NORTH);
            card.add(label, BorderLayout.CENTER);
            return card;
        }

        private void refreshAll() {
            // Refresh Rooms
            roomsTableModel.setRowCount(0);
            int availCount = 0;
            for (Room r : manager.getRooms().values()) {
                if (r.isAvailable()) availCount++;
                roomsTableModel.addRow(new Object[]{
                        r.getRoomNumber(),
                        r.getCategory().getDisplayName(),
                        String.format("$%.2f", r.getCategory().getRatePerNight()),
                        r.getCategory().getMaxGuests(),
                        r.isAvailable() ? "Available" : "Occupied",
                        r.getCategory().getAmenities()
                });
            }

            // Refresh Bookings
            bookingsTableModel.setRowCount(0);
            int activeCount = 0;
            for (Reservation res : manager.getReservations()) {
                if (res.isActive()) activeCount++;
                bookingsTableModel.addRow(new Object[]{
                        res.getReservationId(),
                        res.getGuestName(),
                        res.getRoomNumber(),
                        res.getCategory().getDisplayName(),
                        res.getCheckIn().toString(),
                        res.getCheckOut().toString(),
                        res.getNights(),
                        String.format("$%.2f", res.getTotalAmount()),
                        res.isActive() ? "Confirmed" : "Cancelled"
                });
            }

            // Update Stats
            totalRoomsLabel.setText(String.valueOf(manager.getRooms().size()));
            availableRoomsLabel.setText(String.valueOf(availCount));
            activeBookingsLabel.setText(String.valueOf(activeCount));
            revenueLabel.setText(String.format("$%,.2f", manager.calculateTotalRevenue()));
        }
    }

    // ==========================================
    // 4. Interactive Console CLI Mode
    // ==========================================
    public static void runCLI() {
        Scanner scanner = new Scanner(System.in);
        HotelManager manager = new HotelManager();

        System.out.println("==========================================================");
        System.out.println("   CodeAlpha — Task 4: Hotel Reservation System (CLI)     ");
        System.out.println("==========================================================");

        boolean running = true;
        while (running) {
            System.out.println("\n[1] View All Rooms & Availability");
            System.out.println("[2] Book a Room (Search & Reserve)");
            System.out.println("[3] View All Reservations");
            System.out.println("[4] Cancel a Reservation");
            System.out.println("[5] Save Data File");
            System.out.println("[6] Exit");
            System.out.print("Enter choice (1-6): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println("\n-------------------------------------------------------------------------------");
                    System.out.printf("%-8s %-18s %-12s %-10s %-25s\n", "Room", "Category", "Rate/Night", "Status", "Amenities");
                    System.out.println("-------------------------------------------------------------------------------");
                    for (Room r : manager.getRooms().values()) {
                        System.out.printf("%-8s %-18s $%-11.2f %-10s %-25s\n",
                                r.getRoomNumber(), r.getCategory().getDisplayName(),
                                r.getCategory().getRatePerNight(),
                                r.isAvailable() ? "Available" : "Occupied",
                                r.getCategory().getAmenities().substring(0, Math.min(25, r.getCategory().getAmenities().length())) + "...");
                    }
                }
                case "2" -> {
                    System.out.println("\nSelect Room Category:");
                    System.out.println("1. Standard Room ($79/night)");
                    System.out.println("2. Deluxe Room ($149/night)");
                    System.out.println("3. Executive Suite ($269/night)");
                    System.out.print("Choice (1-3): ");
                    String catChoice = scanner.nextLine().trim();
                    RoomCategory cat = switch (catChoice) {
                        case "2" -> RoomCategory.DELUXE;
                        case "3" -> RoomCategory.SUITE;
                        default -> RoomCategory.STANDARD;
                    };

                    System.out.print("Enter Guest Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Email Address: ");
                    String email = scanner.nextLine().trim();
                    System.out.print("Enter Phone Number: ");
                    String phone = scanner.nextLine().trim();
                    System.out.print("Number of Nights: ");
                    try {
                        int nights = Integer.parseInt(scanner.nextLine().trim());
                        LocalDate checkIn = LocalDate.now();
                        LocalDate checkOut = checkIn.plusDays(nights);

                        Reservation res = manager.bookRoom(name, email, phone, cat, checkIn, checkOut, "Credit Card (Simulated)");
                        System.out.println("\n✔ Booking Confirmed!");
                        System.out.printf("Booking ID  : %s\n", res.getReservationId());
                        System.out.printf("Assigned Rm : %s (%s)\n", res.getRoomNumber(), res.getCategory().getDisplayName());
                        System.out.printf("Total Paid  : $%.2f (for %d nights)\n", res.getTotalAmount(), res.getNights());
                    } catch (Exception ex) {
                        System.out.println("✖ Booking failed: " + ex.getMessage());
                    }
                }
                case "3" -> {
                    System.out.println("\n-------------------------------------------------------------------------------");
                    System.out.printf("%-10s %-16s %-6s %-16s %-10s %-10s\n", "ID", "Guest", "Room", "Category", "Total", "Status");
                    System.out.println("-------------------------------------------------------------------------------");
                    for (Reservation res : manager.getReservations()) {
                        System.out.printf("%-10s %-16s %-6s %-16s $%-9.2f %-10s\n",
                                res.getReservationId(), res.getGuestName(), res.getRoomNumber(),
                                res.getCategory().getDisplayName(), res.getTotalAmount(),
                                res.isActive() ? "Confirmed" : "Cancelled");
                    }
                }
                case "4" -> {
                    System.out.print("Enter Booking ID to Cancel: ");
                    String bId = scanner.nextLine().trim();
                    if (manager.cancelReservation(bId)) {
                        System.out.println("✔ Reservation cancelled. Room restored to inventory.");
                    } else {
                        System.out.println("✖ Reservation not found or already inactive.");
                    }
                }
                case "5" -> {
                    try {
                        manager.saveToFile("hotel_reservations.txt");
                        System.out.println("✔ Saved to hotel_reservations.txt");
                    } catch (IOException e) {
                        System.out.println("✖ Error: " + e.getMessage());
                    }
                }
                case "6" -> {
                    System.out.println("Exiting Hotel Reservation System. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid selection. Please choose 1 to 6.");
            }
        }
    }

    // ==========================================
    // 5. Main Entry Point
    // ==========================================
    public static void main(String[] args) {
        boolean headless = GraphicsEnvironment.isHeadless();
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                headless = true;
                break;
            }
        }

        if (headless) {
            runCLI();
        } else {
            SwingUtilities.invokeLater(() -> {
                HotelGUI gui = new HotelGUI();
                gui.setVisible(true);
            });
        }
    }
}
