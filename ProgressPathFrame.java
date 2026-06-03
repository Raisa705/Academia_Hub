import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

public class ProgressPathFrame extends JFrame {

    private String studentName;
    private Map<String, CourseProgress> courses = new LinkedHashMap<>();
    private static final String DATA_FILE = "progress_data_courses.dat";

    private JPanel coursesPanel; // panel for all course charts
    private PieChartPanel pieChartPanel;

    public ProgressPathFrame(String studentName) {
        this.studentName = studentName;

        setTitle(studentName + " - Progress Path");
        setSize(UIConfig.WINDOW_WIDTH, UIConfig.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel headline = new JLabel("📊 Student Progress Path", JLabel.CENTER);
        headline.setFont(new Font("Arial", Font.BOLD, 22));
        add(headline, BorderLayout.NORTH);

        // ===== Courses panel =====
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.setPreferredSize(new Dimension(600, 0));
        add(scrollPane, BorderLayout.WEST);

        // ===== Pie chart panel =====
        pieChartPanel = new PieChartPanel();
        pieChartPanel.setPreferredSize(new Dimension(500, 0));
        add(pieChartPanel, BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel btnPanel = new JPanel();
        JButton addCourseBtn = new JButton("Add/Edit Course");
        JButton deleteCourseBtn = new JButton("Delete Course"); // 🔹 New delete button
        JButton closeBtn = new JButton("Close");
        btnPanel.add(addCourseBtn);
        btnPanel.add(deleteCourseBtn);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        addCourseBtn.addActionListener(e -> addOrEditCourse());
        deleteCourseBtn.addActionListener(e -> deleteCourse()); // 🔹 Delete action
        closeBtn.addActionListener(e -> dispose());

        loadData();
        refreshCourses();
        setVisible(true);
    }

    // ===== Course class =====
    static class CourseProgress implements Serializable {
        String name;
        int completedLessons, totalLessons;
        int testMarksObtained, testMarksTotal;
        int attendedClasses, totalClasses;
        int doneAssignments, totalAssignments;

        CourseProgress(String name) { this.name = name; }

        public double getOverallProgress() {
            int done = completedLessons + testMarksObtained + attendedClasses + doneAssignments;
            int total = totalLessons + testMarksTotal + totalClasses + totalAssignments;
            return total > 0 ? (double) done / total : 0.0;
        }
    }

    // ===== Add/Edit course =====
    private void addOrEditCourse() {
        String courseName = JOptionPane.showInputDialog(this, "Enter Course Name:");
        if (courseName == null || courseName.trim().isEmpty()) return;
        courseName = courseName.trim();

        CourseProgress cp = courses.getOrDefault(courseName, new CourseProgress(courseName));

        try {
            cp.completedLessons = Integer.parseInt(JOptionPane.showInputDialog(this, "Completed Lessons:", cp.completedLessons));
            cp.totalLessons = Integer.parseInt(JOptionPane.showInputDialog(this, "Total Lessons:", cp.totalLessons));

            cp.testMarksObtained = Integer.parseInt(JOptionPane.showInputDialog(this, "Obtained Test Marks:", cp.testMarksObtained));
            cp.testMarksTotal = Integer.parseInt(JOptionPane.showInputDialog(this, "Total Test Marks:", cp.testMarksTotal));

            cp.attendedClasses = Integer.parseInt(JOptionPane.showInputDialog(this, "Attended Classes:", cp.attendedClasses));
            cp.totalClasses = Integer.parseInt(JOptionPane.showInputDialog(this, "Total Classes:", cp.totalClasses));

            cp.doneAssignments = Integer.parseInt(JOptionPane.showInputDialog(this, "Done Assignments:", cp.doneAssignments));
            cp.totalAssignments = Integer.parseInt(JOptionPane.showInputDialog(this, "Total Assignments:", cp.totalAssignments));

            courses.put(courseName, cp);
            saveData();
            refreshCourses();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠ Invalid input! Please enter numbers only.");
        }
    }

    // ===== Delete course =====
    private void deleteCourse() {
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses available to delete.");
            return;
        }

        String[] courseNames = courses.keySet().toArray(new String[0]);
        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select course to delete:",
                "Delete Course",
                JOptionPane.PLAIN_MESSAGE,
                null,
                courseNames,
                courseNames[0]
        );

        if (selected != null) {
            courses.remove(selected);
            saveData();
            refreshCourses();
            JOptionPane.showMessageDialog(this, "Course \"" + selected + "\" deleted successfully.");
        }
    }

