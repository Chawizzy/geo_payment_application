package com.example.geopaymentapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;

    private ActionBarDrawerToggle mToggle;
    private Intent intent;

    private TextView balanceTextView;

    private SharedPreferences sharedPreferences;

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
        setContentView(R.layout.activity_home);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.option_menu_icon));

        // Database
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.open();

        // Navigation Drawer
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));//changes the three bar color to white
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_navigation) {
                intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.send_money_navigation) {
                intent = new Intent(this, SendMoneyActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.receive_money_navigation) {
                intent = new Intent(this, ReceiveMoneyActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.history_navigation) {
                intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.settings_navigation) {
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.help_navigation) {
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (item.getItemId() == R.id.about_navigation) {
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else {
                return false;
            }
        });

        // Set Balance
        balanceTextView = findViewById(R.id.balance_home);

        /*
        i used the shared preference to store the boolean to prevent the password from being cleared
        every time a user goes to a different activity.
         */
        sharedPreferences = getApplicationContext().getSharedPreferences("userBoolean", Context.MODE_PRIVATE);
        boolean alreadyExecuted = sharedPreferences.getBoolean("alreadyExecuted", false);

        if (!alreadyExecuted) {
            savePassword();

            sharedPreferences = getSharedPreferences("userBoolean", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("alreadyExecuted", true);
            editor.apply();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.logout_nav) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePassword() {
        sharedPreferences = getSharedPreferences("userPassword", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", getIntent().getStringExtra("keyPassword"));
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getBalance();

        balanceTextView.setText(String.valueOf(account.getBalance()));
    }

    private void getBalance() {
        Cursor cursor = databaseHelper.getBalance(getPassword());

        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);

            account = new Account(balance);
        }
    }

    private String getPassword() {
        sharedPreferences = getApplicationContext().getSharedPreferences("userPassword", Context.MODE_PRIVATE);
        String pword = sharedPreferences.getString("password", "");

        return pword;
    }

    public void sendMoneyCardView(View view) {
        intent = new Intent(this, SendMoneyActivity.class);
        startActivity(intent);
    }

    public void receiveMoneyCardView(View view) {
        intent = new Intent(this, ReceiveMoneyActivity.class);
        startActivity(intent);
    }

    public void historyCardView(View view) {
        intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void settingsCardView(View view) {
        intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void helpCardView(View view) {
        intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void aboutCardView(View view) {
        intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}