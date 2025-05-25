package mvcstockesystem.c;

import javax.swing.*;

import mvcstockesystem.m.DivisionSelectorApp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField databaseField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginForm() {
        setTitle("Database Login");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Form layout
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Username:"));
        usernameField = new JTextField("root"); // default value
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField(""); // set your default if desired
        add(passwordField);

        add(new JLabel("Database name:"));
        databaseField = new JTextField("warehouse");
        add(databaseField);

        loginButton = new JButton("Login");
        add(loginButton);

        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statusLabel);

        // Fill empty cells
        add(new JLabel());
        add(new JLabel());

        loginButton.addActionListener(new LoginAction());

        setVisible(true);
    }

    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String user = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String dbName = databaseField.getText();

            String url = "jdbc:mysql://localhost:3306/" + dbName;

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                 statusLabel.setText("✅ Login successful!");
                 JOptionPane.showMessageDialog(LoginForm.this, "Connected to " + dbName + " successfully!");

                 DBConfig config = new DBConfig(user, password, dbName);
                 dispose(); // close login form
                 DivisionSelectorApp.launch(config); // pass DB config to DivisionSelectorApp
               
            } catch (SQLException ex) {
                statusLabel.setText("❌ Login failed!");
                JOptionPane.showMessageDialog(LoginForm.this, "Connection failed:\n" + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
    }
}
