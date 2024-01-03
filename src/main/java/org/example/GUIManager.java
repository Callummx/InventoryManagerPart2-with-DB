package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Responsible for creating and managing the graphical user interface.
class GUIManager {
    // JFrame instance for the main frame of the GUI
    private final JFrame frame;
    // Buttons for actions in the GUI
    private final JButton addButton;
    private final JButton updateButton;
    private final JButton removeButton;
    private final JButton viewButton;
    private final JButton searchButton;
    // Textfields for user input
    private final JTextField itemIDField;
    private final JTextField descriptionField;
    private final JTextField priceField;
    private final JTextField quantityField;
    // TextArea for displaying results in GUI
    private final JTextArea resultArea;

    // Constructor for GUIManager
    public GUIManager(ActionListener actionListener) {
        // Initialize the main JFrame
        frame = new JFrame("Inventory Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize buttons with labels
        addButton = new JButton("Add Item");
        updateButton = new JButton("Update Quantity");
        removeButton = new JButton("Remove Item");
        viewButton = new JButton("View Report");
        searchButton = new JButton("Search Item");

        itemIDField = new JTextField(10);
        descriptionField = new JTextField(20);
        priceField = new JTextField(10);
        quantityField = new JTextField(10);

        // Initialize TextArea for displaying results
        resultArea = new JTextArea();

        // Action listeners for buttons
        addButton.addActionListener(actionListener);
        updateButton.addActionListener(actionListener);
        removeButton.addActionListener(actionListener);
        viewButton.addActionListener(actionListener);
        searchButton.addActionListener(actionListener);

        // Create inputPanel to organize input fields and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Item ID: "));
        inputPanel.add(itemIDField);
        inputPanel.add(new JLabel("Description: "));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Price: "));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity: "));
        inputPanel.add(quantityField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);

        // Create buttonPanel to organize action buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(removeButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(searchButton);

        // Add components to the main frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    // Makes GUI visible
    public void initializeUI() {
        frame.setVisible(true);
    }

    // Method to handle actions performed on buttons
    public void handleAction(ActionEvent e, ItemManager dbItemManager, TransactionManager dbTransactionManager) {
        if (e.getSource() == addButton) {
            // Call the addItem method in ItemManager
            dbItemManager.addItem(itemIDField.getText(), descriptionField.getText(),
                    priceField.getText(), quantityField.getText(), resultArea);
        } else if (e.getSource() == updateButton) {
            // Call the updateItem method in ItemManager
            String itemIdToUpdate = itemIDField.getText();
            String newQuantityStr = quantityField.getText();
            dbItemManager.updateQuantity(itemIdToUpdate, newQuantityStr, resultArea);
        } else if (e.getSource() == removeButton) {
            // Call the removeItem method in ItemManager
            dbItemManager.removeItem(itemIDField.getText(), resultArea);
        } else if (e.getSource() == viewButton) {
            // Call the viewReport method in TransactionManager
            dbTransactionManager.viewReport();
        } else if (e.getSource() == searchButton) {
            // Call the searchItem method in ItemManager
            dbItemManager.searchItem(itemIDField.getText(), resultArea);
        }
    }
}

