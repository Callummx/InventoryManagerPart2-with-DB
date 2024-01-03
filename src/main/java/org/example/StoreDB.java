package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Main class for the inventory management system
public class StoreDB extends JFrame implements ActionListener {
    // MongoDB's connection details
    private static final String DATABASE_URI = "mongodb+srv://s257028:27N2BimT0OcdrCHC@inventorydb.0tsq7ii.mongodb.net/test?retryWrites=true&w=majority";
    private static final String DB_NAME = "InventoryDB";
    private static final String COLLECTION_NAME = "items";
    // Managers for handling items, transactions, and GUI
    private final ItemManager dbItemManager;
    private final TransactionManager dbTransactionManager;
    private final GUIManager guiManager;
    // MongoDB's connection objects
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    // StoreDB constructor
    public StoreDB() {
        try {
            // Establish MongoDB connection
            MongoClient mongoClient = MongoClients.create(DATABASE_URI);
            database = mongoClient.getDatabase(DB_NAME);
            collection = database.getCollection(COLLECTION_NAME);

            // Clear the transactions collection at the beginning of each run
            database.getCollection("transactions").deleteMany(new Document());

            // Create an index on the "itemID" field for efficient searching
            collection.createIndex(Filters.eq("itemID", 1));
        } catch (Exception e) {
            // Print stack trace in case of an exception during MongoDB connection
            e.printStackTrace();
        }

        // Initialize managers with MongoDB collection and database
        dbItemManager = new ItemManager(collection, database);
        dbTransactionManager = new TransactionManager(database.getCollection("transactions"));
        guiManager = new GUIManager(this);

        // Initialize and display the GUI
        guiManager.initializeUI();
    }

    // Entry point for the application
    public static void main(String[] args) {

        SwingUtilities.invokeLater(StoreDB::new);
    }

    // ActionListener implementation for handling GUI actions
    public void actionPerformed(ActionEvent e) {

        guiManager.handleAction(e, dbItemManager, dbTransactionManager);
    }
}