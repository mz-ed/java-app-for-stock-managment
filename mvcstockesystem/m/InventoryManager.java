package mvcstockesystem.m;

import java.sql.*;

public class InventoryManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/warehouse";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        // Example usage
        insertItemIntoDatabase("Hammer", 50, "Tool", "hardware", "2025-05-23");
    }

    public static void insertItemIntoDatabase(String name, int quantity, String type, String division, String dateOfArrival) {
        String tableName = "inventory_" + division.toLowerCase();
        ensureDivisionTableExists(division); // Ensure the table exists before inserting

        String query = "INSERT INTO `" + tableName + "` (item_name, quantity, type, division , date_of_arrival) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, type);
            pstmt.setString(4, division);
            pstmt.setString(5, dateOfArrival);
            pstmt.executeUpdate();
            System.out.println("Item inserted successfully into division '" + division + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ensureDivisionTableExists(String division) {
        String tableName = "inventory_" + division.toLowerCase();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                "item_name VARCHAR(100) PRIMARY KEY, " +
                "quantity INT, " +
                "type VARCHAR(50), " +
                "date_of_arrival DATE" +
                ")";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Checked/Created table for division '" + division + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
