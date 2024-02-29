package com.example.payment;

import java.util.Date;

public class TransactionData {
    private String senderUserId;
    private String receiverUserId;
    private double amount;
    private Date timestamp; // You can use the Firestore timestamp data type

    // Required no-argument constructor for Firestore
    public TransactionData() {
    }

    public TransactionData(String senderUserId, String receiverUserId, double amount) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.timestamp = new Date(); // Set the timestamp to the current date and time
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public double getAmount() {
        return amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // You can add setters if needed
}