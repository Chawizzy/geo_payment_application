package com.example.geopaymentapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatabaseHelper databaseHelper;

    private Spinner bankNameSpinner;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText addressEditText;
    private EditText emailAddressEditText;

    private EditText accountNumberEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private EditText branchLocationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back_icon);

        // Database
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.open();

        // Bank Spinner
        bankNameSpinner = findViewById(R.id.bank_name_register);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.bank_name, R.layout.text_view_spinner);
        arrayAdapter.setDropDownViewResource(R.layout.text_view_dropdown);
        bankNameSpinner.setAdapter(arrayAdapter);

        bankNameSpinner.setOnItemSelectedListener(RegisterActivity.this);

        //send data to verification activity
        firstNameEditText = findViewById(R.id.first_name_register);
        lastNameEditText = findViewById(R.id.last_name_register);
        addressEditText = findViewById(R.id.address_register);
        emailAddressEditText = findViewById(R.id.email_address_register);

        accountNumberEditText = findViewById(R.id.account_number_register);
        usernameEditText = findViewById(R.id.username_register);
        passwordEditText = findViewById(R.id.password_register);

        branchLocationEditText = findViewById(R.id.branch_location_register);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void nextButton(View view) {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String emailAddress = emailAddressEditText.getText().toString();

        String bankName = bankNameSpinner.getSelectedItem().toString();

        String accountNumber = accountNumberEditText.getText().toString();
        double balance = autoGenerateBalance();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String branchLocation = branchLocationEditText.getText().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || emailAddress.isEmpty() || accountNumber.isEmpty() || username.isEmpty() || password.isEmpty() || branchLocation.isEmpty()) {
            showSnackBar("Please enter all fields");

        } else {
            if (isEmailValid(emailAddress)) {

                if (!databaseHelper.checkEmailAddress(emailAddress)) {

                    if (!databaseHelper.checkUsername(username)) {

                        if (!databaseHelper.checkPassword(password)) {
                            Intent intent = new Intent(this, VerificationActivity.class);
                            intent.putExtra("keyFirstName", firstName);
                            intent.putExtra("keyLastName", lastName);
                            intent.putExtra("keyAddress", address);
                            intent.putExtra("keyEmailAddress", emailAddress);

                            intent.putExtra("keyBankName", bankName);

                            intent.putExtra("keyBranchLocation", branchLocation);

                            intent.putExtra("keyAccountNumber", Integer.parseInt(accountNumber));
                            intent.putExtra("keyBalance", balance);
                            intent.putExtra("keyUsername", username);
                            intent.putExtra("keyPassword", password);
                            startActivity(intent);
                        } else {
                            showSnackBar("Please enter a different password");
                        }
                    } else {
                        showSnackBar("Please enter a different username");
                    }
                } else {
                    showSnackBar("Please enter a different email address");
                }
            } else {
                showSnackBar("Please fix you email address");
            }
        }
    }

    private double autoGenerateBalance() {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Random rand = new Random();

        int rand_int1 = rand.nextInt(10000);
        double rand_int2 = rand.nextDouble() + rand_int1;

        String final_Double = decimalFormat.format(rand_int2);

        return Double.parseDouble(final_Double);
    }

    private boolean isEmailValid(String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);

        return pat.matcher(emailAddress).matches();
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(Color.BLACK);
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }
}