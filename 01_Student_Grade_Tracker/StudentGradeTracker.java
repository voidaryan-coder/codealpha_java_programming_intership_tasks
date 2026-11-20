import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

/**
 * CodeAlpha Java Programming Internship — Task 1: Student Grade Tracker
 * 
 * Features:
 * - Manage student records and grades using ArrayList
 * - Dynamic calculations: Class Average, Highest Score, Lowest Score, Letter Grades & GPA
 * - Modern ScamShield-inspired Clean White Swing GUI & Interactive CLI fallback
 * - Grade report export to text file
 * 
 * @author Aryan Sharma (CodeAlpha Intern)
 */
public class StudentGradeTracker {

    // ==========================================
    // Student Model
    // ==========================================
    public static class Student {
        private final String id;
        private final String name;
        private final Map<String, Double> subjectGrades;

        public Student(String id, String name) {
            this.id = id;
            this.name = name;
            this.subjectGrades = new LinkedHashMap<>();
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public Map<String, Double> getSubjectGrades() { return subjectGrades; }

        public void addGrade(String subject, double grade) {
            if (grade < 0.0 || grade > 100.0) {
                throw new IllegalArgumentException("Grade must be between 0.0 and 100.0");
            }
            subjectGrades.put(subject, grade);
        }

        public double getAverage() {
            if (subjectGrades.isEmpty()) return 0.0;
            double sum = 0;
            for (double g : subjectGrades.values()) {
                sum += g;
            }
            return sum / subjectGrades.size();
        }

        public String getLetterGrade() {
            double avg = getAverage();
            if (avg >= 90) return "A+";
            if (avg >= 80) return "A";
            if (avg >= 70) return "B";
            if (avg >= 60) return "C";
            if (avg >= 50) return "D";
            return "F";
        }

        public double getGpa() {
            double avg = getAverage();
            if (avg >= 90) return 4.0;
            if (avg >= 80) return 3.5;
            if (avg >= 70) return 3.0;
            if (avg >= 60) return 2.5;
            if (avg >= 50) return 2.0;
            return 0.0;
        }
    }

    // ==========================================
    // Grade Manager / Controller
    // ==========================================
    public static class GradeManager {
        private final List<Student> students = new ArrayList<>();

        public void addStudent(Student student) {
            students.add(student);
        }

        public boolean removeStudent(String id) {
            return students.removeIf(s -> s.getId().equalsIgnoreCase(id));
        }

        public List<Student> getStudents() {
            return Collections.unmodifiableList(students);
        }

        public double calculateClassAverage() {
            if (students.isEmpty()) return 0.0;
            double total = 0;
            int count = 0;
            for (Student s : students) {
                if (!s.getSubjectGrades().isEmpty()) {
                    total += s.getAverage();
                    count++;
                }
            }
            return count == 0 ? 0.0 : total / count;
        }

        public Student getHighestStudent() {
            if (students.isEmpty()) return null;
            return students.stream()
                    .filter(s -> !s.getSubjectGrades().isEmpty())
                    .max(Comparator.comparingDouble(Student::getAverage))
                    .orElse(null);
        }

        public Student getLowestStudent() {
            if (students.isEmpty()) return null;
            return students.stream()
                    .filter(s -> !s.getSubjectGrades().isEmpty())
                    .min(Comparator.comparingDouble(Student::getAverage))
                    .orElse(null);
        }

        public void exportReport(String filePath) throws IOException {
            try (PrintWriter out = new PrintWriter(new FileWriter(filePath))) {
                out.println("==========================================================");
                out.println("         CODEALPHA — STUDENT GRADE SUMMARY REPORT         ");
                out.println("==========================================================");
                out.printf("%-10s %-20s %-12s %-8s %-6s\n", "ID", "Name", "Average", "Grade", "GPA");
                out.println("----------------------------------------------------------");
                for (Student s : students) {
                    out.printf("%-10s %-20s %-12.2f %-8s %-6.2f\n",
                            s.getId(), s.getName(), s.getAverage(), s.getLetterGrade(), s.getGpa());
                }
                out.println("----------------------------------------------------------");
                out.printf("Total Students : %d\n", students.size());
                out.printf("Class Average  : %.2f%%\n", calculateClassAverage());
                Student high = getHighestStudent();
                Student low = getLowestStudent();
                if (high != null) out.printf("Top Scorer     : %s (%.2f%%)\n", high.getName(), high.getAverage());
                if (low != null)  out.printf("Lowest Scorer  : %s (%.2f%%)\n", low.getName(), low.getAverage());
                out.println("==========================================================");
            }
        }
    }

