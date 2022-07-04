package com.moveitech.dealerpay.IDTECHPack.reader.blutooth;

import android.bluetooth.BluetoothDevice;

public interface BluetoothScanListener {
    void handle(BluetoothDevice bluetoothDevice);
    void handle(BluetoothScanMessage bluetoothScanMessage);
}
