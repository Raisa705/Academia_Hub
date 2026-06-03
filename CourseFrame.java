import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Complete CourseFrame with syllabus upload/view/delete and persistent storage.
 * OOP concepts used:
 * - Abstraction: CourseBase
 * - Encapsulation: Course private fields + getters/setters
 * - Inheritance & Polymorphism: CourseStorage (abstract) -> SerializedCourseStorage
 * - UI uses CourseStorage reference (polymorphism)
 */
public class CourseFrame extends JFrame {
    // ======= Abstraction =======
    public static abstract class CourseBase implements Serializable {
        private static final long serialVersionUID = 1L;
        protected String code, title, credit, classHours, semester;

        public CourseBase(String code, String title, String credit, String classHours, String semester) {
            this.code = code;
            this.title = title;
            this.credit = credit;
            this.classHours = classHours;
            this.semester = semester;
        }

        public abstract String getDetails();
    }

    // ======= Encapsulation =======
    public static class Course extends CourseBase {
        private static final long serialVersionUID = 2L;
        private String syllabusPath; // absolute path to uploaded syllabus (can be pdf, image, doc, txt)

        public Course(String code, String title, String credit, String classHours, String semester) {
            super(code, title, credit, classHours, semester);
        }

        public String getCode() { return code; }
        public String getTitle() { return title; }
        public String getCredit() { return credit; }
        public String getClassHours() { return classHours; }
        public String getSemester() { return semester; }

        public String getSyllabusPath() { return syllabusPath; }
        public void setSyllabusPath(String path) { this.syllabusPath = path; }

        @Override
        public String getDetails() {
            return semester + "|" + code + "|" + title + "|" + credit + "|" + classHours + "|" + (syllabusPath != null ? syllabusPath : "N/A");
        }

        @Override
        public String toString() {
            return code + " - " + title;
        }
    }

    // ======= Storage abstraction =======
    public static abstract class CourseStorage {
        protected final File file;
        public CourseStorage(String filename) { this.file = new File(filename); }
        public abstract void save(ArrayList<Course> courses) throws IOException;
        public abstract ArrayList<Course> load() throws IOException, ClassNotFoundException;
    }

    // ======= Serialized storage (inheritance + polymorphism) =======
    public static class SerializedCourseStorage extends CourseStorage {
        public SerializedCourseStorage(String filename) { super(filename); }

