package com.moveitech.dealerpay.IDTECHPack.Fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.annotation.NonUiContext;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.moveitech.dealerpay.IDTECHPack.Adapters.BluetoothDeviceListAdapter;
import com.moveitech.dealerpay.IDTECHPack.Constants;
import com.moveitech.dealerpay.IDTECHPack.MainActivityIdTech;
import com.moveitech.dealerpay.IDTECHPack.Util.LocalCache;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothLeService;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothScanListener;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothScanMessage;
import com.moveitech.dealerpay.R;


public class IdTechBlutoothScanFragment extends ListFragment implements BluetoothScanListener {

    private SettingsViewModel settingsViewModel;

    private BluetoothDeviceListAdapter bluetoothDeviceListAdapter;

    private BluetoothLeService bluetoothLeService;

    private View root = null;
    private String last5OfBluetoothReader = null;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bluetoothDeviceListAdapter = new BluetoothDeviceListAdapter(this.getLayoutInflater());
        setListAdapter(bluetoothDeviceListAdapter);


        settingsViewModel = ViewModelProviders.of(getActivity()).get(SettingsViewModel.class);

        bluetoothLeService = new BluetoothLeService(this, Constants.BLUETOOTH_SCAN_PERIOD);
        observeConfigurationValues(root);

        bluetoothLeService.scan(Constants.IDTECH);
    }

    private void observeConfigurationValues(final View root) {
        settingsViewModel.getLast5OfBluetoothReader().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                last5OfBluetoothReader = s;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        bluetoothDeviceListAdapter.clear();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        final BluetoothDevice bluetoothDevice = bluetoothDeviceListAdapter.getDevice(position);

        if (bluetoothDevice == null) {
            return;
        }

        if(bluetoothDevice.getName() != null && !"".equals(bluetoothDevice.getName()) && bluetoothDevice.getName().length() > 5) {
            String last5 = bluetoothDevice.getName().substring(bluetoothDevice.getName().length() - 5);
            settingsViewModel.setLast5OfBluetoothReader(last5OfBluetoothReader);
            LocalCache.setSelectedBluetoothDeviceLast5(getActivity().getApplicationContext(), last5);
        }

        if (bluetoothLeService.isBluetoothScanningInProcess()) {
            bluetoothLeService.stopScan();
        }

        if (MainActivityIdTech.getNavController() != null){
            MainActivityIdTech.getNavController().navigate(R.id.nav_configure);
        }
    }

    @Override
    public void handle(BluetoothDevice bluetoothDevice) {
        bluetoothDeviceListAdapter.addDevice(bluetoothDevice);
        bluetoothDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void handle(BluetoothScanMessage bluetoothScanMessage) {
        Toast.makeText(getActivity(), bluetoothScanMessage.getDisplayMessage(), Toast.LENGTH_LONG).show();
    }

}
