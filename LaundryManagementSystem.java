import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LaundryManagementSystem extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField bagNumberField;
    private JTextField clothesCountField;
    private JButton submitButton;

    private Connection connection;
    private PreparedStatement preparedStatement;

    public LaundryManagementSystem() {
        setTitle("Laundry Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(50, 120, 220));
        JLabel headerLabel = new JLabel("Laundry Management System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel nameLabel = new JLabel("Name:");
        inputPanel.add(nameLabel, constraints);

        constraints.gridx = 1;
        nameField = new JTextField(20);
        inputPanel.add(nameField, constraints);

        constraints.gridy = 1;
        constraints.gridx = 0;
        JLabel bagNumberLabel = new JLabel("Bag Number:");
        inputPanel.add(bagNumberLabel, constraints);

        constraints.gridx = 1;
        bagNumberField = new JTextField(20);
        inputPanel.add(bagNumberField, constraints);

        constraints.gridy = 2;
        constraints.gridx = 0;
        JLabel clothesCountLabel = new JLabel("No. of Clothes:");
        inputPanel.add(clothesCountLabel, constraints);

        constraints.gridx = 1;
        clothesCountField = new JTextField(20);
        inputPanel.add(clothesCountField, constraints);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        buttonPanel.add(submitButton);

        // Add components to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Establish database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
            String DB_USERNAME = "root";
            String DB_PASSWORD = "Amit@2508";
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("INSERT INTO customers (name, bag_number, clothes_count) VALUES (?, ?, ?)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String name = nameField.getText();
            String bagNumber = bagNumberField.getText();
            String clothesCount = clothesCountField.getText();

            addDataToDatabase(name, bagNumber, clothesCount);

            nameField.setText("");
            bagNumberField.setText("");
            clothesCountField.setText("");
        }
    }

    private void addDataToDatabase(String name, String bagNumber, String clothesCount) {
        try {
            // Check if the student has already submitted a laundry bag this week
            if (hasSubmittedLaundryThisWeek(name)) {
                JOptionPane.showMessageDialog(this, "You have already submitted a laundry bag this week.");
                return;
            }

            // Check if the student has reached the maximum number of laundry bag submissions for the month
            if (hasReachedMaximumSubmissions(name)) {
                JOptionPane.showMessageDialog(this, "You have reached the maximum number of laundry bag submissions for this month.");
                return;
            }

            // Check if the number of clothes exceeds the limit
            int count = Integer.parseInt(clothesCount);
            if (count > 15) {
                JOptionPane.showMessageDialog(this, "Number of clothes should not exceed 15.");
                return;
            }

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, bagNumber);
            preparedStatement.setString(3, clothesCount);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data added to the database successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add data to the database");
        }
    }

    private boolean hasSubmittedLaundryThisWeek(String name) throws SQLException {
        String query = "SELECT COUNT(*) FROM customers WHERE name = ? AND YEARWEEK(date_column) = YEARWEEK(NOW())";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0;
        }
        return false;
    }

    private boolean hasReachedMaximumSubmissions(String name) throws SQLException {
        String query = "SELECT COUNT(*) FROM customers WHERE name = ? AND MONTH(date_column) = MONTH(NOW())";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            int count = rs.getInt(1);
            return count >= 4;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LaundryManagementSystem();
    }
}