    // ===== Refresh all course panels =====
    private void refreshCourses() {
        coursesPanel.removeAll();

        for (CourseProgress cp : courses.values()) {
            JPanel courseBox = new JPanel(new BorderLayout());
            courseBox.setBorder(BorderFactory.createTitledBorder(cp.name));
            courseBox.setPreferredSize(new Dimension(550, 200));

            CourseChartPanel chart = new CourseChartPanel(cp);
            courseBox.add(chart, BorderLayout.CENTER);
            coursesPanel.add(courseBox);
        }

        coursesPanel.revalidate();
        coursesPanel.repaint();
        pieChartPanel.repaint();
    }

    // ===== Individual course chart =====
    class CourseChartPanel extends JPanel {
        CourseProgress cp;
        public CourseChartPanel(CourseProgress cp) { this.cp = cp; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            Map<String, Double> data = new LinkedHashMap<>();
            data.put("Lessons", cp.totalLessons > 0 ? cp.completedLessons * 100.0 / cp.totalLessons : 0.0);
            data.put("Tests", cp.testMarksTotal > 0 ? cp.testMarksObtained * 100.0 / cp.testMarksTotal : 0.0);
            data.put("Attendance", cp.totalClasses > 0 ? cp.attendedClasses * 100.0 / cp.totalClasses : 0.0);
            data.put("Assignments", cp.totalAssignments > 0 ? cp.doneAssignments * 100.0 / cp.totalAssignments : 0.0);

            int x = 30, barWidth = 80;
            Random rand = new Random();
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int barHeight = (int)((entry.getValue()/100.0)*(height-50));
                g2.setColor(new Color(rand.nextInt(0xFFFFFF)));
                g2.fill(new Rectangle2D.Double(x, height-30-barHeight, barWidth, barHeight));
                g2.setColor(Color.BLACK);
                g2.drawString(entry.getKey(), x, height-10);
                g2.drawString(String.format("%.0f%%", entry.getValue()), x, height-40-barHeight);
                x += barWidth + 20;
            }
        }
    }

    // ===== Pie chart panel for all courses (course-wise) =====
    class PieChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 100;
            int x = (width - diameter)/2;
            int y = (height - diameter)/2;

            // Sum of all course progresses
            double totalProgressSum = 0;
            for (CourseProgress cp : courses.values()) totalProgressSum += cp.getOverallProgress();

            if (totalProgressSum <= 0) return;

            double startAngle = 0;
            Random rand = new Random();
            for (CourseProgress cp : courses.values()) {
                double portion = cp.getOverallProgress() / totalProgressSum;
                double angle = portion * 360;

                g2.setColor(new Color(rand.nextInt(0xFFFFFF)));
                g2.fill(new Arc2D.Double(x, y, diameter, diameter, startAngle, angle, Arc2D.PIE));

                // Draw label
                double midAngle = Math.toRadians(startAngle + angle/2);
                int labelX = x + diameter/2 + (int)((diameter/2+20) * Math.cos(midAngle));
                int labelY = y + diameter/2 - (int)((diameter/2+20) * Math.sin(midAngle));
                g2.setColor(Color.BLACK);
                g2.drawString(cp.name, labelX-10, labelY);

                startAngle += angle;
            }
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(courses);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            courses = (Map<String, CourseProgress>) ois.readObject();
        } catch (Exception e) {
            courses = new LinkedHashMap<>();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProgressPathFrame("Student1"));
    }
}
