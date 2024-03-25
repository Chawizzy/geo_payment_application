package com.example.geopaymentapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    // Database name
    static final String DB_NAME = "Payment_Application.DB";

    // Database version
    static final int DB_VERSION = 1;

    // Tables
    public static final String TABLE_NAME_1 = "Customer";
    public static final String TABLE_NAME_2 = "Account";
    public static final String TABLE_NAME_3 = "Bank";
    public static final String TABLE_NAME_4 = "Branch";
    public static final String TABLE_NAME_5 = "Transactions";

    // Table ID Columns
    public static final String CUSTOMER_ID = "Customer_ID";
    public static final String ACCOUNT_ID = "Account_ID";
    public static final String BANK_ID = "Bank_ID";
    public static final String BRANCH_ID = "Branch_ID";
    public static final String TRANSACTION_ID = "Transaction_ID";

    // Customer Columns
    public static final String CUSTOMER_FIRST_NAME = "First_Name";
    public static final String CUSTOMER_LAST_NAME = "Last_Name";
    public static final String CUSTOMER_ADDRESS = "Address";
    public static final String CUSTOMER_EMAIL_ADDRESS = "Email_Address";

    // Account Columns
    public static final String ACCOUNT_NUMBER = "Account_Number";
    public static final String ACCOUNT_BALANCE = "Balance";
    public static final String ACCOUNT_USERNAME = "Username";
    public static final String ACCOUNT_PASSWORD = "Password";

    // Bank Columns
    public static final String BANK_NAME = "Bank_Name";

    // Branch Columns
    public static final String BRANCH_LOCATION = "Branch_Location";

    // Transaction Columns
    public static final String TRANSACTION_DATE = "Date";
    public static final String TRANSACTION_TIME = "Time";
    public static final String TRANSACTION_TYPE = "Transaction_Type";
    public static final String TRANSACTION_AMOUNT = "Amount";
    public static final String TRANSACTION_FULL_NAME = "Full_Name";

    // Create Customer Table
    private static final String CREATE_TABLE_1 = "CREATE TABLE " + TABLE_NAME_1 + "(" + CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CUSTOMER_FIRST_NAME + " TEXT, " + CUSTOMER_LAST_NAME + " TEXT, " + CUSTOMER_ADDRESS + " TEXT, " + CUSTOMER_EMAIL_ADDRESS + " TEXT" + ")";

    // Create Account Table
    private static final String CREATE_TABLE_2 = "CREATE TABLE " + TABLE_NAME_2 + "(" + ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CUSTOMER_ID + " INTEGER, " + BANK_ID + " INTEGER, " + ACCOUNT_NUMBER + " INTEGER, " + ACCOUNT_BALANCE + " DOUBLE, " + ACCOUNT_USERNAME + " TEXT, " + ACCOUNT_PASSWORD + " TEXT" + ")";

    // Create Bank Table
    private static final String CREATE_TABLE_3 = "CREATE TABLE " + TABLE_NAME_3 + "(" + BANK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CUSTOMER_ID + " INTEGER, " + BANK_NAME + " TEXT" + ")";

    // Create Branch Table
    private static final String CREATE_TABLE_4 = "CREATE TABLE " + TABLE_NAME_4 + "(" + BRANCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + BANK_ID + " INTEGER, " + BRANCH_LOCATION + " TEXT" + ")";

    // Create Transaction Table
    private static final String CREATE_TABLE_5 = "CREATE TABLE " + TABLE_NAME_5 + "(" + TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACCOUNT_ID + " INTEGER, " + TRANSACTION_DATE + " TEXT, " + TRANSACTION_TIME + " TEXT, " + TRANSACTION_TYPE + " TEXT, " + TRANSACTION_AMOUNT + " DOUBLE, " + TRANSACTION_FULL_NAME + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_1);
        db.execSQL(CREATE_TABLE_2);
        db.execSQL(CREATE_TABLE_3);
        db.execSQL(CREATE_TABLE_4);
        db.execSQL(CREATE_TABLE_5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_4);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_5);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public void open() throws SQLException {
        database = this.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public boolean insertCustomer(Customer customer) {
        long newRowId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(CUSTOMER_FIRST_NAME, customer.getFirstName());
            values.put(CUSTOMER_LAST_NAME, customer.getLastName());
            values.put(CUSTOMER_ADDRESS, customer.getAddress());
            values.put(CUSTOMER_EMAIL_ADDRESS, customer.getEmailAddress());

            newRowId = database.insert(TABLE_NAME_1, null, values);
        } catch (Exception exception) {
            Log.e("Error", exception.toString());
        }

        return newRowId > 0;
    }

    public boolean insertBank(Bank bank) {
        long newRowId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(CUSTOMER_ID, bank.getCustomerID());
            values.put(BANK_NAME, bank.getBankName());

            newRowId = database.insert(TABLE_NAME_3, null, values);
        } catch (Exception exception) {
            Log.e("Error", exception.toString());
        }

        return newRowId > 0;
    }

    public boolean insertAccount(Account account) {
        long newRowId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(CUSTOMER_ID, account.getCustomerID());
            values.put(BANK_ID, account.getBankID());
            values.put(ACCOUNT_NUMBER, account.getAccountNumber());
            values.put(ACCOUNT_BALANCE, account.getBalance());
            values.put(ACCOUNT_USERNAME, account.getUsername());
            values.put(ACCOUNT_PASSWORD, account.getPassword());

            newRowId = database.insert(TABLE_NAME_2, null, values);
        } catch (Exception exception) {
            Log.e("Error", exception.toString());
        }

        return newRowId > 0;
    }

    public boolean insertBranch(Branch branch) {
        long newRowId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(BANK_ID, branch.getBankID());
            values.put(BRANCH_LOCATION, branch.getCity());

            newRowId = database.insert(TABLE_NAME_4, null, values);
        } catch (Exception exception) {
            Log.e("Error", exception.toString());
        }

        return newRowId > 0;
    }

    public void insertTransactions(Transaction transaction) {
        try {
            ContentValues values = new ContentValues();
            values.put(ACCOUNT_ID, transaction.getAccountID());
            values.put(TRANSACTION_DATE, transaction.getDate());
            values.put(TRANSACTION_TIME, transaction.getTime());
            values.put(TRANSACTION_TYPE, transaction.getTransactionType());
            values.put(TRANSACTION_AMOUNT, transaction.getAmount());
            values.put(TRANSACTION_FULL_NAME, transaction.getFullName());

            database.insert(TABLE_NAME_5, null, values);
        } catch (Exception exception) {
            Log.e("Error", exception.toString());
        }
    }

    public Cursor getCustomerID(String emailAddress) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select Customer_ID from Customer where Email_Address = ?", new String[]{emailAddress});

        return cursor;
    }

    public Cursor getBankID(int customerID) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select Bank_ID from Bank where Bank_ID = ?", new String[]{String.valueOf(customerID)});

        return cursor;
    }

    public Cursor getAccountID(String password) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select Account_ID from Account where Password = ?", new String[]{password});

        return cursor;
    }

    public Cursor getCustomerID(int accountID) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select Customer_ID from Account where Account_ID = ?", new String[]{String.valueOf(accountID)});

        return cursor;
    }

    public Cursor getCustomerName(int customerID) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select First_Name from Customer where Customer_ID = ?", new String[]{String.valueOf(customerID)});

        return cursor;
    }


    public Cursor getUsernamePassword(int customerID) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select Username, Password from Account where Account_ID = ?", new String[]{String.valueOf(customerID)});

        return cursor;
    }

    public Cursor getBalance(String password) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select Balance from Account where Password = ?", new String[]{String.valueOf(password)});

        return cursor;
    }

    public Cursor getTransactionData(int accountID) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select * from Transactions where Account_ID = ?", new String[]{String.valueOf(accountID)});

        return cursor;
    }

    public boolean checkEmailAddress(String emailAddress) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select * from Customer where Email_Address = ?", new String[]{emailAddress});

        return cursor.getCount() > 0;
    }

    public boolean checkUsername(String username) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select * from Account where Username = ?", new String[]{username});

        return cursor.getCount() > 0;
    }

    public boolean checkPassword(String password) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select * from Account where Password = ?", new String[]{password});

        return cursor.getCount() > 0;
    }

    public boolean checkLoginDetails(String username, String password) {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("Select * from Account where Username = ? and Password = ?", new String[]{username, password});

        return cursor.getCount() > 0;
    }

    public boolean updateUserBalance(Account account) {
        long newRowId = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(ACCOUNT_BALANCE, account.getBalance());

            newRowId = database.update(TABLE_NAME_2, values, "PASSWORD = ?", new String[]{account.getPassword()});

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        return newRowId > 0;
    }
}
