package com.example.geopaymentapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AboutActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mToggle;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.option_menu_icon));

        // Navigation Drawer
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));//changes the three bar color to white
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        if(getSupportActionBar() != null) {
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
}