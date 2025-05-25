package mvcstockesystem.v;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WarehouseInventorySwingApp {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/warehouse";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI("default")); // fallback division
    }

    public static void launchForDivision(String division) {
        SwingUtilities.invokeLater(() -> createAndShowGUI(division));
    }

    public static void createAndShowGUI(String division) {
        JFrame frame = new JFrame("Warehouse Inventory - Division: " + division);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Designation");
        model.addColumn("Quantite");
        model.addColumn("Type");
        model.addColumn("Division");
        model.addColumn("Date d'entre");

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JTextField itemNameField = new JTextField(20);
        JTextField quantityField = new JTextField(10);
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Electronics", "Furniture", "Clothing", "Food", "Toys"});
        JTextField divisionField = new JTextField(division, 10);
        divisionField.setEditable(false);
        JFormattedTextField dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        dateField.setColumns(10);

        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Item");
        JButton updateButton = new JButton("Update Item");

        Font font = new Font("Arial", Font.PLAIN, 18);
        for (JComponent comp : new JComponent[]{itemNameField, quantityField, typeComboBox, divisionField, dateField, addButton, removeButton, updateButton}) {
            comp.setFont(font);
        }

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(new JLabel("Designation:")); inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Quantity:")); inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Item Type:")); inputPanel.add(typeComboBox);
        inputPanel.add(new JLabel("Division:")); inputPanel.add(divisionField);
        inputPanel.add(new JLabel("Date d'entre (yyyy-MM-dd):")); inputPanel.add(dateField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        loadInventoryData(model, division);

        addButton.addActionListener((ActionEvent e) -> {
            String name = itemNameField.getText();
            String quantityStr = quantityField.getText();
            String type = (String) typeComboBox.getSelectedItem();
            String dateStr = dateField.getText();

            if (name.isEmpty() || quantityStr.isEmpty() || dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityStr);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date arrivalDate = sdf.parse(dateStr);
                insertItemIntoDatabase(name, quantity, type, division, sdf.format(arrivalDate));
                model.addRow(new Object[]{name, quantity, type, division, sdf.format(arrivalDate)});
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }

            itemNameField.setText("");
            quantityField.setText("");
            dateField.setText("");
        });

        removeButton.addActionListener((ActionEvent e) -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String name = (String) model.getValueAt(selectedRow, 0);
                deleteItemFromDatabase(name, division);
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to remove.");
            }
        });

        updateButton.addActionListener((ActionEvent e) -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String name = itemNameField.getText();
                String quantityStr = quantityField.getText();
                String type = (String) typeComboBox.getSelectedItem();
                String dateStr = dateField.getText();

                if (name.isEmpty() || quantityStr.isEmpty() || dateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                    return;
                }

                try {
                    int quantity = Integer.parseInt(quantityStr);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date arrivalDate = sdf.parse(dateStr);
                    updateItemInDatabase(name, quantity, type, division, sdf.format(arrivalDate));
                    model.setValueAt(name, selectedRow, 0);
                    model.setValueAt(quantity, selectedRow, 1);
                    model.setValueAt(type, selectedRow, 2);
                    model.setValueAt(division, selectedRow, 3);
                    model.setValueAt(sdf.format(arrivalDate), selectedRow, 4);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }

                itemNameField.setText("");
                quantityField.setText("");
                dateField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to update.");
            }
        });

        frame.setVisible(true);
    }

    private static void loadInventoryData(DefaultTableModel model, String division) {
        String tableName = "inventory_" + division.toLowerCase();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM `" + tableName + "`")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("type"),
                        division,
                        rs.getString("date_of_arrival")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Load error: " + e.getMessage());
        }
    }

    private static void insertItemIntoDatabase(String name, int quantity, String type, String division, String dateOfArrival) {
        String tableName = "inventory_" + division.toLowerCase();
        String query = "INSERT INTO `" + tableName + "` (item_name, quantity, type, date_of_arrival) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, type);
            pstmt.setString(4, dateOfArrival);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Insert error: " + e.getMessage());
        }
    }

    private static void deleteItemFromDatabase(String name, String division) {
        String tableName = "inventory_" + division.toLowerCase();
        String query = "DELETE FROM `" + tableName + "` WHERE item_name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Delete error: " + e.getMessage());
        }
    }

    private static void updateItemInDatabase(String name, int quantity, String type, String division, String dateOfArrival) {
        String tableName = "inventory_" + division.toLowerCase();
        String query = "UPDATE `" + tableName + "` SET quantity = ?, type = ?, date_of_arrival = ? WHERE item_name = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, quantity);
            pstmt.setString(2, type);
            pstmt.setString(3, dateOfArrival);
            pstmt.setString(4, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Update error: " + e.getMessage());
        }
    }
}
