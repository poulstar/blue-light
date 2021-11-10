package com.poulstar.bluelight;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceItem {

    String name;
    boolean paired;
    BluetoothDevice bluetoothDevice;

    public BluetoothDeviceItem(String name, BluetoothDevice bluetoothDevice, boolean paired) {
        this.name = name;
        this.paired = paired;
        this.bluetoothDevice = bluetoothDevice;
    }

}
