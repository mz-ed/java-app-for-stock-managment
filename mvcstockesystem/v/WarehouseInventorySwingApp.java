package mvcstockesystem.v ;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class WarehouseInventorySwingApp {

    // MySQL database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/warehouse";
    private static final String DB_USER = "root"; // replace with your MySQL username
    private static final String DB_PASSWORD = "12Itachi"; // replace with your MySQL password

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WarehouseInventorySwingApp::createAndShowGUI);
    }

    public static void createAndShowGUI() {
        // Create frame
        JFrame frame = new JFrame("Warehouse Inventory Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500); // Increase the frame size

        // Create JComboBox (dropdown) for item type
        
        
        // Create table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Item Name");
        model.addColumn("Quantity");
        model.addColumn("type");
        model.addColumn("Date of Arrival");

        // Create JTable
        JTable table = new JTable(model);
        table.setRowHeight(40); // Increase the row height
        table.setFont(new Font("Arial", Font.PLAIN, 18)); // Set a larger font for table

        JScrollPane tableScrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Create input fields with larger font
        JTextField itemNameField = new JTextField(20);
        itemNameField.setFont(new Font("Arial", Font.PLAIN, 18)); // Set larger font

        JTextField quantityField = new JTextField(10);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 18)); // Set larger font
        
        String[] itemTypes = {"Electronics", "Furniture", "Clothing", "Food", "Toys"}; // Define your choices here
        JComboBox<String> typeComboBox = new JComboBox<>(itemTypes);
        typeComboBox.setFont(new Font("Arial", Font.PLAIN, 18)); // Set font for the combo box
  
        // Create JFormattedTextField for Date of Arrival with a larger font
        JFormattedTextField dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        dateField.setColumns(10);
        dateField.setFont(new Font("Arial", Font.PLAIN, 18)); // Set larger font

        // Create buttons with larger font
        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Item");
        JButton updateButton = new JButton("Update Item");

        addButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Set larger font
        removeButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Set larger font
        updateButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Set larger font

        // Panel for input fields, arranged vertically (one field per line)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // Vertical layout
        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(itemNameField);
        inputPanel.add(Box.createVerticalStrut(10)); // Space between components

        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(Box.createVerticalStrut(10)); // Space between components
        
        inputPanel.add(new JLabel("Item Type:"));  // Label for item type
        inputPanel.add(typeComboBox);               // Add the combo box (dropdown) for type
        inputPanel.add(Box.createVerticalStrut(10)); 

        inputPanel.add(new JLabel("Date of Arrival:"));
        inputPanel.add(dateField);
        inputPanel.add(Box.createVerticalStrut(20)); // Space at the bottom for better spacing

        // Panel for buttons with larger spacing
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Increased gap between buttons
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        // Add components to frame
        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Load inventory data from the database
        loadInventoryData(model);

        // Add action listeners to buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = itemNameField.getText();
                String quantityStr = quantityField.getText();
                String typeObject = (String) typeComboBox.getSelectedItem();
                String dateStr = dateField.getText();

                if (name.isEmpty() || quantityStr.isEmpty() || dateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                    return;
                }

                try {
                    int quantity = Integer.parseInt(quantityStr);

                    // Parse the date from the input
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date arrivalDate = sdf.parse(dateStr);

                    // Insert into database
                    insertItemIntoDatabase(name, quantity, sdf.format(arrivalDate));

                    // Add row to table
                    model.addRow(new Object[]{name, quantity,typeObject, sdf.format(arrivalDate)});
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number format.");
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid date format. Use yyyy-MM-dd.");
                }

                // Clear input fields
                itemNameField.setText("");
                quantityField.setText("");
                dateField.setText("");
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String itemName = (String) model.getValueAt(selectedRow, 0);

                    // Delete from database
                    deleteItemFromDatabase(itemName);

                    // Remove from table
                    model.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a row to remove.");
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String name = itemNameField.getText();
                    String quantityStr = quantityField.getText();
                    String typeObject = (String) typeComboBox.getSelectedItem();
                    String dateStr = dateField.getText();

                    if (name.isEmpty() || quantityStr.isEmpty() || dateStr.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                        return;
                    }

                    try {
                        int quantity = Integer.parseInt(quantityStr);

                        // Parse the date from the input
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date arrivalDate = sdf.parse(dateStr);

                        // Update in database
                        updateItemInDatabase(name, quantity, sdf.format(arrivalDate));

                        // Update row in table
                        model.setValueAt(name, selectedRow, 0);
                        model.setValueAt(quantity, selectedRow, 1);
                        model.setValueAt(typeObject, selectedRow,2);
                        model.setValueAt(sdf.format(arrivalDate), selectedRow, 3);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid number format.");
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid date format. Use yyyy-MM-dd.");
                    }

                    // Clear input fields
                    itemNameField.setText("");
                    quantityField.setText("");
                    dateField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a row to update.");
                }
            }
        });

        // Show the frame
        frame.setVisible(true);
    }

    // Method to load inventory data from the database
    private static void loadInventoryData(DefaultTableModel model) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM inventory")) {

            while (rs.next()) {
                String name = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                String typeObject = rs.getString("type");
                String dateOfArrival = rs.getString("date_of_arrival");

                model.addRow(new Object[]{name, quantity, dateOfArrival});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to insert an item into the database
    private static void insertItemIntoDatabase(String name, int quantity, String dateOfArrival) {
        String query = "INSERT INTO inventory (item_name, quantity, date_of_arrival) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, dateOfArrival);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete an item from the database
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

    // Method to update an item in the database
    private static void updateItemInDatabase(String name, int quantity, String dateOfArrival) {
        String query = "UPDATE inventory SET quantity = ?, date_of_arrival = ? WHERE item_name = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, quantity);
            pstmt.setString(2, dateOfArrival);
            pstmt.setString(3, name);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
