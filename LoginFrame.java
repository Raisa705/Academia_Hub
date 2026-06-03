import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, resetButton, registerButton;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("✨ Login System ✨");
        setSize(UIConfig.WINDOW_WIDTH, UIConfig.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ✅ Background color
        getContentPane().setBackground(new Color(245, 250, 255));
        setLayout(new GridBagLayout());

        // ✅ Apply custom border
        UIStyle.applyWindowBorder(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("🔐 Please Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 60, 114));
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel userLabel = new JLabel("Username: ");
        userLabel.setFont(labelFont);
        add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(fieldFont);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password: ");
        passLabel.setFont(labelFont);
        add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(fieldFont);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        add(passwordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 250, 255));

        loginButton = createStyledButton("Login", new Color(34, 139, 34));
        resetButton = createStyledButton("Reset", new Color(220, 20, 60));
        registerButton = createStyledButton("Register", new Color(65, 105, 225));

        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(statusLabel, gbc);

        setVisible(true);
    }

    // ✅ Helper method for styled button
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            try {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                boolean success = false;

                for (User u : FileManager.loadUsers()) {
                    if (u.checkLogin(username, password)) {
                        success = true;
                        // ✅ Open dashboard window
                        new DashboardFrame(username);
                        dispose(); // Close login window
                        break;
                    }
                }

                if (success) {
                    statusLabel.setText("✅ Login Successful!");
                    statusLabel.setForeground(new Color(0, 128, 0));
                } else {
                    statusLabel.setText("❌ Invalid Username or Password!");
                    statusLabel.setForeground(new Color(178, 34, 34));
                }
            } catch (IOException ex) {
                statusLabel.setText("⚠ Error reading user database!");
                statusLabel.setForeground(Color.RED);
            }
        } else if (e.getSource() == resetButton) {
            usernameField.setText("");
            passwordField.setText("");
            statusLabel.setText("");
        } else if (e.getSource() == registerButton) {
            new RegistrationFrame();
        }
    }
}
