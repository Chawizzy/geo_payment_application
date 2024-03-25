package com.example.geopaymentapplication;

public class Bank {
    private int bankID;
    private int customerID;
    private String bankName;

    public Bank() {
    }

    public Bank(int bankID) {
        this.bankID = bankID;
    }

    public Bank(int customerID, String bankName) {
        this.customerID = customerID;
        this.bankName = bankName;
    }

    public Bank(int bankID, int customerID, String bankName) {
        this.bankID = bankID;
        this.customerID = customerID;
        this.bankName = bankName;
    }

    public int getBankID() {
        return bankID;
    }

    public void setBankID(int bankID) {
        this.bankID = bankID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
