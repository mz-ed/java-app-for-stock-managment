package mvcstockesystem.m;
import javax.swing.*;

import mvcstockesystem.v.WarehouseInventorySwingApp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

public class SimpleDivisionSelector {

    private static final Set<String> divisions = new HashSet<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleDivisionSelector::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Division Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

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

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(openButton, BorderLayout.SOUTH);

        // Create division
        createButton.addActionListener((ActionEvent e) -> {
            String divisionName = divisionNameField.getText().trim().toLowerCase();
            if (!divisionName.isEmpty() && !divisions.contains(divisionName)) {
                divisions.add(divisionName);
                divisionListModel.addElement(divisionName);
                divisionNameField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid or duplicate division name.");
            }
        });

        // Open division (just a message for now)
        openButton.addActionListener((ActionEvent e) -> {
            String selected = divisionList.getSelectedValue();
            if (selected != null) {
                JOptionPane.showMessageDialog(frame, "Opening division: " + selected);
                SwingUtilities.invokeLater(WarehouseInventorySwingApp::createAndShowGUI);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a division to open.");
            }
        });

        frame.setVisible(true);
    }
}
