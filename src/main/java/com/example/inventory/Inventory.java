package com.example.inventory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 * Performs data management for the system
 * */
public class Inventory {
    //Holds the parts and products data of the system
    private static final ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static final ObservableList<Product> allProducts = FXCollections.observableArrayList();


    /**
     * Adds a new part into the parts list
     */
    public static void addPart(Part newPart) {
        allParts.add(newPart);
    }

    /**
     * Adds a new product into the products list
     */
    public static void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    /**
     * Searches for a parts using its ID
     */
    public static Part lookupPart(int partId) {
        for (Part part : allParts) {
            if (part.getId() == partId) return part;
        }
        return null;
    }

    /**
     * Searches for a product using its ID
     */
    public static Product lookupProduct(int productId) {
        for (Product product : allProducts) {
            if (product.getId() == productId) return product;

        }
        return null;
    }

    /**
     * Searches for parts using the name
     */
    public static ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> searchResult = FXCollections.observableArrayList();
        for (Part part : allParts) {
            if (part.getName().toLowerCase().contains(partName.toLowerCase())) searchResult.add(part);
        }
        return searchResult;
    }

    /**
     * Searches for products using the name
     */
    public static ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> searchResult = FXCollections.observableArrayList();
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(productName.toLowerCase())) searchResult.add(product);
        }
        return searchResult;
    }

    /**
     * Updates the parts data
     */
    public static void updatePart(int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }

    /**
     * Updates the products data
     */
    public static void updateProduct(int index, Product newProduct) {
        allProducts.set(index, newProduct);
    }

    /**
     * Removes the parts from the system data
     */
    public static boolean deletePart(Part selectedPart) {
        return allParts.remove(selectedPart);
    }

    /**
     * Removes the product from the system data
     */
    public static boolean deleteProduct(Product selectedProduct) {
        return allProducts.remove(selectedProduct);
    }

    /**
     * Provides access to all the system's parts data
     */
    public static ObservableList<Part> getAllParts() {
        return allParts;
    }

    /**
     * Provides access to all the system's products data
     */
    public static ObservableList<Product> getAllProducts() {
        return allProducts;
    }

}
