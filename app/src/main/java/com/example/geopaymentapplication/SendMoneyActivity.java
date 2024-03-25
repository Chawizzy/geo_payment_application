package com.example.geopaymentapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class SendMoneyActivity extends AppCompatActivity {
    private ListView pairedDevices;
    private TextView connectionStatus;
    private EditText amountEditText;

    //notification
    private final String CHANNEL_ID = "Geo_Payment_Application";
    private final int NOTIFICATION_ID = 1;

    //database
    private DatabaseHelper databaseHelper;

    //navigation
    private ActionBarDrawerToggle mToggle;
    private Intent intent;

    private Account account;
    private Customer customer;

    private String receiverName;

    //bluetooth
    private SendReceive sendReceive;

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice[] bluetoothDeviceArray;

    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_MESSAGE_RECEIVED = 5;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

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

        // Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetoothAvailability();

        pairedDevices = findViewById(R.id.paired_devices_list_view);
        connectionStatus = findViewById(R.id.connection_status_send);
        amountEditText = findViewById(R.id.amount_send);

        pairedDevices.setOnItemClickListener((adapterView, view, i, l) -> {
            ClientClass clientClass = new ClientClass(bluetoothDeviceArray[i]);
            clientClass.start();

            connectionStatus.setText("Connecting");
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
                Toast.makeText(this, "Bluetooth permission is required for the app to function properly.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth permission is required for the app to function properly.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableBluetooth() {
        if (ContextCompat.checkSelfPermission(SendMoneyActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SendMoneyActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }

    public void listDevicesButton(View view) {
        if (ContextCompat.checkSelfPermission(SendMoneyActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SendMoneyActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            if (bluetoothAdapter.isEnabled()) {
                Set<BluetoothDevice> set = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[set.size()];
                bluetoothDeviceArray = new BluetoothDevice[set.size()];
                int index = 0;

                if (set.size() > 0) {
                    for (BluetoothDevice device : set) {
                        bluetoothDeviceArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings) {
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
                    pairedDevices.setAdapter(arrayAdapter);
                }
            } else {
                enableBluetooth();
            }
        }
    }

    public void sendButton(View view) {
        double newBalance;

        if (!amountEditText.getText().toString().isEmpty()) {

            if (connectionStatus.getText().toString().equals("Connected")) {

                if (Double.parseDouble(amountEditText.getText().toString()) > 0) {
                    getBalance();

                    newBalance = account.getBalance() - Double.parseDouble(amountEditText.getText().toString());

                    if (newBalance > 0) {
                        passwordDialog(newBalance);

                    } else {
                        Toast.makeText(this, "This transaction cannot be made", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "The amount cannot be P0", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "You are not connected yet", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter the amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void passwordDialog(double newBalance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.password_dialog, null);
        EditText passwordEditText = view.findViewById(R.id.password_send);
        AppCompatButton sendAppCompatButton = view.findViewById(R.id.send_button);
        TextView errorMessageTextView = view.findViewById(R.id.error_message_send);

        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();//opens the dialog

        sendAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEditText.getText().toString().equals(getPassword())) {
                    String string = String.valueOf(amountEditText.getText());
                    sendReceive.write(string.getBytes());

                    account = new Account(newBalance, getPassword());

                    if (databaseHelper.updateUserBalance(account)) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            addTransaction();

                        }, 2000);
                    }

                    createNotification();
                    addNotification();

                    showToast();

                    alertDialog.dismiss();//closes the dialog
                } else {
                    errorMessageTextView.setText("Please try again");
                }
            }
        });
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Geo Payment Application";
            String description = "This a send money notification";

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    //extremely
    private void addNotification() {
        if (ContextCompat.checkSelfPermission(SendMoneyActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                builder.setSmallIcon(R.drawable.logo_icon);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_icon));
                builder.setContentTitle("Transaction notification");
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText("A transaction was made using your account, P" + amountEditText.getText().toString() + " was sent to " + getCustomerName()));
                builder.setAutoCancel(true);
                builder.setPriority((NotificationCompat.PRIORITY_DEFAULT));

                //add as notification
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }

    private void showToast() {
        Toast toast = Toast.makeText(this, "The transaction was successful", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void getBalance() {
        Cursor cursor = databaseHelper.getBalance(getPassword());

        if (cursor.moveToFirst()) {
            double balance = cursor.getDouble(0);

            account = new Account(balance);
        }
    }

    private void addTransaction() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        int accountID = getAccountID();
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        ;
        String time = simpleDateFormat.format(calendar.getTime());
        String transactionType = "send";
        double amount = Double.parseDouble(amountEditText.getText().toString());
        String fullName = receiverName;

        //objects
        Transaction transaction = new Transaction(accountID, date, time, transactionType, amount, fullName);

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

    private String getPassword() {
        //SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userPassword", Context.MODE_PRIVATE);

        return sharedPreferences.getString("password", "");
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

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

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

                    receiverName = new String(readBuff, 0, msg.arg1);
                    break;
            }
            return true;
        }
    });

    private class ClientClass extends Thread {
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {

            try {
                if (ContextCompat.checkSelfPermission(SendMoneyActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SendMoneyActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
                } else {
                    socket = device1.createRfcommSocketToServiceRecord(MY_UUID);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                if (ContextCompat.checkSelfPermission(SendMoneyActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SendMoneyActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
                } else {
                    socket.connect();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket) {
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
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
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
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