    // ==========================================
    // Modern Clean White Swing GUI
    // ==========================================
    public static class TrackerGUI extends JFrame {
        private final GradeManager manager = new GradeManager();
        private final DefaultTableModel tableModel;
        private final JLabel avgLabel = new JLabel("0.0%");
        private final JLabel highestLabel = new JLabel("N/A");
        private final JLabel lowestLabel = new JLabel("N/A");
        private final JLabel totalLabel = new JLabel("0");

        public TrackerGUI() {
            setTitle("CodeAlpha — Student Grade Tracker");
            setSize(950, 650);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Styling palette
            Color white = new Color(255, 255, 255);
            Color bgSurface = new Color(248, 250, 252);
            Color textDark = new Color(23, 32, 51);
            Color primaryBlue = new Color(37, 99, 235);
            Color borderColor = new Color(229, 231, 235);

            JPanel root = new JPanel(new BorderLayout(15, 15));
            root.setBackground(bgSurface);
            root.setBorder(new EmptyBorder(20, 20, 20, 20));

            // Header banner
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(white);
            header.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    new EmptyBorder(15, 20, 15, 20)
            ));
            JLabel title = new JLabel("Student Grade Tracker");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(textDark);
            JLabel subtitle = new JLabel("CodeAlpha Internship Task 1 • ArrayList & Grade Statistics Engine");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(100, 116, 139));
            header.add(title, BorderLayout.NORTH);
            header.add(subtitle, BorderLayout.SOUTH);
            root.add(header, BorderLayout.NORTH);

