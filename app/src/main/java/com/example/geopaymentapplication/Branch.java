package com.example.geopaymentapplication;

public class Branch {
    private int branchID;
    private int bankID;
    private String city;

    public Branch() {
    }

    public Branch(int bankID, String city) {
        this.bankID = bankID;
        this.city = city;
    }

    public Branch(int branchID, int bankID, String city) {
        this.branchID = branchID;
        this.bankID = bankID;
        this.city = city;
    }

    public int getBranchID() {
        return branchID;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }

    public int getBankID() {
        return bankID;
    }

    public void setBankID(int bankID) {
        this.bankID = bankID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
