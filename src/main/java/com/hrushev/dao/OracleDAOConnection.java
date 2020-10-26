package com.hrushev.dao;

import com.hrushev.model.Product;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleDAOConnection implements DAOConnection{
    private static OracleDAOConnection oracleDAOConnection;

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private OracleDAOConnection() {
        super();
    }

    public static OracleDAOConnection getInstance() {
        if (oracleDAOConnection != null) {
            return oracleDAOConnection;
        } else {
            oracleDAOConnection = new OracleDAOConnection();
            return oracleDAOConnection;
        }
    }

    @Override
    public int createOrder(double totalPrice, int userId) {
        int orderId = -1;

        try {
            connection = DBConnectionPool.getConnection();

            Date date = Date.valueOf(LocalDate.now());

            statement = connection.prepareStatement("INSERT INTO ORDERS (\"date\", TOTAL_PRICE, USER_ID) VALUES (?, ?, ?)");
            ((PreparedStatement) statement).setDate(1, date);
            ((PreparedStatement) statement).setFloat(2, (float) totalPrice);
            ((PreparedStatement) statement).setInt(3, userId);
            ((PreparedStatement) statement).execute();

            statement = connection.prepareStatement("SELECT ORDER_ID FROM ORDERS where \"date\" = ? AND TOTAL_PRICE = ? AND USER_ID = ?");
            ((PreparedStatement) statement).setDate(1, date);
            ((PreparedStatement) statement).setFloat(2, (float) totalPrice);
            ((PreparedStatement) statement).setInt(3, userId);

            resultSet = ((PreparedStatement) statement).executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("ORDER_ID");
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }

    @Override
    public void addOrderDetails(String productName, double productPrice, int productId, int orderId) {
        try {
            connection = DBConnectionPool.getConnection();

            statement = connection.prepareStatement("INSERT INTO ORDER_DETAILS (PRODUCT_NAME, PRODUCT_ID, PRODUCT_PRICE, ORDER_ID) VALUES (?,?,?,?)");
            ((PreparedStatement) statement).setString(1, productName);
            ((PreparedStatement) statement).setInt(2, productId);
            ((PreparedStatement) statement).setFloat(3, (float) productPrice);
            ((PreparedStatement) statement).setInt(4, orderId);
            ((PreparedStatement) statement).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int addUser(String username, String pass, String address) {
        int id = -1;
        String checkUser = "SELECT USER_ID FROM USERS WHERE NAME LIKE ? AND PASSWORD LIKE ? AND ADDRESS LIKE ?";

        try {
            connection = DBConnectionPool.getConnection();

            statement = connection.prepareStatement(checkUser);
            ((PreparedStatement) statement).setString(1, username);
            ((PreparedStatement) statement).setString(2, pass);
            ((PreparedStatement) statement).setString(3, address);
            resultSet = ((PreparedStatement) statement).executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt("USER_ID");
                return id;
            } else {
                statement = connection.prepareStatement("INSERT INTO USERS (NAME, PASSWORD, ADDRESS) VALUES (?, ?, ?)");
                ((PreparedStatement) statement).setString(1, username);
                ((PreparedStatement) statement).setString(2, pass);
                ((PreparedStatement) statement).setString(3, address);
                ((PreparedStatement) statement).execute();
            }

            statement = connection.prepareStatement(checkUser);
            ((PreparedStatement) statement).setString(1, username);
            ((PreparedStatement) statement).setString(2, pass);
            ((PreparedStatement) statement).setString(3, address);
            resultSet = ((PreparedStatement) statement).executeQuery();

            while (resultSet.next()) {
                id = resultSet.getInt("USER_ID");
            }

            connection.close();
            return id;
        } catch (SQLException e) {
            e.printStackTrace();
            return id;
        }
    }

    @Override
    public Product findProduct(int id) {
        Product product = null;

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM PRODUCTS WHERE PRODUCT_ID = " + id);

            while (resultSet.next()) {
                product = parseProduct(resultSet);
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();

        try {
            connection = DBConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM PRODUCTS");

            while (resultSet.next()) {
                productList.add(parseProduct(resultSet));
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    @Override
    public List<Product> getFilteredProducts(String price, String category) throws SQLException {
        connection = DBConnectionPool.getConnection();

        List<Product> productList = new ArrayList<>();

        String query;

        if (!category.toLowerCase().equals("default")) {
            String[] categoryList = category.split(",");

            for (int i = 0; i < categoryList.length; i++) {
                categoryList[i] = "'" + categoryList[i] + "'";
            }

            query = "SELECT * FROM PRODUCTS INNER JOIN CATEGORIES CAT on PRODUCTS.CATEGORY_ID = CAT.CATEGORY_ID WHERE CAT.NAME IN (" + Arrays.toString(categoryList) + ")";

            query = query.replace("[", "");
            query = query.replace("]", "");
        } else {
            query = "SELECT * FROM PRODUCTS";
        }

        if (!price.toLowerCase().equals("default")) {
            query += price.toLowerCase().equals("high to low") ? " ORDER BY PRICE desc" : " ORDER BY PRICE asc";
        }

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                productList.add(parseProduct(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    private Product parseProduct(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("PRODUCT_ID");
            String name = resultSet.getString("NAME");
            double price = resultSet.getDouble("PRICE");
            String description = resultSet.getString("DESCRIPTION");
            int categoryId = resultSet.getInt("CATEGORY_ID");

            return new Product(id, name, price, description, categoryId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
