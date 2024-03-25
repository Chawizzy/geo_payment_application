package com.example.geopaymentapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class VerificationActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;

    private ConnectivityManager connectivityManager;

    private final int verificationCode = autoGenerateCode();
    private EditText verificationCodeEditText;

    private Customer customer2;
    private Bank bank2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Theme
        SharedPref sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_icon);

        // Database
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.open();

        // Set Text Message
        TextView messageTextView = findViewById(R.id.message_verification);
        messageTextView.setText("A verification code has been sent to: \n" + getIntent().getStringExtra("keyEmailAddress"));

        // Send Verification Code
        TextView sendMessageTextView = findViewById(R.id.resend_verification_code);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    sendMessageToCustomer();
                } else {
                    showSnackBar("Please check your internet connection.");
                }
            } catch (Exception e) {
                showSnackBar("Please try again later, something went wrong,");

            }

        }, 1000);

        sendMessageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        sendMessageToCustomer();

                    } else {
                        showSnackBar("Please check your internet connection");

                    }
                } catch (Exception e) {
                    showSnackBar("Please try again later, something went wrong,");

                }
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //compare verification codes to finish
        verificationCodeEditText = findViewById(R.id.verification_code);
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
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getIntent().getStringExtra("keyEmailAddress")));
            msg.setSubject("Verification Code From Payment Application");
            msg.setText(String.valueOf(verificationCode));
            Transport.send(msg);

            showSnackBar("Message sent successfully");

        } catch (MessagingException me) {
            showSnackBar("Failed to sent message");

            throw new RuntimeException(me);
        }
    }

    private int autoGenerateCode() {
        Random rand = new Random();

        return rand.nextInt(1000);
    }

    public void finish(View view) {
        if (verificationCodeEditText.getText().toString().isEmpty()) {
            showSnackBar("Please enter the verification code");

        } else {
            if (Integer.parseInt(verificationCodeEditText.getText().toString()) == verificationCode) {

                if (addDataToDatabase()) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    showSnackBar("Registration failed, please try again later");
                }
            } else {
                showSnackBar("The verification code is wrong, please try again");
            }
        }
    }

    private boolean addDataToDatabase() {
        String firstName = getIntent().getStringExtra("keyFirstName");
        String lastName = getIntent().getStringExtra("keyLastName");
        String address = getIntent().getStringExtra("keyAddress");
        String emailAddress = getIntent().getStringExtra("keyEmailAddress");

        String bankName = getIntent().getStringExtra("keyBankName");

        int accountNumber = getIntent().getIntExtra("keyAccountNumber", 0);
        double balance = getIntent().getDoubleExtra("keyBalance", 0.00);
        String username = getIntent().getStringExtra("keyUsername");
        String password = getIntent().getStringExtra("keyPassword");

        String branchLocation = getIntent().getStringExtra("keyBranchLocation");

        boolean finishedAdding = false;

        Customer customer1 = new Customer(firstName, lastName, address, emailAddress);

        if (databaseHelper.insertCustomer(customer1)) {
            Cursor cursor1 = databaseHelper.getCustomerID(getIntent().getStringExtra("keyEmailAddress"));

            if (cursor1.moveToFirst()) {
                int customerID1 = cursor1.getInt(0);

                customer2 = new Customer(customerID1);
            }

            Bank bank1 = new Bank(customer2.getCustomerID(), bankName);

            if (databaseHelper.insertBank(bank1)) {
                Cursor cursor2 = databaseHelper.getBankID(customer2.getCustomerID());

                if (cursor2.moveToFirst()) {
                    int bankID2 = cursor2.getInt(0);

                    bank2 = new Bank(bankID2);
                }

                Branch branch1 = new Branch(bank2.getBankID(), branchLocation);

                if (databaseHelper.insertBranch(branch1)) {
                    Account account1 = new Account(customer2.getCustomerID(), bank2.getBankID(), accountNumber, balance, username, password);

                    if (databaseHelper.insertAccount(account1)) {
                        finishedAdding = true;
                    }
                }
            }
        }

        return finishedAdding;
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(Color.BLACK);
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}