package com.example.geopaymentapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Intent intent;

    private EditText usernameEditText;
    private EditText passwordEditText;

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
        setContentView(R.layout.activity_main);

        //actionbar/toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //database
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.open();

        //login
        usernameEditText = findViewById(R.id.username_login);
        passwordEditText = findViewById(R.id.password_login);
    }

    public void loginButton(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            showSnackBar("Please fill in all fields");

        } else {
            if (databaseHelper.checkLoginDetails(username, password)) {
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("keyPassword", password);
                startActivity(intent);
                finish();

            } else {
                showSnackBar("Login failed, please try again");

            }
        }
    }

    public void forgotPasswordTextView(View view) {
        intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void registerTextView(View view) {
        intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);

    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(Color.BLACK);
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}