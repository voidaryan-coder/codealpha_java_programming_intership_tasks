import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * CodeAlpha Java Programming Internship — Task 2: Stock Trading Platform
 * 
 * Features:
 * - Real-time market simulation engine with price fluctuations
 * - Robust OOP architecture: Stock, Holding, Transaction, UserPortfolio, Market
 * - Buy / Sell execution with balance and share inventory validations
 * - Portfolio performance tracking (Net Worth, P&L %, Cash, Holdings)
 * - File I/O persistence for portfolio & transaction ledger
 * - ScamShield-inspired clean white Swing GUI + interactive CLI mode
 * 
 * @author Aryan Sharma (CodeAlpha Intern)
 */
public class StockTradingPlatform {

    // ==========================================
    // 1. Domain Models
    // ==========================================
    public static class Stock {
        private final String symbol;
        private final String companyName;
        private double price;
        private double previousClose;
        private double dayHigh;
        private double dayLow;

        public Stock(String symbol, String companyName, double initialPrice) {
            this.symbol = symbol;
            this.companyName = companyName;
            this.price = initialPrice;
            this.previousClose = initialPrice;
            this.dayHigh = initialPrice;
            this.dayLow = initialPrice;
        }

        public String getSymbol() { return symbol; }
        public String getCompanyName() { return companyName; }
        public double getPrice() { return price; }
        public double getPreviousClose() { return previousClose; }
        public double getDayHigh() { return dayHigh; }
        public double getDayLow() { return dayLow; }

        public double getChangePercent() {
            if (previousClose == 0) return 0;
            return ((price - previousClose) / previousClose) * 100.0;
        }

        public synchronized void updatePrice(double deltaPercent) {
            this.price = Math.max(1.0, this.price * (1.0 + (deltaPercent / 100.0)));
            if (this.price > this.dayHigh) this.dayHigh = this.price;
            if (this.price < this.dayLow) this.dayLow = this.price;
        }
    }

    public static class Holding {
        private final String symbol;
        private int quantity;
        private double totalCost;

        public Holding(String symbol, int quantity, double pricePerShare) {
            this.symbol = symbol;
            this.quantity = quantity;
            this.totalCost = quantity * pricePerShare;
        }

        public String getSymbol() { return symbol; }
        public int getQuantity() { return quantity; }
        public double getTotalCost() { return totalCost; }

        public double getAverageCost() {
            return quantity == 0 ? 0.0 : totalCost / quantity;
        }

        public void addShares(int shares, double price) {
            this.quantity += shares;
            this.totalCost += (shares * price);
        }

        public void removeShares(int shares) {
            if (shares > this.quantity) throw new IllegalArgumentException("Cannot sell more shares than owned.");
            double avgCost = getAverageCost();
            this.quantity -= shares;
            this.totalCost -= (shares * avgCost);
        }
    }

    public static class Transaction {
        private final String id;
        private final String timestamp;
        private final String type; // BUY or SELL
        private final String symbol;
        private final int quantity;
        private final double pricePerShare;
        private final double totalAmount;

        public Transaction(String type, String symbol, int quantity, double pricePerShare) {
            this.id = "TX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.type = type;
            this.symbol = symbol;
            this.quantity = quantity;
            this.pricePerShare = pricePerShare;
            this.totalAmount = quantity * pricePerShare;
        }

        public Transaction(String id, String timestamp, String type, String symbol, int quantity, double pricePerShare) {
            this.id = id;
            this.timestamp = timestamp;
            this.type = type;
            this.symbol = symbol;
            this.quantity = quantity;
            this.pricePerShare = pricePerShare;
            this.totalAmount = quantity * pricePerShare;
        }

        public String getId() { return id; }
        public String getTimestamp() { return timestamp; }
        public String getType() { return type; }
        public String getSymbol() { return symbol; }
        public int getQuantity() { return quantity; }
        public double getPricePerShare() { return pricePerShare; }
        public double getTotalAmount() { return totalAmount; }
    }

    // ==========================================
    // 2. User Portfolio & Market Manager
    // ==========================================
    public static class PortfolioManager {
        private double cashBalance = 10000.00; // Starting $10k seed capital
        private final Map<String, Holding> holdings = new LinkedHashMap<>();
        private final List<Transaction> transactions = new ArrayList<>();
        private final Map<String, Stock> marketStocks = new LinkedHashMap<>();

