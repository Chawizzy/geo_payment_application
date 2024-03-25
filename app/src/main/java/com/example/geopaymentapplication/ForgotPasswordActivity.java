package com.example.geopaymentapplication;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;

    private EditText emailAddressEditText;

    private ConnectivityManager connectivityManager;
    private Customer customer;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_icon);

        // Database
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.open();

        // Send Username And Password
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        emailAddressEditText = findViewById(R.id.email_address_forgot_password);
        AppCompatButton sendAppCompatButton = findViewById(R.id.send_button);

        sendAppCompatButton.setOnClickListener(v -> {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                if (!emailAddressEditText.getText().toString().isEmpty()) {

                    if (databaseHelper.checkEmailAddress(emailAddressEditText.getText().toString())) {
                        try {
                            sendMessageToCustomer();

                        } catch (Exception e) {
                            showSnackBar("Please try again later, something went wrong");
                        }
                    } else {
                        showSnackBar("We cannot find this email address, please try again");
                    }
                } else {
                    showSnackBar("Please enter your email address");
                }
            } else {
                showSnackBar("Please check your internet connection");
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void sendMessageToCustomer() {
        final String USERNAME = "paymentapplication123456789@gmail.com";
        final String PASSWORD = "tqln ffms sjtb vwax";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(USERNAME));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddressEditText.getText().toString()));
            msg.setSubject("Login Details From Payment Application");
            msg.setText(getUsernameAndPassword());
            Transport.send(msg);

            showSnackBar("Message sent successfully");

        } catch (MessagingException me) {
            showSnackBar("Failed to sent message");

            throw new RuntimeException(me);
        }
    }

    private String getUsernameAndPassword() {
        getCustomerData();

        Cursor cursor = databaseHelper.getUsernamePassword(customer.getCustomerID());

        if (cursor.moveToFirst()) {
            String username = cursor.getString(0);
            String password = cursor.getString(1);

            account = new Account(username, password);
        }

        return "Username:" + account.getUsername() + "\n" + "Password :" + account.getPassword();
    }

    private void getCustomerData() {
        Cursor cursor = databaseHelper.getCustomerID(emailAddressEditText.getText().toString());

        if (cursor.moveToFirst()) {
            int customerID = cursor.getInt(0);

            customer = new Customer(customerID);
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(Color.BLACK);
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}