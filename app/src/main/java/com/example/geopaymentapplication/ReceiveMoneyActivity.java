package com.example.geopaymentapplication;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiveMoneyActivity extends AppCompatActivity {
    private TextView connectionStatus;

    //database
    private DatabaseHelper databaseHelper;

    //navigation
    private ActionBarDrawerToggle mToggle;
    private Intent intent;

    //SharedPreferences
    private SharedPreferences sharedPreferences;

    //objects
    private Transaction transaction;
    private Account account;
    private Customer customer;

    //bluetooth
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;


    private BluetoothAdapter bluetoothAdapter;
    private SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_money);

        //toolbar or actionbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setOverflowIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.option_menu_icon));

        //database
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.open();

        //navigation
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));//changes the three bar color to white
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        //bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetoothAvailability();

        connectionStatus = findViewById(R.id.connection_status_receive);
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

    private void checkBluetoothAvailability() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth is not supported on you device", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            enableBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth permission is required for the app to function properly.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableBluetooth() {
        if (ContextCompat.checkSelfPermission(ReceiveMoneyActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }


    public void Listen(View view) {
        ServerClass serverClass = new ServerClass();
        serverClass.start();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    connectionStatus.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    connectionStatus.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    connectionStatus.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    connectionStatus.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);

                    getBalance();

                    String amountReceived = tempMsg.replaceAll("[^0-9]+", "");

                    double newBalance = account.getBalance() + Double.parseDouble(amountReceived);

                    account = new Account(newBalance, getPassword());

                    if (databaseHelper.updateUserBalance(account)) {
                        Pattern p = Pattern.compile("[a-zA-Z]+");

                        Matcher m1 = p.matcher(tempMsg);

                        while (m1.find()) {
                            String senderName = m1.group();

                            addTransaction(senderName, Double.parseDouble(amountReceived));

                            sendMessage();
                        }
                    }

                    break;
            }
            return true;
        }
    });

    private void addTransaction(String senderName, double amountReceived) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        int accountID = getAccountID();
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        ;
        String time = simpleDateFormat.format(calendar.getTime());
        String transactionType = "Receive";
        double amount = amountReceived;
        String fullName = senderName;

        transaction = new Transaction(accountID, date, time, transactionType, amount, fullName);

        databaseHelper.insertTransactions(transaction);
    }

    private int getAccountID() {
        Cursor cursor = databaseHelper.getAccountID(getPassword());

        if (cursor.moveToFirst()) {
            int accountID = cursor.getInt(0);

            account = new Account(accountID);
        }

        return account.getAccountID();
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

    private void sendMessage() {
        String string = String.valueOf(getCustomerName());
        sendReceive.write(string.getBytes());
    }

    private String getCustomerName() {
        Cursor cursor = databaseHelper.getCustomerName(getCustomerID());

        if (cursor.moveToFirst()) {
            String firstname = cursor.getString(0);

            customer = new Customer(firstname);
        }

        return customer.getFirstName();
    }

    private int getCustomerID() {
        Cursor cursor = databaseHelper.getCustomerID(getAccountID());

        if (cursor.moveToFirst()) {
            int customerID = cursor.getInt(0);

            customer = new Customer(customerID);
        }

        return customer.getCustomerID();
    }

    //classes
    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                if (ContextCompat.checkSelfPermission(ReceiveMoneyActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReceiveMoneyActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
                } else {
                    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}