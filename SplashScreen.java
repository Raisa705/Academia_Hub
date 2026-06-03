import javax.swing.*;
import java.awt.*;  
import java.awt.event.*;


public class SplashScreen extends JWindow{
     public SplashScreen() {
        // ✅ Apply border style
        UIStyle.applyWindowBorder(this);

        // Load logo
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\ASUS\\Downloads\\logo1.jpeg");
        Image img = logoIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(img);

        JLabel logoLabel = new JLabel(logoIcon, JLabel.CENTER);
        getContentPane().add(logoLabel, BorderLayout.CENTER);

        // Window size
        setSize(UIConfig.WINDOW_WIDTH, UIConfig.WINDOW_HEIGHT);
        setLocationRelativeTo(null); // center of screen
        setVisible(true);

        // Timer to close splash after 3 seconds and open Login
        Timer t = new Timer(3000, e -> {
            dispose();            // close splash
            new LoginFrame();     // open login window
        });
        t.setRepeats(false); // ensure it fires only once
        t.start();
    }
}
