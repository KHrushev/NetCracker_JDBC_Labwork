package com.hrushev.model;

import java.util.Date;

public class Order {
    private int id;
    private Date date;
    private int quantity;
    private double totalPrice;
    private int userId;

    public Order() {
    }

    public Order(int id, Date date, int quantity, double totalPrice, int userId) {
        this.id = id;
        this.date = date;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
