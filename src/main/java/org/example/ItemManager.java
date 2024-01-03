package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import javax.swing.*;

// Handles operations related to items in the inventory
class ItemManager {
    // MongoDB collection and database
    private final MongoCollection<Document> collection;
    private final MongoDatabase database;

    // Constructor to initialize ItemManager with MongoDB collection and database
    public ItemManager(MongoCollection<Document> collection, MongoDatabase database) {
        this.collection = collection;
        this.database = database;
    }

    // Method to add a new item to the inventory
    public void addItem(String itemId, String description, String price, String quantity, JTextArea resultArea) {
        try {
            // Parse input values
            double parsedPrice = Double.parseDouble(price);
            int parsedQuantity = Integer.parseInt(quantity);
            double totalPrice = parsedPrice * parsedQuantity;

            // Create a new Document for the item
            Document document = new Document("id", itemId)
                    .append("description", description)
                    .append("unitPrice", String.valueOf(parsedPrice))
                    .append("qtyInStock", String.valueOf(parsedQuantity))
                    .append("totalPrice", String.valueOf(totalPrice));

            // Insert the document into the MongoDB collection
            collection.insertOne(document);

            // Record the transaction for adding the item
            recordTransaction(generateTransactionID(), itemId, parsedQuantity, totalPrice, "added", parsedQuantity);
            // Update GUI to indicate success
            resultArea.setText("New Item Added");
        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
            resultArea.setText("Error adding item. Please check your input.");
        }
    }

    // Method to update the quantity of an existing item in the inventory
    public void updateQuantity(String itemIdToUpdate, String newQuantityStr, JTextArea resultArea) {
        try {
            // Parse the new quantity value
            int newQuantity = Integer.parseInt(newQuantityStr);

            // Query the MongoDB collection to find the item to update
            Document query = new Document("id", itemIdToUpdate);
            Document doc = collection.find(query).first();

            if (doc != null) {
                // Retrieve unit price from the existing document
                double unitPrice = Double.parseDouble(doc.getString("unitPrice"));
                double totalValue = newQuantity * unitPrice;

                // Update quantity and total value in the document
                doc.put("qtyInStock", String.valueOf(newQuantity));
                doc.put("totalPrice", String.valueOf(totalValue));

                // Replace the existing document with the updated one
                collection.replaceOne(Filters.eq("id", itemIdToUpdate), doc);

                // Record the transaction for updating the item quantity
                recordTransaction(generateTransactionID(), itemIdToUpdate, newQuantity, totalValue, "updated", newQuantity);

                // Update GUI to indicate success
                resultArea.setText("Item quantity updated");
            }
            // Update GUI to indicate success
            resultArea.setText("Item quantity updated");
        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
            resultArea.setText("Item not found. Error updating quantity. Please check your input.");
        }
    }

    // Method to remove an item from the inventory
    public void removeItem(String itemIdToRemove, JTextArea resultArea) {
        try {
            // Query the MongoDB collection to find the item to remove
            Document query = new Document("id", itemIdToRemove);
            DeleteResult result = collection.deleteOne(query);

            if (result.getDeletedCount() > 0) {
                // Record the transaction for removing the item
                recordTransaction(generateTransactionID(), itemIdToRemove, 0, 0, "removed", 0);
                // Update GUI to indicate success
                resultArea.setText("Item Removed");
            } else {
                resultArea.setText("Item not found.");
            }
        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
            resultArea.setText("Error removing item. Please check your input.");
        }
    }

    // Method to search for an item in the inventory
    public void searchItem(String itemIdToSearch, JTextArea resultArea) {
        try {
            // Query the MongoDB collection to find the item
            Document query = new Document("id", itemIdToSearch);
            Document resultDoc = collection.find(query).first();

            if (resultDoc != null) {
                // Construct the result text for the found item
                String resultText = "Item Found:\n" +
                        "Item ID: " + resultDoc.getString("id") + "\n" +
                        "Item Description: " + resultDoc.getString("description") + "\n" +
                        "Unit Price (£): " + resultDoc.getString("unitPrice") + "\n" +
                        "Current No of items in stock: " + resultDoc.getString("qtyInStock") + "\n" +
                        "Total value of items in stock (£): " + resultDoc.getString("totalPrice") + "\n";

                // Display the search result in a separate JFrame
                displaySearchResult(resultText);
            } else {
                resultArea.setText("Item not found.");
            }
        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
        }
    }

    // Method to generate a unique transaction ID based on the current timestamp
    private String generateTransactionID() {

        return String.valueOf(System.currentTimeMillis());
    }

    // Method to record a transaction in the "transactions" collection
    private void recordTransaction(String transactionID, String itemID, int quantity, double totalValue, String transactionType, int stockRemaining) {
        try {
            // Get the "transactions" collection from the main database
            MongoCollection<Document> transactionsCollection = database.getCollection("transactions");

            // Create a new Document for the transaction
            Document transactionDoc = new Document("transactionID", transactionID)
                    .append("itemID", itemID)
                    .append("quantity", quantity)
                    .append("totalValue", totalValue)
                    .append("stockRemaining", stockRemaining)
                    .append("transactionType", transactionType);

            // Insert the document into the "transactions" collection
            transactionsCollection.insertOne(transactionDoc);
        } catch (Exception e) {
            // Print stack trace in case of an exception
            e.printStackTrace();
        }
    }


    // Method to display the search result in a separate JFrame
    private void displaySearchResult(String resultText) {
        JFrame searchResultFrame = new JFrame("Search Results");
        JTextArea searchResultArea = new JTextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setText(resultText);

        searchResultFrame.add(new JScrollPane(searchResultArea));
        searchResultFrame.setSize(600, 400);
        searchResultFrame.setVisible(true);
    }
}
