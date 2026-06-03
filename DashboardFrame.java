import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// 🔹 Dashboard Frame
public class DashboardFrame extends JFrame {

    private String username;

    public DashboardFrame(String username) {
        this.username = username;

        setTitle("Dashboard - Welcome, " + username);
        setSize(UIConfig.WINDOW_WIDTH, UIConfig.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Top Bar =====
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 60, 90));
        topBar.setPreferredSize(new Dimension(900, 50));

        JButton menuButton = new JButton("\u2630"); // hamburger
        menuButton.setFont(new Font("Arial", Font.BOLD, 22));
        menuButton.setPreferredSize(new Dimension(60, 40));
        menuButton.addActionListener(e -> showMenu());

        JLabel welcomeLabel = new JLabel("Welcome, " + username, JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        topBar.add(menuButton, BorderLayout.WEST);
        topBar.add(welcomeLabel, BorderLayout.CENTER);

        // ===== Main Panel (6 panels reordered) =====
        JPanel mainPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] names = {
                "Courses", "Notes", "Finance Tracker",
                "Alerts & Reminders", "Progress Path", "Typing Speed Challenge"
        };

        // Optional logos
        String[] logoPaths = {
                "C:\\Users\\ASUS\\Downloads\\course.jpeg",
                "C:/Users/ASUS/Downloads/notes.jpeg",
                "C:\\Users\\ASUS\\Downloads\\finance.jpeg",
                "C:\\Users\\ASUS\\Downloads\\alert.jpeg",
                "C:\\Users\\ASUS\\Downloads\\progress.jpeg",
                "C:\\Users\\ASUS\\Downloads\\type.jpeg"
        };

        for (int i = 0; i < 6; i++) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(230, 240, 255));
            panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            ImageIcon icon = new ImageIcon(logoPaths[i]);
            Image scaledImg = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImg), JLabel.CENTER);

            JLabel textLabel = new JLabel(names[i], JLabel.CENTER);
            textLabel.setFont(new Font("Arial", Font.BOLD, 16));
            textLabel.setForeground(new Color(0, 51, 102));

            panel.add(logoLabel, BorderLayout.CENTER);
            panel.add(textLabel, BorderLayout.SOUTH);

            int index = i;
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (index) {
                        case 0 -> new CourseFrame(username);           // Courses
                        case 1 -> new NotesFrame(username);            // Notes
                        case 2 -> new JFrame_FT().setVisible(true);   // Finance Tracker
                        case 3 -> new AlertsFrame();                   // Alerts & Reminders
                        case 4 -> new ProgressPathFrame(username);    // Progress Path
                        case 5 -> new TypingSpeedGame();              // Typing Speed Game
                    }
                }
            });
            mainPanel.add(panel);
        }

        add(topBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void showMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem homeItem = new JMenuItem("Home");
        JMenuItem infoItem = new JMenuItem("Information");
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem logoutItem = new JMenuItem("Logout");

        homeItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "You are already on Home!"));
        infoItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "App: Student Dashboard\nVersion: 1.0\nLogged in as: " + username));
        settingsItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Settings window will open here."));
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        menu.add(homeItem);
        menu.add(infoItem);
        menu.add(settingsItem);
        menu.addSeparator();
        menu.add(logoutItem);

        menu.show(this, 50, 50);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame("Student1"));
    }
}
