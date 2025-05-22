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

    // MySQL database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/warehouse";
    private static final String DB_USER = ""; // your MySQL username
    private static final String DB_PASSWORD = ""; // your MySQL password

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WarehouseInventorySwingApp::createAndShowGUI);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Warehouse Inventory Management");
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
        itemNameField.setFont(new Font("Arial", Font.PLAIN, 18));

        JTextField quantityField = new JTextField(10);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 18));

        String[] itemTypes = {"Electronics", "Furniture", "Clothing", "Food", "Toys"};
        JComboBox<String> typeComboBox = new JComboBox<>(itemTypes);
        typeComboBox.setFont(new Font("Arial", Font.PLAIN, 18));

        JTextField devisionField = new JTextField(10);
        devisionField.setFont(new Font("Arial", Font.PLAIN, 18));

        JFormattedTextField dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        dateField.setColumns(10);
        dateField.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Item");
        JButton updateButton = new JButton("Update Item");

        addButton.setFont(new Font("Arial", Font.PLAIN, 18));
        removeButton.setFont(new Font("Arial", Font.PLAIN, 18));
        updateButton.setFont(new Font("Arial", Font.PLAIN, 18));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(new JLabel("Designation:"));
        inputPanel.add(itemNameField);
        inputPanel.add(Box.createVerticalStrut(10));

        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(Box.createVerticalStrut(10));

        inputPanel.add(new JLabel("Item Type:"));
        inputPanel.add(typeComboBox);
        inputPanel.add(Box.createVerticalStrut(10));
        
        inputPanel.add(new JLabel("devision:"));
        inputPanel.add(devisionField);
        inputPanel.add(Box.createVerticalStrut(10));

        inputPanel.add(new JLabel("Date d'entre:"));
        inputPanel.add(dateField);
        inputPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        loadInventoryData(model);

        addButton.addActionListener((ActionEvent e) -> {
            String name = itemNameField.getText();
            String quantityStr = quantityField.getText();
            String type = (String) typeComboBox.getSelectedItem();
            String devision = devisionField.getText();
            String dateStr = dateField.getText();

            if (name.isEmpty() || quantityStr.isEmpty() || dateStr.isEmpty() || devision.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityStr);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date arrivalDate = sdf.parse(dateStr);

                insertItemIntoDatabase(name, quantity, type, devision , sdf.format(arrivalDate));
                model.addRow(new Object[]{name, quantity, type, devision ,sdf.format(arrivalDate)});
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid number format.");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Use yyyy-MM-dd.");
            }

            itemNameField.setText("");
            quantityField.setText("");
            dateField.setText("");
        });

        removeButton.addActionListener((ActionEvent e) -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String itemName = (String) model.getValueAt(selectedRow, 0);
                deleteItemFromDatabase(itemName);
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
                String division = devisionField.getText();
                String dateStr = dateField.getText();

                if (name.isEmpty() || quantityStr.isEmpty() || dateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                    return;
                }

                try {
                    int quantity = Integer.parseInt(quantityStr);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date arrivalDate = sdf.parse(dateStr);

                    updateItemInDatabase(name, quantity, type, division ,sdf.format(arrivalDate));
                    model.setValueAt(name, selectedRow, 0);
                    model.setValueAt(quantity, selectedRow, 1);
                    model.setValueAt(type, selectedRow, 2);
                    model.setValueAt(division, selectedRow, 3);
                    model.setValueAt(sdf.format(arrivalDate), selectedRow, 4);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number format.");
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Use yyyy-MM-dd.");
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

    private static void loadInventoryData(DefaultTableModel model) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM inventory")) {

            while (rs.next()) {
                String name = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                String type = rs.getString("type");
                String dateOfArrival = rs.getString("date_of_arrival");
                String division = rs.getString("division");
                
                model.addRow(new Object[]{name, quantity, type, division,dateOfArrival});
                
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Already good
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
        
    }

    private static void insertItemIntoDatabase(String name, int quantity, String type , String division , String dateOfArrival) {
        String query = "INSERT INTO inventory (item_name, quantity, type,  date_of_arrival , division ) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
    
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, type);
           
            pstmt.setString(4, dateOfArrival);
            pstmt.setString(5, division);
            pstmt.executeUpdate();
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private static void deleteItemFromDatabase(String name) {
        String query = "DELETE FROM inventory WHERE item_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateItemInDatabase(String name, int quantity, String type,String division , String dateOfArrival) {
        String query = "UPDATE inventory SET quantity = ?, type = ?, division = ? ,date_of_arrival = ? WHERE item_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, quantity);
            pstmt.setString(2, type);
            pstmt.setString(3, division);
            pstmt.setString(4, dateOfArrival);
            pstmt.setString(5, name); 
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
