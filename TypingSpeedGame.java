import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.text.*;

abstract class TypingGameFrame extends JFrame {
    abstract void startGame();
    abstract void endGame();
}

public class TypingSpeedGame extends TypingGameFrame {
    private JTextPane sentencePane;
    private JTextField inputField;
    private JLabel wpmLabel, accuracyLabel, timerLabel;
    private String sentence;
    private Timer timer;
    private int seconds = 0;
    private int totalTyped = 0;
    private int correctTyped = 0;

    private static final String[] SENTENCES = {
        "The quick brown fox jumps over the lazy dog!",
        "Hello, World! Welcome to Java Swing typing game.",
        "Practice makes perfect: typing fast & accurate.",
        "Symbols like @, #, $, %, & should also be typed correctly.",
        "Longer sentences will challenge your speed and accuracy!"
    };

    public TypingSpeedGame() {
        setTitle("Typing Speed Game");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Top stats panel =====
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        wpmLabel = new JLabel("WPM: 0");
        accuracyLabel = new JLabel("Accuracy: 100%");
        timerLabel = new JLabel("Time: 0s");
        statsPanel.add(wpmLabel);
        statsPanel.add(accuracyLabel);
        statsPanel.add(timerLabel);
        add(statsPanel, BorderLayout.NORTH);

        // ===== Sentence panel =====
        sentencePane = new JTextPane();
        sentencePane.setEditable(false);
        sentencePane.setFont(new Font("Consolas", Font.PLAIN, 22));
        JScrollPane scroll = new JScrollPane(sentencePane);
        add(scroll, BorderLayout.CENTER);

        // ===== Input field =====
        inputField = new JTextField();
        inputField.setFont(new Font("Consolas", Font.PLAIN, 22));
        add(inputField, BorderLayout.SOUTH);

        // ===== Timer =====
        timer = new Timer(1000, e -> {
            seconds++;
            timerLabel.setText("Time: " + seconds + "s");
            updateWPM();
        });

        startGame();
        setVisible(true);
    }

    @Override
    public void startGame() {
        // Random sentence
        sentence = SENTENCES[new Random().nextInt(SENTENCES.length)];
        sentencePane.setText(sentence);
        inputField.setText("");
        seconds = 0;
        totalTyped = 0;
        correctTyped = 0;
        timer.start();

        // Input typing
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkTyping();
            }
        });
    }

    @Override
    public void endGame() {
        timer.stop();
        inputField.setEditable(false);
        JOptionPane.showMessageDialog(this,
                "Finished!\nWPM: " + calculateWPM() + "\nAccuracy: " + calculateAccuracy() + "%");
    }

    private void checkTyping() {
        String typed = inputField.getText();
        totalTyped = typed.length();
        correctTyped = 0;

        StyledDocument doc = sentencePane.getStyledDocument();
        StyleContext sc = StyleContext.getDefaultStyleContext();
        Style defaultStyle = sc.addStyle("default", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);

        Style correctStyle = sc.addStyle("correct", null);
        StyleConstants.setForeground(correctStyle, Color.GREEN);

        Style wrongStyle = sc.addStyle("wrong", null);
        StyleConstants.setForeground(wrongStyle, Color.RED);

        // Clear the pane
        sentencePane.setText("");

        for (int i = 0; i < sentence.length(); i++) {
            char c = sentence.charAt(i);
            Style styleToUse = defaultStyle;
            if (i < typed.length()) {
                if (typed.charAt(i) == c) {
                    styleToUse = correctStyle;
                    correctTyped++;
                } else {
                    styleToUse = wrongStyle;
                }
            }
            try {
                doc.insertString(doc.getLength(), String.valueOf(c), styleToUse);
            } catch (BadLocationException ex) { ex.printStackTrace(); }
        }

        updateWPM();
        updateAccuracy();

        if (typed.equals(sentence)) {
            endGame();
        }
    }

    private void updateWPM() {
        wpmLabel.setText("WPM: " + calculateWPM());
    }

    private void updateAccuracy() {
        accuracyLabel.setText("Accuracy: " + calculateAccuracy() + "%");
    }

    private int calculateWPM() {
        double minutes = seconds / 60.0;
        if (minutes == 0) return 0;
        return (int)((correctTyped / 5.0) / minutes);
    }

    private int calculateAccuracy() {
        if (totalTyped == 0) return 100;
        return (int)((correctTyped * 100.0) / totalTyped);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TypingSpeedGame::new);
    }
}
