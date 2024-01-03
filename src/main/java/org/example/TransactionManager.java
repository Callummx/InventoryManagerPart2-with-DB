package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.swing.*;

// Responsible for handling and displaying transaction reports
class TransactionManager {
    // Mongo Collection instance to store transactions
    private final MongoCollection<Document> transactionsCollection;

    // Constructor to initialise the TransactionManager with a Mongo Collection
    public TransactionManager(MongoCollection<Document> transactionsCollection) {
        this.transactionsCollection = transactionsCollection;
    }

    // Method to generate and display a transaction report using JFrame + JTextArea
    public void viewReport() {
        try {
            // StringBuilder to construct the text content of the report
            StringBuilder reportText = new StringBuilder();

            // Header
            reportText.append("DAILY TRANSACTION REPORT\n");
            reportText.append("-----------------------------------------------\n");
            // Formatting for the columns
            reportText.append(String.format("%-30s%-15s%-17s%-25s%-25s%-20s\n", "Transaction ID", "Item ID", "Quantity", "Total Value (£)", "Transaction Type", "Stock Remaining"));

            // Re-query the transactions collection each time the report is generated
            transactionsCollection.find().forEach((Document transaction) -> {
                String transactionID = transaction.getString("transactionID");
                String itemID = transaction.getString("itemID");
                int quantity = transaction.getInteger("quantity");
                double totalValue = transaction.getDouble("totalValue");
                String transactionType = transaction.getString("transactionType");
                int stockRemaining = transaction.getInteger("stockRemaining");

                // Append transaction details to the report
                reportText.append(String.format("%-28s%-17s%-21s%-30s%-35s%-25s\n",
                        transactionID, itemID, quantity, totalValue, transactionType, stockRemaining));
            });

            // Create and configure the JFrame for the report
            JFrame reportFrame = new JFrame("Transaction Report");
            JTextArea reportArea = new JTextArea();
            reportArea.setEditable(false);
            reportArea.setText(reportText.toString());

            // Add a JScrollPane to the JTextArea to enable scrolling
            reportFrame.add(new JScrollPane(reportArea));
            // Set the size
            reportFrame.setSize(800, 400);
            reportFrame.setVisible(true);
        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
        }
    }
}