        public PortfolioManager() {
            initializeMarket();
        }

        private void initializeMarket() {
            marketStocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 182.50));
            marketStocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 148.20));
            marketStocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 415.75));
            marketStocks.put("NVDA", new Stock("NVDA", "NVIDIA Corp.", 124.90));
            marketStocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 218.40));
            marketStocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 186.30));
        }

        public Map<String, Stock> getMarketStocks() { return marketStocks; }
        public Map<String, Holding> getHoldings() { return holdings; }
        public List<Transaction> getTransactions() { return transactions; }
        public double getCashBalance() { return cashBalance; }

        public synchronized void buyStock(String symbol, int quantity) throws Exception {
            Stock stock = marketStocks.get(symbol.toUpperCase());
            if (stock == null) throw new IllegalArgumentException("Invalid stock symbol: " + symbol);
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");

            double totalCost = stock.getPrice() * quantity;
            if (totalCost > cashBalance) {
                throw new IllegalStateException(String.format("Insufficient funds! Required: $%.2f, Available: $%.2f", totalCost, cashBalance));
            }

            cashBalance -= totalCost;
            Holding h = holdings.get(stock.getSymbol());
            if (h == null) {
                holdings.put(stock.getSymbol(), new Holding(stock.getSymbol(), quantity, stock.getPrice()));
            } else {
                h.addShares(quantity, stock.getPrice());
            }

            transactions.add(0, new Transaction("BUY", stock.getSymbol(), quantity, stock.getPrice()));
        }

        public synchronized void sellStock(String symbol, int quantity) throws Exception {
            Stock stock = marketStocks.get(symbol.toUpperCase());
            if (stock == null) throw new IllegalArgumentException("Invalid stock symbol: " + symbol);
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");

            Holding h = holdings.get(stock.getSymbol());
            if (h == null || h.getQuantity() < quantity) {
                int owned = (h == null) ? 0 : h.getQuantity();
                throw new IllegalStateException(String.format("Insufficient shares! Owned: %d, Requested to sell: %d", owned, quantity));
            }

            double proceeds = stock.getPrice() * quantity;
            cashBalance += proceeds;
            h.removeShares(quantity);
            if (h.getQuantity() == 0) {
                holdings.remove(stock.getSymbol());
            }

            transactions.add(0, new Transaction("SELL", stock.getSymbol(), quantity, stock.getPrice()));
        }

        public double calculateHoldingsValue() {
            double value = 0;
            for (Holding h : holdings.values()) {
                Stock s = marketStocks.get(h.getSymbol());
                if (s != null) {
                    value += (h.getQuantity() * s.getPrice());
                }
            }
            return value;
        }

        public double calculateTotalPortfolioValue() {
            return cashBalance + calculateHoldingsValue();
        }

        public double calculateTotalUnrealizedPnL() {
            double pnl = 0;
            for (Holding h : holdings.values()) {
                Stock s = marketStocks.get(h.getSymbol());
                if (s != null) {
                    double currentVal = h.getQuantity() * s.getPrice();
                    pnl += (currentVal - h.getTotalCost());
                }
            }
            return pnl;
        }

        public synchronized void simulateMarketTick() {
            Random rng = new Random();
            for (Stock s : marketStocks.values()) {
                // Random fluctuation between -2.5% and +2.5%
                double delta = (rng.nextDouble() * 5.0) - 2.45;
                s.updatePrice(delta);
            }
        }

        public void saveToFile(String filePath) throws IOException {
            try (PrintWriter out = new PrintWriter(new FileWriter(filePath))) {
                out.println("# CODEALPHA STOCK TRADING PORTFOLIO STATE");
                out.printf("CASH:%.2f\n", cashBalance);
                out.println("[HOLDINGS]");
                for (Holding h : holdings.values()) {
                    out.printf("%s,%d,%.2f\n", h.getSymbol(), h.getQuantity(), h.getTotalCost());
                }
                out.println("[TRANSACTIONS]");
                for (Transaction t : transactions) {
                    out.printf("%s,%s,%s,%s,%d,%.2f\n", t.getId(), t.getTimestamp(), t.getType(), t.getSymbol(), t.getQuantity(), t.getPricePerShare());
                }
            }
        }

        public void loadFromFile(String filePath) throws IOException {
            File f = new File(filePath);
            if (!f.exists()) return;

            try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                String line;
                String section = "";
                holdings.clear();
                transactions.clear();

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    if (line.startsWith("CASH:")) {
                        cashBalance = Double.parseDouble(line.substring(5).trim());
                    } else if (line.equals("[HOLDINGS]")) {
                        section = "HOLDINGS";
                    } else if (line.equals("[TRANSACTIONS]")) {
                        section = "TRANSACTIONS";
                    } else {
                        String[] parts = line.split(",");
                        if ("HOLDINGS".equals(section) && parts.length >= 3) {
                            String sym = parts[0];
                            int qty = Integer.parseInt(parts[1]);
                            double cost = Double.parseDouble(parts[2]);
                            Holding h = new Holding(sym, qty, qty > 0 ? cost / qty : 0);
                            holdings.put(sym, h);
                        } else if ("TRANSACTIONS".equals(section) && parts.length >= 6) {
                            transactions.add(new Transaction(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), Double.parseDouble(parts[5])));
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // 3. Clean White ScamShield-Inspired GUI
    // ==========================================
    public static class TradingGUI extends JFrame {
        private final PortfolioManager manager = new PortfolioManager();
        private final DefaultTableModel marketTableModel;
        private final DefaultTableModel holdingsTableModel;
        private final DefaultTableModel txTableModel;

        private final JLabel cashLabel = new JLabel("$0.00");
        private final JLabel holdingsLabel = new JLabel("$0.00");
        private final JLabel netWorthLabel = new JLabel("$0.00");
        private final JLabel pnlLabel = new JLabel("+$0.00 (0.0%)");

        public TradingGUI() {
            setTitle("CodeAlpha — Stock Trading Platform");
            setSize(1100, 720);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Palette
            Color white = new Color(255, 255, 255);
            Color bgSurface = new Color(248, 250, 252);
            Color textDark = new Color(23, 32, 51);
            Color primaryBlue = new Color(37, 99, 235);
            Color borderColor = new Color(229, 231, 235);

            JPanel root = new JPanel(new BorderLayout(15, 15));
            root.setBackground(bgSurface);
            root.setBorder(new EmptyBorder(18, 18, 18, 18));

            // Top Header Banner
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(white);
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    new EmptyBorder(14, 18, 14, 18)
            ));
            JLabel title = new JLabel("Stock Trading Platform");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(textDark);
            JLabel subtitle = new JLabel("CodeAlpha Internship Task 2 • Real-Time Market Simulation & Portfolio OOP");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(100, 116, 139));
            header.add(title, BorderLayout.WEST);

            JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            headerBtns.setBackground(white);
            JButton tickBtn = new JButton("Simulate Price Tick");
            tickBtn.setBackground(new Color(239, 246, 255));
            tickBtn.setForeground(primaryBlue);
            tickBtn.setFocusPainted(false);

            JButton saveBtn = new JButton("Save State");
            saveBtn.setBackground(white);
            saveBtn.setForeground(textDark);

            headerBtns.add(tickBtn);
            headerBtns.add(saveBtn);
            header.add(headerBtns, BorderLayout.EAST);
            root.add(header, BorderLayout.NORTH);

            // Stat Cards Banner
            JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
            statsPanel.setBackground(bgSurface);
            statsPanel.add(createCard("Available Cash", cashLabel, primaryBlue));
            statsPanel.add(createCard("Holdings Value", holdingsLabel, new Color(79, 70, 229)));
            statsPanel.add(createCard("Total Net Worth", netWorthLabel, textDark));
            statsPanel.add(createCard("Unrealized P&L", pnlLabel, new Color(22, 163, 74)));

            // Center Panel: Split into Left (Market Watch) and Right (Holdings + Transactions)
            JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
            centerPanel.setBackground(bgSurface);
            centerPanel.add(statsPanel, BorderLayout.NORTH);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setResizeWeight(0.48);
            splitPane.setDividerSize(6);
            splitPane.setBorder(null);

            // Left: Market Watch Table
            JPanel marketPanel = new JPanel(new BorderLayout(0, 8));
            marketPanel.setBackground(white);
            marketPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            JLabel mTitle = new JLabel("Market Watch");
            mTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
            mTitle.setForeground(textDark);
            marketPanel.add(mTitle, BorderLayout.NORTH);

            String[] marketCols = {"Ticker", "Company", "Live Price", "Change %"};
            marketTableModel = new DefaultTableModel(marketCols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable marketTable = new JTable(marketTableModel);
            styleTable(marketTable, borderColor, textDark);

            // Price change renderer (green/red)
            marketTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(t, val, isSel, hasFoc, r, c);
                    setHorizontalAlignment(JLabel.CENTER);
                    String text = val != null ? val.toString() : "";
                    if (text.startsWith("+")) {
                        setForeground(new Color(22, 163, 74));
                    } else if (text.startsWith("-")) {
                        setForeground(new Color(220, 38, 38));
                    } else {
                        setForeground(textDark);
                    }
                    return comp;
                }
            });

            marketPanel.add(new JScrollPane(marketTable), BorderLayout.CENTER);

            // Market action buttons (Buy / Sell)
            JPanel tradeActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            tradeActionPanel.setBackground(white);
            JButton buyBtn = new JButton("Buy Stock");
            buyBtn.setBackground(new Color(22, 163, 74));
            buyBtn.setForeground(Color.WHITE);
            buyBtn.setFocusPainted(false);

            JButton sellBtn = new JButton("Sell Stock");
            sellBtn.setBackground(new Color(220, 38, 38));
            sellBtn.setForeground(Color.WHITE);
            sellBtn.setFocusPainted(false);

            tradeActionPanel.add(buyBtn);
            tradeActionPanel.add(sellBtn);
            marketPanel.add(tradeActionPanel, BorderLayout.SOUTH);
            splitPane.setLeftComponent(marketPanel);

            // Right: Tabbed Pane for Holdings & Order Ledger
            JTabbedPane rightTabs = new JTabbedPane();
            rightTabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

            // Holdings Table
            String[] holdCols = {"Ticker", "Shares", "Avg Cost", "Current Val", "P&L"};
            holdingsTableModel = new DefaultTableModel(holdCols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable holdingsTable = new JTable(holdingsTableModel);
            styleTable(holdingsTable, borderColor, textDark);
            JPanel holdingsPanel = new JPanel(new BorderLayout());
            holdingsPanel.setBackground(white);
            holdingsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
            holdingsPanel.add(new JScrollPane(holdingsTable), BorderLayout.CENTER);
            rightTabs.addTab("Current Holdings", holdingsPanel);

            // Transactions Table
            String[] txCols = {"Order ID", "Time", "Action", "Ticker", "Qty", "Price"};
            txTableModel = new DefaultTableModel(txCols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable txTable = new JTable(txTableModel);
            styleTable(txTable, borderColor, textDark);
            JPanel txPanel = new JPanel(new BorderLayout());
            txPanel.setBackground(white);
            txPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
            txPanel.add(new JScrollPane(txTable), BorderLayout.CENTER);
            rightTabs.addTab("Transaction History", txPanel);

            splitPane.setRightComponent(rightTabs);
            centerPanel.add(splitPane, BorderLayout.CENTER);
            root.add(centerPanel, BorderLayout.CENTER);

            // Seed initial portfolio holdings
            try {
                manager.buyStock("AAPL", 15);
                manager.buyStock("NVDA", 20);
            } catch (Exception ignored) {}

            refreshAll();

            // Auto price tick timer every 3 seconds
            javax.swing.Timer marketTimer = new javax.swing.Timer(3000, e -> {
                manager.simulateMarketTick();
                refreshAll();
            });
            marketTimer.start();

            // Actions
            tickBtn.addActionListener(e -> {
                manager.simulateMarketTick();
                refreshAll();
            });

            saveBtn.addActionListener(e -> {
                try {
                    manager.saveToFile("portfolio_state.txt");
                    JOptionPane.showMessageDialog(this, "Portfolio state saved to portfolio_state.txt", "Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            buyBtn.addActionListener(e -> openTradeDialog(marketTable, true));
            sellBtn.addActionListener(e -> openTradeDialog(marketTable, false));

            setContentPane(root);
        }

        private void openTradeDialog(JTable marketTable, boolean isBuy) {
            int row = marketTable.getSelectedRow();
            String defaultSymbol = (row >= 0) ? (String) marketTableModel.getValueAt(row, 0) : "AAPL";

            JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
            JComboBox<String> stockCombo = new JComboBox<>(manager.getMarketStocks().keySet().toArray(new String[0]));
            stockCombo.setSelectedItem(defaultSymbol);
            JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
            JLabel priceNotice = new JLabel();

            stockCombo.addActionListener(ev -> {
                String s = (String) stockCombo.getSelectedItem();
                Stock st = manager.getMarketStocks().get(s);
                if (st != null) priceNotice.setText(String.format("Price: $%.2f", st.getPrice()));
            });
            stockCombo.getActionListeners()[0].actionPerformed(null);

            panel.add(new JLabel("Select Stock:"));
            panel.add(stockCombo);
            panel.add(new JLabel("Quantity (Shares):"));
            panel.add(qtySpinner);
            panel.add(new JLabel("Market Rate:"));
            panel.add(priceNotice);

            int res = JOptionPane.showConfirmDialog(this, panel, isBuy ? "Execute Buy Order" : "Execute Sell Order", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String symbol = (String) stockCombo.getSelectedItem();
                int qty = (Integer) qtySpinner.getValue();
                try {
                    if (isBuy) {
                        manager.buyStock(symbol, qty);
                        JOptionPane.showMessageDialog(this, String.format("Bought %d shares of %s successfully!", qty, symbol), "Order Executed", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        manager.sellStock(symbol, qty);
                        JOptionPane.showMessageDialog(this, String.format("Sold %d shares of %s successfully!", qty, symbol), "Order Executed", JOptionPane.INFORMATION_MESSAGE);
                    }
                    refreshAll();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Transaction Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
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
            // Refresh Market
            marketTableModel.setRowCount(0);
            for (Stock s : manager.getMarketStocks().values()) {
                double chg = s.getChangePercent();
                String chgStr = String.format("%s%.2f%%", (chg >= 0 ? "+" : ""), chg);
                marketTableModel.addRow(new Object[]{
                        s.getSymbol(),
                        s.getCompanyName(),
                        String.format("$%.2f", s.getPrice()),
                        chgStr
                });
            }

            // Refresh Holdings
            holdingsTableModel.setRowCount(0);
            for (Holding h : manager.getHoldings().values()) {
                Stock s = manager.getMarketStocks().get(h.getSymbol());
                double curPrice = (s != null) ? s.getPrice() : 0.0;
                double curVal = h.getQuantity() * curPrice;
                double pnl = curVal - h.getTotalCost();
                holdingsTableModel.addRow(new Object[]{
                        h.getSymbol(),
                        h.getQuantity(),
                        String.format("$%.2f", h.getAverageCost()),
                        String.format("$%.2f", curVal),
                        String.format("%s$%.2f", (pnl >= 0 ? "+" : ""), pnl)
                });
            }

            // Refresh Transactions
            txTableModel.setRowCount(0);
            for (Transaction t : manager.getTransactions()) {
                txTableModel.addRow(new Object[]{
                        t.getId(),
                        t.getTimestamp(),
                        t.getType(),
                        t.getSymbol(),
                        t.getQuantity(),
                        String.format("$%.2f", t.getPricePerShare())
                });
            }

            // Refresh Stats
            cashLabel.setText(String.format("$%,.2f", manager.getCashBalance()));
            holdingsLabel.setText(String.format("$%,.2f", manager.calculateHoldingsValue()));
            double netWorth = manager.calculateTotalPortfolioValue();
            netWorthLabel.setText(String.format("$%,.2f", netWorth));

            double pnl = manager.calculateTotalUnrealizedPnL();
            double pnlPct = (netWorth > 0) ? (pnl / (netWorth - pnl)) * 100.0 : 0.0;
            pnlLabel.setText(String.format("%s$%,.2f (%s%.2f%%)", (pnl >= 0 ? "+" : ""), pnl, (pnl >= 0 ? "+" : ""), pnlPct));
            pnlLabel.setForeground(pnl >= 0 ? new Color(22, 163, 74) : new Color(220, 38, 38));
        }
    }

    // ==========================================
    // 4. Interactive Console CLI Mode
    // ==========================================
    public static void runCLI() {
        Scanner scanner = new Scanner(System.in);
        PortfolioManager manager = new PortfolioManager();

        System.out.println("==========================================================");
        System.out.println("   CodeAlpha — Task 2: Stock Trading Platform (CLI)       ");
        System.out.println("==========================================================");

        boolean running = true;
        while (running) {
            System.out.println("\n--- [PORTFOLIO OVERVIEW] ---");
            System.out.printf("Cash Available : $%,.2f\n", manager.getCashBalance());
            System.out.printf("Holdings Value : $%,.2f\n", manager.calculateHoldingsValue());
            System.out.printf("Total Net Worth: $%,.2f\n", manager.calculateTotalPortfolioValue());
            System.out.printf("Unrealized P&L : $%,.2f\n", manager.calculateTotalUnrealizedPnL());

            System.out.println("\n[1] View Market Rates & Tickers");
            System.out.println("[2] Buy Shares");
            System.out.println("[3] Sell Shares");
            System.out.println("[4] View My Holdings");
            System.out.println("[5] View Transaction History");
            System.out.println("[6] Simulate Market Price Tick");
            System.out.println("[7] Save Portfolio State File");
            System.out.println("[8] Exit");
            System.out.print("Enter choice (1-8): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println("\n----------------------------------------------------------");
                    System.out.printf("%-8s %-20s %-12s %-10s\n", "Symbol", "Company", "Live Price", "Change %");
                    System.out.println("----------------------------------------------------------");
                    for (Stock s : manager.getMarketStocks().values()) {
                        System.out.printf("%-8s %-20s $%-11.2f %s%.2f%%\n",
                                s.getSymbol(), s.getCompanyName(), s.getPrice(), (s.getChangePercent() >= 0 ? "+" : ""), s.getChangePercent());
                    }
                }
                case "2" -> {
                    System.out.print("Enter Stock Symbol to Buy (e.g. AAPL, NVDA): ");
                    String sym = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Enter Quantity to Buy: ");
                    try {
                        int qty = Integer.parseInt(scanner.nextLine().trim());
                        manager.buyStock(sym, qty);
                        System.out.printf("✔ Order filled! Bought %d shares of %s.\n", qty, sym);
                    } catch (Exception ex) {
                        System.out.println("✖ Trade rejected: " + ex.getMessage());
                    }
                }
                case "3" -> {
                    System.out.print("Enter Stock Symbol to Sell: ");
                    String sym = scanner.nextLine().trim().toUpperCase();
                    System.out.print("Enter Quantity to Sell: ");
                    try {
                        int qty = Integer.parseInt(scanner.nextLine().trim());
                        manager.sellStock(sym, qty);
                        System.out.printf("✔ Order filled! Sold %d shares of %s.\n", qty, sym);
                    } catch (Exception ex) {
                        System.out.println("✖ Trade rejected: " + ex.getMessage());
                    }
                }
                case "4" -> {
                    System.out.println("\n----------------------------------------------------------");
                    System.out.printf("%-8s %-10s %-12s %-12s\n", "Symbol", "Shares", "Avg Cost", "Current Val");
                    System.out.println("----------------------------------------------------------");
                    for (Holding h : manager.getHoldings().values()) {
                        Stock s = manager.getMarketStocks().get(h.getSymbol());
                        double curPrice = s != null ? s.getPrice() : 0.0;
                        System.out.printf("%-8s %-10d $%-11.2f $%-11.2f\n",
                                h.getSymbol(), h.getQuantity(), h.getAverageCost(), (h.getQuantity() * curPrice));
                    }
                }
                case "5" -> {
                    System.out.println("\n----------------------------------------------------------");
                    System.out.printf("%-10s %-20s %-6s %-8s %-6s %-10s\n", "ID", "Time", "Action", "Symbol", "Qty", "Price");
                    System.out.println("----------------------------------------------------------");
                    for (Transaction t : manager.getTransactions()) {
                        System.out.printf("%-10s %-20s %-6s %-8s %-6d $%-9.2f\n",
                                t.getId(), t.getTimestamp(), t.getType(), t.getSymbol(), t.getQuantity(), t.getPricePerShare());
                    }
                }
                case "6" -> {
                    manager.simulateMarketTick();
                    System.out.println("✔ Market prices updated with live ticker volatility!");
                }
                case "7" -> {
                    try {
                        manager.saveToFile("portfolio_state.txt");
                        System.out.println("✔ Saved portfolio state to portfolio_state.txt");
                    } catch (IOException e) {
                        System.out.println("✖ Error: " + e.getMessage());
                    }
                }
                case "8" -> {
                    System.out.println("Exiting Stock Trading Platform. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid selection. Please choose 1 to 8.");
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
                TradingGUI gui = new TradingGUI();
                gui.setVisible(true);
            });
        }
    }
}
