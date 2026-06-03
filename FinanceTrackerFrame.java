import javax.swing.*;
import java.awt.*;

public class FinanceTrackerFrame extends JFrame {
    public FinanceTrackerFrame() {
        setTitle("Finance Tracker");
        setSize(UIConfig.WINDOW_WIDTH, UIConfig.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("💰 Finance Tracker Window", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));

        add(label, BorderLayout.CENTER);
        setVisible(true);
    }
}
