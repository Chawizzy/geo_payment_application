package com.example.geopaymentapplication;

public class Transaction {
    private int transactionID;
    private int accountID;
    private String date;
    private String time;
    private String transactionType;
    private double amount;
    private String fullName;

    public Transaction() {
    }

    public Transaction(int accountID, String date, String time, String transactionType, double amount, String fullName) {
        this.accountID = accountID;
        this.date = date;
        this.time = time;
        this.transactionType = transactionType;
        this.amount = amount;
        this.fullName = fullName;
    }

    public Transaction(int transactionID, int accountID, String date, String time, String transactionType, double amount, String fullName) {
        this.transactionID = transactionID;
        this.accountID = accountID;
        this.date = date;
        this.time = time;
        this.transactionType = transactionType;
        this.amount = amount;
        this.fullName = fullName;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
