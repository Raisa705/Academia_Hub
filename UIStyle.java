import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.Border;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UIStyle {
    public static Border getWindowBorder() {
        return BorderFactory.createLineBorder(new Color(50, 50, 50), 4); // dark border, 4px
    }

    public static void applyWindowBorder(JFrame frame) {
        ((JPanel) frame.getContentPane()).setBorder(getWindowBorder());
    }

    public static void applyWindowBorder(JWindow window) {
        ((JPanel) window.getContentPane()).setBorder(getWindowBorder());
    }
}
