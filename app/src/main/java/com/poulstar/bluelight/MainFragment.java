package com.poulstar.bluelight;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MainFragment extends Fragment {

    LinearLayout layDevices;
    final String TAG = "BLUELIGHT";
    ArrayList<BluetoothDeviceItem> devices;
    private BroadcastReceiver reciever;
    private UUID myId = UUID.randomUUID();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothConnectThread bluetoothThread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        layDevices = rootView.findViewById(R.id.layDevices);

        reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "New device found");
                if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothDeviceItem item = new BluetoothDeviceItem(device.getName(), device, false);
                    if(!devices.contains(item)) {
                        devices.add(item);
                        makeList();
                    }
                }
            }
        };

        checkBluetooth();
        return rootView;
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

            listRootView.setOnClickListener(view -> {
                // connect to device
                if(bluetoothThread != null) {
                    bluetoothThread.disconnect();
                    try {
                        bluetoothThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                bluetoothThread = new BluetoothConnectThread(device);
                bluetoothThread.start();
            });
        }
    }

    private void checkBluetooth() {
        devices = new ArrayList<>();
        bluetoothAdapter = Bluetooth.checkBluetooth(getActivity());
        if(bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for(BluetoothDevice device : pairedDevices) {
                    devices.add(new BluetoothDeviceItem(device.getName(), device, true));
                }
            }
            makeList();

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGpsEnabled) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 20);
                return;
            }

            IntentFilter discoverBluetooth = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(reciever, discoverBluetooth);
            boolean started = bluetoothAdapter.startDiscovery();
            Log.i(TAG, "Started discovery "+started);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkBluetooth();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(reciever);
        bluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class BluetoothConnectThread extends Thread {

        BluetoothDeviceItem device;
        BluetoothSocket socket;
        OutputStream out;
        InputStream in;

        public BluetoothConnectThread(BluetoothDeviceItem device) {
            this.device = device;
        }

        @Override
        public void run() {
            try {
                socket = device.bluetoothDevice.createRfcommSocketToServiceRecord(myId);
                socket.connect();
                bluetoothAdapter.cancelDiscovery();

                out = socket.getOutputStream();
                in = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Couldn't connect to device. "+e.getMessage());
            }
        }

        public void writeString(String data) {
            try {
                Objects.requireNonNull(out).write(data.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void disconnect() {
            if(socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



//    class BluetoothThread extends Thread {
//
//        BluetoothDevice device;
//
//        public BluetoothThread(BluetoothDevice device) {
//            this.device = device;
//        }
//
//        public void connect() {
//            BluetoothAdapter adapter = Bluetooth.checkBluetooth(getActivity());
//            if(adapter != null) {
//                try {
//                    device.createRfcommSocketToServiceRecord(myId);
//                }catch (Exception e) {
//                    Log.e(TAG, e.getMessage());
//                }
//            }
//        }
//
//        @Override
//        public void run() {
//            super.run();
//
//        }
//    }

}