import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LaundryManagementSystem extends JFrame implements ActionListener {
    private JTextField customerNameField;
    private JTextField phoneNumberField;
    private JButton submitButton;

    private Connection connection;
    private PreparedStatement preparedStatement;

    public LaundryManagementSystem() {
        setTitle("Laundry Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        JLabel customerNameLabel = new JLabel("Customer Name:");
        customerNameField = new JTextField(20);

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        phoneNumberField = new JTextField(20);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);

        add(customerNameLabel);
        add(customerNameField);
        add(phoneNumberLabel);
        add(phoneNumberField);
        add(submitButton);

        setVisible(true);

        // Establish database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
            String DB_USERNAME = "root";
            String DB_PASSWORD = "Amit@2508";
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("INSERT INTO customers (name, phone) VALUES (?, ?)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String customerName = customerNameField.getText();
            String phoneNumber = phoneNumberField.getText();

            addDataToDatabase(customerName, phoneNumber);

            customerNameField.setText("");
            phoneNumberField.setText("");
        }
    }

    private void addDataToDatabase(String name, String phone) {
        try {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data added to database successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add data to database");
        }
    }

    public static void main(String[] args) {
        new LaundryManagementSystem();
    }
}
