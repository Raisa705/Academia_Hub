import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class NotesFrame extends JFrame {

    private JTextField courseField, searchField;
    private JButton addCourseBtn, searchBtn, deleteCourseBtn;
    private JPanel coursesPanel;
    private JScrollPane scrollPane;

    private List<CourseNotes> courses;
    private String username;
    private File dataFile;

    // ======= Inner Classes =======
    public static class Note implements Serializable {
        private static final long serialVersionUID = 1L;
        public String title;
        public String content;   // typed notes
        public File file;        // uploaded files

        public Note(String title, String content, File file) {
            this.title = title;
            this.content = content;
            this.file = file;
        }
        public boolean isFile() { return file != null; }
    }

    public static class CourseNotes implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private List<Note> notes;
        public CourseNotes(String name) { this.name = name; notes = new ArrayList<>(); }
        public String getName() { return name; }
        public List<Note> getNotes() { return notes; }
        public void addNote(Note n) { notes.add(n); }
    }

    // ======= Constructor =======
    public NotesFrame(String username) {
        this.username = username;
        this.dataFile = new File("notes_" + username + ".dat");
        this.courses = loadCourses();

        setTitle(username + " - Notes");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Top Panel =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245,245,245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Headline
        JLabel headline = new JLabel("📝 Notes", JLabel.CENTER);
        headline.setFont(new Font("Arial", Font.BOLD, 24));
        headline.setForeground(new Color(0,51,153));
        topPanel.add(headline, BorderLayout.CENTER);

        // Course input + Search panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        inputPanel.setBackground(new Color(245,245,245));

        courseField = new JTextField(); 
        courseField.setPreferredSize(new Dimension(150,25));
        inputPanel.add(courseField);

        addCourseBtn = new JButton("Add Course");
        inputPanel.add(addCourseBtn);

        deleteCourseBtn = new JButton("Delete Course");
        inputPanel.add(deleteCourseBtn);

        searchField = new JTextField(); 
        searchField.setPreferredSize(new Dimension(200,25));
        inputPanel.add(searchField);

        searchBtn = new JButton("Search");
        inputPanel.add(searchBtn);

        topPanel.add(inputPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ===== Courses Panel (cards) =====
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        scrollPane = new JScrollPane(coursesPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refreshCoursesPanel();

        // ===== Button Actions =====
        addCourseBtn.addActionListener(e -> {
            String cname = courseField.getText().trim();
            if(!cname.isEmpty() && getCourse(cname)==null) {
                courses.add(new CourseNotes(cname));
                refreshCoursesPanel();
                courseField.setText("");
                saveCourses();
            }
        });

        deleteCourseBtn.addActionListener(e -> {
            String cname = JOptionPane.showInputDialog(this, "Enter course name to delete:");
            if(cname != null && !cname.trim().isEmpty()) {
                CourseNotes target = getCourse(cname.trim());
                if(target != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to delete \"" + target.getName() + "\"?",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if(confirm == JOptionPane.YES_OPTION) {
                        courses.remove(target);
                        saveCourses();
                        refreshCoursesPanel();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Course not found!");
                }
            }
        });

        searchBtn.addActionListener(e -> searchCourses());

        setVisible(true);
    }

    private void searchCourses(){
        String query = searchField.getText().trim().toLowerCase();
        coursesPanel.removeAll();
        for(CourseNotes c: courses){
            if(c.getName().toLowerCase().contains(query)) addCourseCard(c);
        }
        coursesPanel.revalidate();
        coursesPanel.repaint();
    }

    private void refreshCoursesPanel(){
        coursesPanel.removeAll();
        for(CourseNotes c: courses) addCourseCard(c);
        coursesPanel.revalidate();
        coursesPanel.repaint();
    }

    private void addCourseCard(CourseNotes c){
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBackground(new Color(new Random().nextInt(0xFFFFFF))); // random color
        card.setPreferredSize(new Dimension(200,150));
        card.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JLabel title = new JLabel(c.getName());
        title.setFont(new Font("Arial",Font.BOLD,16));
        title.setForeground(Color.WHITE);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel notesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        notesPanel.setOpaque(false);
        for(Note n: c.getNotes()){
            JButton nb = new JButton(n.title);
            nb.setBackground(new Color(255,255,255,200));
            nb.setFocusPainted(false);
            nb.addActionListener(e -> openNote(n));
            notesPanel.add(nb);
        }

        JButton addNoteBtn = new JButton("+ Note");
        addNoteBtn.setBackground(new Color(255,255,255,200));
        addNoteBtn.addActionListener(e -> addNoteToCourse(c));
        notesPanel.add(addNoteBtn);

        card.add(notesPanel);
        coursesPanel.add(card);
    }

    private void addNoteToCourse(CourseNotes c){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        JTextArea ta = new JTextArea(8,30);
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(ta);
        panel.add(new JLabel("Write Note (leave empty if uploading file):"));
        panel.add(sp);
        JButton uploadBtn = new JButton("Upload File");
        panel.add(uploadBtn);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Note", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String typed = ta.getText().trim();

        File uploadedFile = null;
        if(result==JOptionPane.OK_OPTION) {
            JFileChooser chooser = new JFileChooser();
            int r = chooser.showOpenDialog(this);
            if(r==JFileChooser.APPROVE_OPTION) uploadedFile = chooser.getSelectedFile();

            if(!typed.isEmpty() || uploadedFile!=null){
                String title = typed.isEmpty() ? uploadedFile.getName() : c.getName()+"_note_"+(c.getNotes().size()+1);
                c.addNote(new Note(title, typed.isEmpty()?null:typed, uploadedFile));
                saveCourses();
                refreshCoursesPanel();
            }
        }
    }

    private void openNote(Note n){
        try{
            if(n.isFile()) Desktop.getDesktop().open(n.file);
            else {
                JTextArea ta = new JTextArea(n.content);
                ta.setLineWrap(true); ta.setWrapStyleWord(true);
                ta.setEditable(false);
                JScrollPane sp = new JScrollPane(ta);
                sp.setPreferredSize(new Dimension(500,400));
                JOptionPane.showMessageDialog(this, sp, n.title, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch(Exception e){ JOptionPane.showMessageDialog(this,"Cannot open note/file."); }
    }

    private CourseNotes getCourse(String name){
        for(CourseNotes c: courses) if(c.getName().equals(name)) return c;
        return null;
    }

    private void saveCourses(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))){
            oos.writeObject(courses);
        } catch(Exception e){ e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private List<CourseNotes> loadCourses(){
        if(!dataFile.exists()) return new ArrayList<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))){
            return (List<CourseNotes>)ois.readObject();
        } catch(Exception e){ e.printStackTrace(); return new ArrayList<>(); }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new NotesFrame("user1"));
    }
}
