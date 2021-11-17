package com.poulstar.bluelight;

import android.bluetooth.BluetoothDevice;

import java.util.Objects;

public class BluetoothDeviceItem {

    String name;
    boolean paired;
    BluetoothDevice bluetoothDevice;

    public BluetoothDeviceItem(String name, BluetoothDevice bluetoothDevice, boolean paired) {
        this.name = name;
        this.paired = paired;
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothDeviceItem that = (BluetoothDeviceItem) o;
        return this.name.equals(that.name);
    }
}
