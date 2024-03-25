package com.example.geopaymentapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;

    private ActionBarDrawerToggle mToggle;
    private Intent intent;

    private ListView historyListView;

    private Account account;

    private ArrayList<String> arrayList;

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
        setContentView(R.layout.activity_history);

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

        // Show History
        historyListView = findViewById(R.id.history_listview);

        arrayList = new ArrayList<>();

        displayHistory();
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

    private void displayHistory() {
        Cursor cursor = databaseHelper.getTransactionData(getAccountID());

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "12345", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                int transactionID = cursor.getInt(0);
                int accountID = cursor.getInt(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);
                String transactionType = cursor.getString(4);
                double amount = cursor.getDouble(5);
                String fullName = cursor.getString(6);

                if (transactionType.equals("send")) {
                    arrayList.add("i sent P" + amount + " to " + fullName);
                } else {
                    arrayList.add("i received P" + amount + " from " + fullName);
                }
            }

            // Initialize a TextView for ListView each Item
            // Set the text color of TextView (ListView Item)
            // Generate ListView Item using TextView
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.WHITE);

                    // Generate ListView Item using TextView
                    return view;
                }
            };
            historyListView.setAdapter(arrayAdapter);
        }
    }

    private int getAccountID() {
        Cursor cursor = databaseHelper.getAccountID(getPassword());

        if (cursor.moveToFirst()) {
            int accountID = cursor.getInt(0);

            account = new Account(accountID);
        }

        return account.getAccountID();
    }

    private String getPassword() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userPassword", Context.MODE_PRIVATE);
        String pword = sharedPreferences.getString("password", "");

        return pword;
    }
}