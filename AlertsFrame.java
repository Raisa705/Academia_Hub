import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

// ===== Abstraction (Abstract Class) =====
abstract class Reminder implements Serializable {
    private String description;
    private LocalDate date;

    public Reminder(String description, LocalDate date) {
        this.description = description;
        this.date = date;
    }

    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
    public abstract String getType();

    @Override
    public String toString() {
        return getType() + ": " + description + " (" + date + ")";
    }
}

// ===== Inheritance =====
class ExamReminder extends Reminder {
    public ExamReminder(String description, LocalDate date) { super(description, date); }
    @Override public String getType() { return "Exam"; }
}

class AssignmentReminder extends Reminder {
    public AssignmentReminder(String description, LocalDate date) { super(description, date); }
    @Override public String getType() { return "Assignment"; }
}

class PresentationReminder extends Reminder {
    public PresentationReminder(String description, LocalDate date) { super(description, date); }
    @Override public String getType() { return "Presentation"; }
}

// ===== Encapsulation (Reminder Manager) =====
class ReminderManager {
    private Map<LocalDate, java.util.List<Reminder>> reminders;
    private final String FILE_NAME = "reminders_oop.dat";

    public ReminderManager() {
        reminders = loadReminders();
    }

    public void addReminder(LocalDate date, Reminder reminder) {
        reminders.computeIfAbsent(date, k -> new ArrayList<>()).add(reminder);
        saveReminders();
    }

    public void deleteReminder(LocalDate date, Reminder reminder) {
        if (reminders.containsKey(date)) {
            reminders.get(date).remove(reminder);
            if (reminders.get(date).isEmpty()) reminders.remove(date);
            saveReminders();
        }
    }

    public java.util.List<Reminder> getReminders(LocalDate date) {
        return reminders.getOrDefault(date, new ArrayList<>());
    }

    private void saveReminders() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(reminders);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private Map<LocalDate, java.util.List<Reminder>> loadReminders() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<LocalDate, java.util.List<Reminder>>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}

// ===== UI Class =====
public class AlertsFrame extends JFrame {
    private MonthCalendarPanel calendarPanel;
    private JPanel reminderPanel;
    private JTextArea reminderArea;
    private ReminderManager reminderManager;

