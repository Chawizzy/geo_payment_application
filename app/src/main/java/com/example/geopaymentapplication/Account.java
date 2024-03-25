package com.example.geopaymentapplication;

public class Account {
    private int accountID;
    private int customerID;
    private int bankID;
    private int accountNumber;
    private double balance;
    private String username;
    private String password;

    public Account() {
    }

    public Account(int accountID) {
        this.accountID = accountID;
    }

    public Account(double balance) {
        this.balance = balance;
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Account(double balance, String password) {
        this.balance = balance;
        this.password = password;
    }

    public Account(int customerID, int bankID, int accountNumber, double balance, String username, String password) {
        this.customerID = customerID;
        this.bankID = bankID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.username = username;
        this.password = password;
    }

    public Account(int accountID, int customerID, int bankID, int accountNumber, double balance, String username, String password) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.bankID = bankID;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.username = username;
        this.password = password;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getBankID() {
        return bankID;
    }

    public void setBankID(int bankID) {
        this.bankID = bankID;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