            // Statistics Strip Cards
            JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 0));
            statsPanel.setBackground(bgSurface);
            statsPanel.add(createStatCard("Total Students", totalLabel, primaryBlue));
            statsPanel.add(createStatCard("Class Average", avgLabel, new Color(16, 163, 74)));
            statsPanel.add(createStatCard("Highest Scorer", highestLabel, new Color(124, 58, 237)));
            statsPanel.add(createStatCard("Lowest Scorer", lowestLabel, new Color(220, 38, 38)));

            // Form inputs
            JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            formPanel.setBackground(white);
            formPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    new EmptyBorder(10, 15, 10, 15)
            ));

            JTextField idField = new JTextField(6);
            JTextField nameField = new JTextField(12);
            JTextField subjectField = new JTextField(10);
            JTextField gradeField = new JTextField(5);

            formPanel.add(new JLabel("Roll ID:"));
            formPanel.add(idField);
            formPanel.add(new JLabel("Student Name:"));
            formPanel.add(nameField);
            formPanel.add(new JLabel("Subject:"));
            formPanel.add(subjectField);
            formPanel.add(new JLabel("Score (0-100):"));
            formPanel.add(gradeField);

            JButton addBtn = new JButton("Add / Update Grade");
            addBtn.setBackground(primaryBlue);
            addBtn.setForeground(Color.WHITE);
            addBtn.setFocusPainted(false);
            formPanel.add(addBtn);

            JButton removeBtn = new JButton("Remove Selected");
            removeBtn.setBackground(Color.WHITE);
            removeBtn.setForeground(textDark);
            formPanel.add(removeBtn);

            JButton exportBtn = new JButton("Export Report");
            exportBtn.setBackground(Color.WHITE);
            exportBtn.setForeground(primaryBlue);
            formPanel.add(exportBtn);

            // Center Panel (Stats + Form + Table)
            JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
            centerPanel.setBackground(bgSurface);
            centerPanel.add(statsPanel, BorderLayout.NORTH);

            // Table
            String[] columns = {"Roll ID", "Student Name", "Subjects & Scores", "Average", "Letter Grade", "GPA"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable table = new JTable(tableModel);
            table.setRowHeight(28);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(241, 245, 249));
            table.getTableHeader().setForeground(textDark);
            table.setGridColor(borderColor);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
            scrollPane.getViewport().setBackground(Color.WHITE);

            JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
            tableContainer.setBackground(bgSurface);
            tableContainer.add(formPanel, BorderLayout.NORTH);
            tableContainer.add(scrollPane, BorderLayout.CENTER);

            centerPanel.add(tableContainer, BorderLayout.CENTER);
            root.add(centerPanel, BorderLayout.CENTER);

            // Add sample data
            seedSampleData();
            refreshTable();

            // Actions
            addBtn.addActionListener(e -> {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String subject = subjectField.getText().trim();
                String scoreStr = gradeField.getText().trim();

                if (id.isEmpty() || name.isEmpty() || subject.isEmpty() || scoreStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double score = Double.parseDouble(scoreStr);
                    if (score < 0 || score > 100) {
                        JOptionPane.showMessageDialog(this, "Score must be between 0 and 100.", "Range Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Student target = manager.getStudents().stream()
                            .filter(s -> s.getId().equalsIgnoreCase(id))
                            .findFirst()
                            .orElse(null);

                    if (target == null) {
                        target = new Student(id, name);
                        manager.addStudent(target);
                    }
                    target.addGrade(subject, score);

                    idField.setText("");
                    nameField.setText("");
                    subjectField.setText("");
                    gradeField.setText("");
                    refreshTable();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid score number: " + scoreStr, "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            removeBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a student from the table.", "Select Row", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String id = (String) tableModel.getValueAt(row, 0);
                manager.removeStudent(id);
                refreshTable();
            });

            exportBtn.addActionListener(e -> {
                try {
                    String file = "student_grades_report.txt";
                    manager.exportReport(file);
                    JOptionPane.showMessageDialog(this, "Report exported successfully to: " + file, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Failed to export report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            setContentPane(root);
        }

        private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            JLabel tLabel = new JLabel(title);
            tLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            tLabel.setForeground(new Color(100, 116, 139));

            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
            valueLabel.setForeground(accent);

            card.add(tLabel, BorderLayout.NORTH);
            card.add(valueLabel, BorderLayout.CENTER);
            return card;
        }

        private void seedSampleData() {
            Student s1 = new Student("101", "Aryan Sharma");
            s1.addGrade("Data Structures", 92);
            s1.addGrade("OOP (Java)", 95);
            s1.addGrade("Operating Systems", 89);
            manager.addStudent(s1);

            Student s2 = new Student("102", "Priya Sharma");
            s2.addGrade("Data Structures", 85);
            s2.addGrade("OOP (Java)", 88);
            s2.addGrade("Operating Systems", 82);
            manager.addStudent(s2);

            Student s3 = new Student("103", "Rohan Verma");
            s3.addGrade("Data Structures", 74);
            s3.addGrade("OOP (Java)", 78);
            s3.addGrade("Operating Systems", 71);
            manager.addStudent(s3);
        }

        private void refreshTable() {
            tableModel.setRowCount(0);
            for (Student s : manager.getStudents()) {
                StringBuilder sb = new StringBuilder();
                s.getSubjectGrades().forEach((sub, sc) -> sb.append(sub).append(": ").append(sc.intValue()).append(" | "));
                String subjects = sb.length() > 3 ? sb.substring(0, sb.length() - 3) : "No Grades";

                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        subjects,
                        String.format("%.1f%%", s.getAverage()),
                        s.getLetterGrade(),
                        String.format("%.2f", s.getGpa())
                });
            }

            totalLabel.setText(String.valueOf(manager.getStudents().size()));
            avgLabel.setText(String.format("%.1f%%", manager.calculateClassAverage()));
            Student high = manager.getHighestStudent();
            Student low = manager.getLowestStudent();
            highestLabel.setText(high != null ? high.getName() + " (" + String.format("%.1f%%", high.getAverage()) + ")" : "N/A");
            lowestLabel.setText(low != null ? low.getName() + " (" + String.format("%.1f%%", low.getAverage()) + ")" : "N/A");
        }
    }

    // ==========================================
    // Interactive Console CLI Mode
    // ==========================================
    public static void runCLI() {
        Scanner scanner = new Scanner(System.in);
        GradeManager manager = new GradeManager();

        // Seed initial data
        Student s1 = new Student("101", "Aryan Sharma");
        s1.addGrade("Java OOP", 94);
        s1.addGrade("DSA", 91);
        manager.addStudent(s1);

        Student s2 = new Student("102", "Priya Sharma");
        s2.addGrade("Java OOP", 86);
        s2.addGrade("DSA", 88);
        manager.addStudent(s2);

        System.out.println("==========================================================");
        System.out.println("   CodeAlpha — Task 1: Student Grade Tracker (CLI Mode)   ");
        System.out.println("==========================================================");

        boolean running = true;
        while (running) {
            System.out.println("\n[1] View All Students & Summary");
            System.out.println("[2] Add / Update Student Grade");
            System.out.println("[3] Remove Student");
            System.out.println("[4] Export Summary Report File");
            System.out.println("[5] Exit");
            System.out.print("Enter your choice (1-5): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    System.out.println("\n----------------------------------------------------------");
                    System.out.printf("%-10s %-18s %-10s %-8s %-6s\n", "ID", "Name", "Average", "Grade", "GPA");
                    System.out.println("----------------------------------------------------------");
                    for (Student s : manager.getStudents()) {
                        System.out.printf("%-10s %-18s %-10.2f %-8s %-6.2f\n",
                                s.getId(), s.getName(), s.getAverage(), s.getLetterGrade(), s.getGpa());
                    }
                    System.out.println("----------------------------------------------------------");
                    System.out.printf("Total Students : %d\n", manager.getStudents().size());
                    System.out.printf("Class Average  : %.2f%%\n", manager.calculateClassAverage());
                    Student h = manager.getHighestStudent();
                    Student l = manager.getLowestStudent();
                    if (h != null) System.out.printf("Highest Scorer : %s (%.2f%%)\n", h.getName(), h.getAverage());
                    if (l != null) System.out.printf("Lowest Scorer  : %s (%.2f%%)\n", l.getName(), l.getAverage());
                }
                case "2" -> {
                    System.out.print("Enter Student ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter Student Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Subject: ");
                    String subject = scanner.nextLine().trim();
                    System.out.print("Enter Score (0-100): ");
                    try {
                        double score = Double.parseDouble(scanner.nextLine().trim());
                        Student s = manager.getStudents().stream()
                                .filter(st -> st.getId().equalsIgnoreCase(id))
                                .findFirst()
                                .orElse(null);
                        if (s == null) {
                            s = new Student(id, name);
                            manager.addStudent(s);
                        }
                        s.addGrade(subject, score);
                        System.out.println("✔ Grade recorded successfully!");
                    } catch (Exception ex) {
                        System.out.println("✖ Error: " + ex.getMessage());
                    }
                }
                case "3" -> {
                    System.out.print("Enter Student ID to remove: ");
                    String id = scanner.nextLine().trim();
                    if (manager.removeStudent(id)) {
                        System.out.println("✔ Student removed.");
                    } else {
                        System.out.println("✖ Student not found.");
                    }
                }
                case "4" -> {
                    try {
                        manager.exportReport("student_grades_report.txt");
                        System.out.println("✔ Summary report written to student_grades_report.txt");
                    } catch (IOException e) {
                        System.out.println("✖ Failed to write file: " + e.getMessage());
                    }
                }
                case "5" -> {
                    System.out.println("Exiting Student Grade Tracker. Thank you!");
                    running = false;
                }
                default -> System.out.println("Invalid selection. Please choose 1 to 5.");
            }
        }
    }

    // ==========================================
    // Main Entry Point
    // ==========================================
    public static void main(String[] args) {
        // If headless or --cli flag passed, run CLI mode
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
                TrackerGUI gui = new TrackerGUI();
                gui.setVisible(true);
            });
        }
    }
}
