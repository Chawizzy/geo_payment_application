package com.example.geopaymentapplication;

public class Customer {
    private int customerID;
    private String firstName;
    private String lastName;
    private String address;
    private String emailAddress;

    public Customer() {
    }

    public Customer(int customerID) {
        this.customerID = customerID;
    }

    public Customer(String firstName) {
        this.firstName = firstName;
    }

    public Customer(String firstName, String lastName, String address, String emailAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.emailAddress = emailAddress;
    }

    public Customer(int customerID, String firstName, String lastName, String address, String emailAddress) {
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.emailAddress = emailAddress;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