    public AlertsFrame() {
        setTitle("📅 Alerts & Reminders (OOP)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        reminderManager = new ReminderManager();
        calendarPanel = new MonthCalendarPanel(reminderManager);
        add(calendarPanel, BorderLayout.CENTER);

        reminderPanel = new JPanel(new BorderLayout());
        reminderPanel.setPreferredSize(new Dimension(300, 0));
        reminderArea = new JTextArea();
        reminderArea.setEditable(false);
        reminderArea.setBackground(new Color(245, 255, 250));
        reminderArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reminderPanel.add(new JLabel("📌 Reminders for selected date:"), BorderLayout.NORTH);
        reminderPanel.add(new JScrollPane(reminderArea), BorderLayout.CENTER);
        add(reminderPanel, BorderLayout.EAST);

        showTomorrowReminders();
        setVisible(true);
    }

    private void showTomorrowReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        java.util.List<Reminder> tomorrowReminders = reminderManager.getReminders(tomorrow);
        if (!tomorrowReminders.isEmpty()) {
            StringBuilder sb = new StringBuilder("⏰ Reminder for Tomorrow (" + tomorrow + "):\n\n");
            for (Reminder r : tomorrowReminders) {
                sb.append(" - ").append(r).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Tomorrow's Reminders", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ===== Calendar Panel =====
    class MonthCalendarPanel extends JPanel {
        private LocalDate currentMonth;
        private JPanel gridPanel;
        private JLabel monthLabel;
        private ReminderManager reminderManager;

        public MonthCalendarPanel(ReminderManager reminderManager) {
            this.reminderManager = reminderManager;
            currentMonth = LocalDate.now().withDayOfMonth(1);
            setLayout(new BorderLayout());

            JPanel header = new JPanel(new BorderLayout());
            JButton prevBtn = new JButton("⬅");
            JButton nextBtn = new JButton("➡");
            monthLabel = new JLabel("", JLabel.CENTER);
            monthLabel.setFont(new Font("Arial", Font.BOLD, 18));

            prevBtn.addActionListener(e -> {
                currentMonth = currentMonth.minusMonths(1);
                refreshCalendar();
            });
            nextBtn.addActionListener(e -> {
                currentMonth = currentMonth.plusMonths(1);
                refreshCalendar();
            });

            header.add(prevBtn, BorderLayout.WEST);
            header.add(monthLabel, BorderLayout.CENTER);
            header.add(nextBtn, BorderLayout.EAST);
            add(header, BorderLayout.NORTH);

            gridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
            add(gridPanel, BorderLayout.CENTER);

            refreshCalendar();
        }

        private void refreshCalendar() {
            gridPanel.removeAll();
            monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());

            LocalDate firstDay = currentMonth;
            int firstDayOfWeek = firstDay.getDayOfWeek().getValue();
            int length = currentMonth.lengthOfMonth();

            for (int i = 1; i < firstDayOfWeek; i++) gridPanel.add(new JLabel(""));

            for (int day = 1; day <= length; day++) {
                LocalDate date = currentMonth.withDayOfMonth(day);
                JButton dayBtn = new JButton(String.valueOf(day));
                if (!reminderManager.getReminders(date).isEmpty()) {
                    dayBtn.setBackground(new Color(255, 228, 196));
                }
                dayBtn.addActionListener(e -> showReminders(date));
                gridPanel.add(dayBtn);
            }

            revalidate();
            repaint();
        }

        private void showReminders(LocalDate date) {
            java.util.List<Reminder> dayReminders = reminderManager.getReminders(date);
            StringBuilder sb = new StringBuilder("📅 " + date + ":\n");
            for (Reminder r : dayReminders) sb.append("- ").append(r).append("\n");
            reminderArea.setText(sb.toString());

            // Dialog with Add / Delete options
            String[] actions = {"Add Reminder", "Delete Reminder"};
            String action = (String) JOptionPane.showInputDialog(
                    AlertsFrame.this,
                    "Choose an action:",
                    "Manage Reminders",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    actions,
                    actions[0]
            );

            if (action == null) return;

            if (action.equals("Add Reminder")) {
                String[] types = {"Exam", "Assignment", "Presentation"};
                String type = (String) JOptionPane.showInputDialog(
                        AlertsFrame.this,
                        "Choose type of reminder:",
                        "Add Reminder",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        types,
                        types[0]
                );

                if (type != null) {
                    String desc = JOptionPane.showInputDialog("Enter " + type + " details:");
                    if (desc != null && !desc.trim().isEmpty()) {
                        Reminder newReminder;
                        switch (type) {
                            case "Exam": newReminder = new ExamReminder(desc, date); break;
                            case "Assignment": newReminder = new AssignmentReminder(desc, date); break;
                            case "Presentation": newReminder = new PresentationReminder(desc, date); break;
                            default: return;
                        }
                        reminderManager.addReminder(date, newReminder);
                        refreshCalendar();
                        showReminders(date);
                    }
                }
            } else if (action.equals("Delete Reminder")) {
                if (dayReminders.isEmpty()) {
                    JOptionPane.showMessageDialog(AlertsFrame.this, "No reminders to delete for this date.");
                    return;
                }
                Reminder selected = (Reminder) JOptionPane.showInputDialog(
                        AlertsFrame.this,
                        "Select reminder to delete:",
                        "Delete Reminder",
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        dayReminders.toArray(),
                        dayReminders.get(0)
                );
                if (selected != null) {
                    reminderManager.deleteReminder(date, selected);
                    JOptionPane.showMessageDialog(AlertsFrame.this, "Reminder deleted successfully!");
                    refreshCalendar();
                    showReminders(date);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AlertsFrame::new);
    }
}