        @Override
        public void save(ArrayList<Course> courses) throws IOException {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(courses);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public ArrayList<Course> load() throws IOException, ClassNotFoundException {
            if (!file.exists()) return new ArrayList<>();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof ArrayList) return (ArrayList<Course>) obj;
                else return new ArrayList<>();
            }
        }
    }

    // ======= UI fields =======
    private final DefaultTableModel tableModel;
    private final JTable courseTable;
    private final ArrayList<Course> courses = new ArrayList<>();
    private final CourseStorage storage;

    private final JTextField codeField = new JTextField();
    private final JTextField titleField = new JTextField();
    private final JTextField creditField = new JTextField();
    private final JTextField classField = new JTextField();
    private final JComboBox<String> semesterBox = new JComboBox<>(new String[]{
            "1st Semester","2nd Semester","3rd Semester","4th Semester",
            "5th Semester","6th Semester","7th Semester","8th Semester"
    });

    // ======= Constructor =======
    public CourseFrame(String username) {
        // storage polymorphism
        this.storage = new SerializedCourseStorage("courses_" + username + ".dat");

        setTitle(username + " - Courses");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(245, 245, 250));

        // Top title
        JLabel topLabel = new JLabel("📘 Courses", SwingConstants.CENTER);
        topLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        topLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topLabel, BorderLayout.NORTH);

        // Table
        String[] cols = {"Code", "Title", "Credit", "Class Hrs", "Semester", "Syllabus"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(26);
        courseTable.setFillsViewportHeight(true);

        // header style
        JTableHeader header = courseTable.getTableHeader();
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < courseTable.getColumnCount(); i++) {
            courseTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(courseTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        add(scroll, BorderLayout.CENTER);

        // Input + buttons panel
        JPanel bottom = new JPanel(new BorderLayout(8,8));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bottom.setBackground(new Color(245, 245, 250));

        JPanel input = new JPanel(new GridLayout(2, 5, 8, 8));
        input.setBackground(new Color(245, 245, 250));

        input.add(new JLabel("Code:"));
        input.add(new JLabel("Title:"));
        input.add(new JLabel("Credit:"));
        input.add(new JLabel("Class Hrs:"));
        input.add(new JLabel("Semester:"));

        input.add(codeField);
        input.add(titleField);
        input.add(creditField);
        input.add(classField);
        input.add(semesterBox);

        bottom.add(input, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setBackground(new Color(245, 245, 250));

        JButton addBtn = styledButton("✅ Add Course", new Color(0,153,76));
        JButton uploadBtn = styledButton("📂 Upload Syllabus", new Color(0,122,204));
        JButton viewBtn = styledButton("👁 View Syllabus", new Color(0,102,153));
        JButton delSylBtn = styledButton("❌ Delete Syllabus", new Color(204,51,51));
        JButton delCourseBtn = styledButton("🗑 Delete Course", new Color(153,51,51));
        JButton refreshBtn = styledButton("🔄 Refresh", new Color(100,100,100));

        actions.add(refreshBtn);
        actions.add(addBtn);
        actions.add(uploadBtn);
        actions.add(viewBtn);
        actions.add(delSylBtn);
        actions.add(delCourseBtn);

        bottom.add(actions, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        // Load saved courses
        loadFromStorage();
        refreshTable();

        // ===== Action listeners =====
        addBtn.addActionListener(e -> {
            addCourseAction();
        });

        uploadBtn.addActionListener(e -> {
            int row = courseTable.getSelectedRow();
            if (row == -1) { showMsg("Select a course (row) first."); return; }
            uploadSyllabusAction(row);
        });

        viewBtn.addActionListener(e -> {
            int row = courseTable.getSelectedRow();
            if (row == -1) { showMsg("Select a course (row) first."); return; }
            viewSyllabusAction(row);
        });

        delSylBtn.addActionListener(e -> {
            int row = courseTable.getSelectedRow();
            if (row == -1) { showMsg("Select a course (row) first."); return; }
            deleteSyllabusAction(row);
        });

        delCourseBtn.addActionListener(e -> {
            int row = courseTable.getSelectedRow();
            if (row == -1) { showMsg("Select a course (row) first."); return; }
            deleteCourseAction(row);
        });

        refreshBtn.addActionListener(e -> {
            loadFromStorage();
            refreshTable();
        });

        // Double-click to open syllabus if exists
        courseTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = courseTable.getSelectedRow();
                    if (row != -1) viewSyllabusAction(row);
                }
            }
        });

        setVisible(true);
    }

    // ===== Helper UI methods =====
    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    // ===== Actions =====
    private void addCourseAction() {
        String code = codeField.getText().trim();
        String title = titleField.getText().trim();
        String credit = creditField.getText().trim();
        String cls = classField.getText().trim();
        String sem = (String) semesterBox.getSelectedItem();

        if (code.isEmpty() || title.isEmpty() || credit.isEmpty() || cls.isEmpty()) {
            showMsg("Fill all fields before adding.");
            return;
        }

        Course c = new Course(code, title, credit, cls, sem);
        courses.add(c);
        saveToStorage();
        refreshTable();

        codeField.setText(""); titleField.setText(""); creditField.setText(""); classField.setText("");
    }

    private void uploadSyllabusAction(int row) {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            Course c = courses.get(row);
            c.setSyllabusPath(f.getAbsolutePath());
            saveToStorage();
            refreshTable();
            showMsg("Syllabus uploaded.");
        }
    }

    private void viewSyllabusAction(int row) {
        Course c = courses.get(row);
        String p = c.getSyllabusPath();
        if (p == null) { showMsg("No syllabus uploaded for this course."); return; }
        Path path = Path.of(p);
        if (!Files.exists(path)) { showMsg("Syllabus file not found (maybe moved or deleted)."); return; }
        if (!Desktop.isDesktopSupported()) { showMsg("Desktop operations not supported on this system."); return; }
        try {
            Desktop.getDesktop().open(path.toFile());
        } catch (IOException ex) {
            showMsg("Unable to open the syllabus file: " + ex.getMessage());
        }
    }

    private void deleteSyllabusAction(int row) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete syllabus for selected course?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        Course c = courses.get(row);
        c.setSyllabusPath(null);
        saveToStorage();
        refreshTable();
        showMsg("Syllabus removed.");
    }

    private void deleteCourseAction(int row) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected course (this will remove syllabus link too)?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        courses.remove(row);
        saveToStorage();
        refreshTable();
    }

    // ===== Storage helpers =====
    private void saveToStorage() {
        try {
            // use storage reference (polymorphism)
            if (storage instanceof SerializedCourseStorage) {
                storage.save(courses);
            } else {
                storage.save(courses);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showMsg("Failed to save courses: " + ex.getMessage());
        }
    }

    private void loadFromStorage() {
        try {
            ArrayList<Course> loaded = storage.load();
            courses.clear();
            courses.addAll(loaded);
        } catch (Exception ex) {
            // if load fails, start fresh (but don't crash)
            courses.clear();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Course c : courses) {
            tableModel.addRow(new Object[]{
                    safe(c.getCode()),
                    safe(c.getTitle()),
                    safe(c.getCredit()),
                    safe(c.getClassHours()),
                    safe(c.getSemester()),
                    (c.getSyllabusPath() != null ? "✔ Uploaded" : "❌ None")
            });
        }
    }

    private String safe(String s) { return s == null ? "" : s; }

    // ===== main for quick test =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CourseFrame("student1"));
    }
}
