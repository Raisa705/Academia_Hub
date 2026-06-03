import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.Vector;

public class JFrame_FT extends JFrame {

    private JButton jButton1, jButton2, jButton3, jButton4;
    private JComboBox<String> jComboBox1;
    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JTable jTable1;
    private JTextField jTextField1, jTextField2, jTextField3;

    private static final String FILE_NAME = "finance_data.csv";

    public JFrame_FT() {
        initComponents();
        loadDataFromFile();  // load saved data at startup
    }

    private void initComponents() {
        jLabel1 = new JLabel("Finance Tracker", JLabel.CENTER);
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 28));
        jLabel1.setForeground(new Color(0, 102, 204));

        jLabel2 = new JLabel("Fee Type :");
        jLabel3 = new JLabel("Date :");
        jLabel4 = new JLabel("Cost :");

        for (JLabel lbl : new JLabel[]{jLabel2, jLabel3, jLabel4}) {
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            lbl.setForeground(new Color(0, 51, 102));
        }

        jComboBox1 = new JComboBox<>(new String[]{"Course Fee", "Hall Fee", "Transport Fee", "Others Fee"});
        jTextField1 = new JTextField();
        jTextField2 = new JTextField();

        jTable1 = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"Fee Type", "Date", "Cost"}));
        jTable1.setRowHeight(25);
        jTable1.setGridColor(Color.BLUE);
        jTable1.setSelectionBackground(new Color(0, 102, 204));
        jTable1.setSelectionForeground(Color.WHITE);
        jScrollPane1 = new JScrollPane(jTable1);

        jButton1 = new JButton("Add");
        jButton2 = new JButton("Delete");
        jButton3 = new JButton("Clear");
        jButton4 = new JButton("Search");

        styleButton(jButton1, new Color(0, 153, 76));  // green
        styleButton(jButton2, new Color(204, 0, 0));   // red
        styleButton(jButton3, new Color(255, 153, 0)); // orange
        styleButton(jButton4, new Color(0, 102, 204)); // blue

        jLabel5 = new JLabel("Search:");
        jLabel5.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel5.setForeground(new Color(0, 51, 102));

        jTextField3 = new JTextField(12);

        jPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jPanel1.add(jLabel5);
        jPanel1.add(jTextField3);
        jPanel1.add(jButton4);

        jLabel6 = new JLabel("Total Amount: $0.00");
        jLabel6.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jLabel6.setForeground(new Color(0, 102, 0));

        // 🔹 Layout
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(20)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                            .addGap(10)
                            .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addGap(30)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(20, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                    .addGap(250)
                    .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(20)
                    .addComponent(jLabel1)
                    .addGap(20)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(5)
                            .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addGap(15)
                            .addComponent(jLabel3)
                            .addGap(5)
                            .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addGap(15)
                            .addComponent(jLabel4)
                            .addGap(5)
                            .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addGap(15)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
                            .addGap(15)
                            .addComponent(jButton2, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                            .addGap(20)
                            .addComponent(jLabel6))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                            .addGap(10)
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(30, Short.MAX_VALUE))
        );

        setTitle("Finance Tracker");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // only close this window
        setSize(900, 600);
        setLocationRelativeTo(null);

        // 🔹 Button Actions
        jButton1.addActionListener(evt -> addRecord());
        jButton2.addActionListener(evt -> deleteRecord());
        jButton3.addActionListener(evt -> clearFields());
        jButton4.addActionListener(evt -> searchRecord());

        // 🔹 Save when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveDataToFile();
            }
        });
    }

    private void styleButton(JButton button, Color bg) {
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void addRecord() {
        String feeType = (String) jComboBox1.getSelectedItem();
        String date = jTextField1.getText().trim();
        String costStr = jTextField2.getText().trim();

        if (date.isEmpty() || costStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date and Cost cannot be empty!");
            return;
        }

        double cost;
        try {
            cost = Double.parseDouble(costStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cost must be a number!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.addRow(new Object[]{feeType, date, cost});
        clearFields();
        updateTotal();
    }

    private void deleteRecord() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0) {
            model.removeRow(selectedRow);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to delete!");
        }
    }

    private void clearFields() {
        jTextField1.setText("");
        jTextField2.setText("");
        jComboBox1.setSelectedIndex(0);
        jTextField3.setText("");
        jTable1.setRowSorter(null); // reset search
    }

    private void searchRecord() {
        String query = jTextField3.getText().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        jTable1.setRowSorter(sorter);

        if (query.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void updateTotal() {
        double total = 0;
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            total += Double.parseDouble(model.getValueAt(i, 2).toString());
        }
        jLabel6.setText("Total Amount: $" + total);
    }

    // 🔹 Save data to CSV file
    private void saveDataToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                writer.println(
                    model.getValueAt(i, 0) + "," +
                    model.getValueAt(i, 1) + "," +
                    model.getValueAt(i, 2)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Load data from CSV file
    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    model.addRow(new Object[]{parts[0], parts[1], Double.parseDouble(parts[2])});
                }
            }
            updateTotal();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JFrame_FT().setVisible(true));
    }
}
