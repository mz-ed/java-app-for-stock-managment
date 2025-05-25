package mvcstockesystem.m;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/warehouse";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    // Ensure the inventory table for a division exists
    public static void ensureDivisionTableExists(String division) {
        String tableName = "inventory_" + division.toLowerCase();
        String query = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                       "item_name VARCHAR(100) PRIMARY KEY, " +
                       "quantity INT, " +
                       "type VARCHAR(50), " +
                       "date_of_arrival DATE)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get a list of all division names (derived from inventory tables)
    public static List<String> getAllDivisions() {
        List<String> divisions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             ResultSet rs = conn.getMetaData().getTables(null, null, "inventory_%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName.startsWith("inventory_")) {
                    divisions.add(tableName.substring("inventory_".length()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisions;
    }

    // Insert item into a division's inventory table
    public static void insertItem(String name, int quantity, String type, String division, String dateOfArrival) {
        ensureDivisionTableExists(division);
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
            e.printStackTrace();
        }
    }

    // Example method to delete a division table (optional)
    public static void deleteDivision(String division) {
        String tableName = "inventory_" + division.toLowerCase();
        String query = "DROP TABLE IF EXISTS `" + tableName + "`";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
