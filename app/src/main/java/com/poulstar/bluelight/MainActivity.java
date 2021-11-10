package com.poulstar.bluelight;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    Boolean hasPermission;
    BluetoothAdapter bluetoothAdapter;
    final String TAG = "BLUEDROID";
    View root;
    ArrayList<BluetoothDeviceItem> devices;
    TextView txtName, txtType;
    LinearLayout layDevices;
    private BroadcastReceiver reciever;
    private UUID myId = UUID.randomUUID();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        SharedPreferences preferences = getSharedPreferences("My Pref", MODE_PRIVATE);
        hasPermission = preferences.getBoolean("has permission", false);
        Log.i("BLUETOOTH", "" + hasPermission);
        if(!hasPermission) {
            checkPermission();
        }

        layDevices = findViewById(R.id.layDevices);
        root = findViewById(R.id.rootView);


        reciever = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "New device found");
                if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(new BluetoothDeviceItem(device.getName(), device, false));
                    makeList();
                }
            }
        };

        checkBluetooth();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(reciever);
    }

    private void checkBluetooth() {
        devices = new ArrayList<>();
        BluetoothAdapter bluetoothAdapter = Bluetooth.checkBluetooth(this);
        if(bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for(BluetoothDevice device : pairedDevices) {
                    devices.add(new BluetoothDeviceItem(device.getName(), device, true));
                }
            }
            makeList();

            IntentFilter discoverBluetooth = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(reciever, discoverBluetooth);
        }
    }



    private void makeList() {
        layDevices.removeAllViews();
        for(BluetoothDeviceItem device : devices) {
            LayoutInflater inflater = getLayoutInflater();
            View listRootView = inflater.inflate(R.layout.bluetooth_device, null);
            TextView txtName = listRootView.findViewById(R.id.txtName);
            TextView txtType = listRootView.findViewById(R.id.txtType);
            txtName.setText(device.name);
            txtType.setText(device.paired ? "Paired" : "Not paired");
            layDevices.addView(listRootView);
            listRootView.getLayoutParams().height = 80;
        }
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.i("BLUE", ""+checkSelfPermission(Manifest.permission.BLUETOOTH));
            if(checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ArrayList<String> permissions = new ArrayList<>();
                permissions.add(Manifest.permission.BLUETOOTH);
                requestPermissions((String[]) permissions.toArray(), 5);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 5) {
            if(grantResults[0] == RESULT_OK) {
                SharedPreferences preferences = getSharedPreferences("My Pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("has permission", true);
                editor.commit();
            }
        }
    }


    class BluetoothThread extends Thread {

        BluetoothDevice device;

        public BluetoothThread(BluetoothDevice device) {
            this.device = device;
        }

        public void connect() {
            BluetoothAdapter adapter = Bluetooth.checkBluetooth(MainActivity.this);
            if(adapter != null) {
                try {
                    device.createRfcommSocketToServiceRecord(myId);
                }catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }

        @Override
        public void run() {
            super.run();

        }
    }
}
