package com.hrushev.dao;

import com.hrushev.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface DAOConnection {
    int createOrder(double totalPrice, int userId);
    void addOrderDetails(String productName, double productPrice, int productId, int orderId);
    Product findProduct(int id) throws SQLException;
    List<Product> getAllProducts() throws SQLException;
    List<Product> getFilteredProducts(String price, String category) throws SQLException;
    int addUser(String username, String pass, String address);
}
