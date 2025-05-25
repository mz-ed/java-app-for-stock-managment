package mvcstockesystem.m;
import mvcstockesystem.v.WarehouseInventorySwingApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class DivisionSelectorApp {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/warehouse";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DivisionSelectorApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Division Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        DefaultListModel<String> divisionListModel = new DefaultListModel<>();
        JList<String> divisionList = new JList<>(divisionListModel);
        JScrollPane scrollPane = new JScrollPane(divisionList);

        JTextField divisionNameField = new JTextField();
        JButton createButton = new JButton("Create Division");
        JButton openButton = new JButton("Open Division");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("New Division:"), BorderLayout.WEST);
        inputPanel.add(divisionNameField, BorderLayout.CENTER);
        inputPanel.add(createButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(openButton, BorderLayout.SOUTH);

        loadDivisions(divisionListModel);

        createButton.addActionListener(e -> {
            String division = divisionNameField.getText().trim().toLowerCase();
            if (!division.isEmpty()) {
                createDivisionTable(division);
                divisionListModel.addElement(division);
                divisionNameField.setText("");
            }
        });

        openButton.addActionListener(e -> {
            String selectedDivision = divisionList.getSelectedValue();
            if (selectedDivision != null) {
                WarehouseInventorySwingApp.launchForDivision(selectedDivision); // new method you'll add
            }
        });

        frame.setVisible(true);
    }

    private static void loadDivisions(DefaultListModel<String> model) {
        model.clear();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             ResultSet rs = conn.getMetaData().getTables(null, null, "inventory_%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName.startsWith("inventory_")) {
                    model.addElement(tableName.substring("inventory_".length()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDivisionTable(String division) {
        String tableName = "inventory_" + division;
        String query = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                "item_name VARCHAR(100) PRIMARY KEY, " +
                "quantity INT, " +
                "type VARCHAR(50), " +
                "date_of_arrival DATE" +
                ")";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
