package com.moveitech.dealerpay.IDTECHPack.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clearent.idtech.android.PublicOnReceiverListener;
import com.clearent.idtech.android.domain.CardProcessingResponse;
import com.clearent.idtech.android.token.domain.TransactionToken;
import com.idtechproducts.device.ReaderInfo;
import com.idtechproducts.device.StructConfigParameters;
import com.idtechproducts.device.bluetooth.BluetoothLEController;
import com.moveitech.dealerpay.IDTECHPack.Constants;
import com.moveitech.dealerpay.IDTECHPack.MainActivityIdTech;
import com.moveitech.dealerpay.IDTECHPack.Util.LocalCache;
import com.moveitech.dealerpay.IDTECHPack.reader.CardReaderService;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothLeService;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothScanListener;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothScanMessage;
import com.moveitech.dealerpay.R;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;




public class IdTechSettingsFragment extends Fragment implements PublicOnReceiverListener, BluetoothScanListener {

    private SettingsViewModel settingsViewModel;
    private Button configureReaderButton;
    private Button selectBluetoothDeviceButton;

    private AlertDialog configurationDialog;

    private Handler handler = new Handler();

    private boolean isReady = false;

    private String bluetoothReaderLast5 = null;
    private String settingsApiKey = null;
    private String settingsPublicKey = null;
    private Boolean settingsProdEnvironment = false;
    private Boolean settingsBluetoothReader = false;
    private Boolean settingsAudioJackReader = false;
    private Boolean enableContactless = false;
    private Boolean enable2In1Mode = false;
    private Boolean clearContactCache = false;
    private Boolean clearContactlessCache = false;

    View root = null;
    ViewGroup viewGroup;
    LayoutInflater layoutInflater;

    private CardReaderService cardReaderService;
    private boolean okayToConfigure = false;
    private boolean configuring = false;

    private BluetoothLeService bluetoothLeService;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.layoutInflater = inflater;
        this.viewGroup = container;
        settingsViewModel =
                ViewModelProviders.of(getActivity()).get(SettingsViewModel.class);

        root = inflater.inflate(R.layout.fragment_id_tech_settings, container, false);

        syncLocalCache(root);
        bindButtons(root);

        updateViewWithModel();

        updateReaderConnected("Reader Disconnected ❌");

        bluetoothLeService = new BluetoothLeService(this, Constants.BLUETOOTH_SCAN_PERIOD);

        return root;
    }

    private void updateViewWithModel() {
        final TextView last5View = root.findViewById(R.id.settings_last_five_of_reader);
        last5View.setText(settingsViewModel.getLast5OfBluetoothReader().getValue());

        final RadioButton prodEnvRadioButton = root.findViewById(R.id.settings_prod_env);
        prodEnvRadioButton.setChecked(settingsViewModel.getProdEnvironment().getValue() == 1 ? true : false);

        final RadioButton sandBoxEnvRadioButton = root.findViewById(R.id.settings_sandbox_env);
        sandBoxEnvRadioButton.setChecked(settingsViewModel.getSandboxEnvironment().getValue() == 1 ? true : false);

        final RadioButton audioJackRadioButton = root.findViewById(R.id.settings_audiojack_reader);
        audioJackRadioButton.setChecked(settingsViewModel.getAudioJackReader().getValue() == 1 ? true : false);

        final RadioButton radioButton = root.findViewById(R.id.settings_bluetooth_reader);
        radioButton.setChecked(settingsViewModel.getBluetoothReader().getValue() == 1 ? true : false);

        final CheckBox enableContactlessCheckBox = root.findViewById(R.id.enableContactless);
        enableContactlessCheckBox.setChecked(settingsViewModel.getEnableContactless().getValue());

        final CheckBox enableContactlessConfigurationCheckBox = root.findViewById(R.id.checkboxContactlessConfigure);
        enableContactlessConfigurationCheckBox.setChecked(settingsViewModel.getConfigureContactless().getValue());

        final CheckBox enableContactConfigurationCheckBox = root.findViewById(R.id.checkboxAutoConfigure);
        enableContactConfigurationCheckBox.setChecked(settingsViewModel.getConfigureContact().getValue());


        final CheckBox clearContactlessCacheCheckbox = root.findViewById(R.id.clearContactlessCache);
        clearContactlessCacheCheckbox.setChecked(settingsViewModel.getClearContactlessConfigurationCache().getValue());


        final CheckBox clearContactCacheCheckbox = root.findViewById(R.id.clearReaderCache);
        clearContactCacheCheckbox.setChecked(settingsViewModel.getClearContactConfigurationCache().getValue());

        final CheckBox enable2In1ModeCheckbox = root.findViewById(R.id.enable2In1Mode);
        enable2In1ModeCheckbox.setChecked(settingsViewModel.getEnable2In1Mode().getValue());
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseSDK();
        updateModelFromView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseSDK();
        updateModelFromView();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateViewWithModel();
    }

    public void releaseSDK() {
        if (cardReaderService != null) {
            cardReaderService.unregisterListen();
            cardReaderService.release();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                //do nothing
            }
        }
    }

    private void bindButtons(View root) {
        configureReaderButton = (Button) root.findViewById(R.id.settings_configure_reader_button);
        configureReaderButton.setOnClickListener(new ConfigureReaderButtonListener());
        configureReaderButton.setEnabled(true);

        selectBluetoothDeviceButton = (Button) root.findViewById(R.id.settings_select_bluetooth_button);
        selectBluetoothDeviceButton.setOnClickListener(new SelectBluetoothReaderButtonListener());
        selectBluetoothDeviceButton.setEnabled(true);

        final RadioGroup radioGroup = (RadioGroup) root.findViewById(R.id.settings_readers);
        final RadioButton audioJackReaderButton = root.findViewById(R.id.settings_audiojack_reader);
        final TextView last5View = root.findViewById(R.id.settings_last_five_of_reader);
        final TextView selectBluetoothReaderButton = root.findViewById(R.id.settings_select_bluetooth_button);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (audioJackReaderButton.isChecked()) {
                    selectBluetoothReaderButton.setEnabled(false);
                    last5View.setEnabled(false);
                } else {
                    selectBluetoothReaderButton.setEnabled(true);
                    last5View.setEnabled(true);
                }
            }
        });
    }

    private void syncLocalCache(final View root) {

        final TextView last5View = root.findViewById(R.id.settings_last_five_of_reader);

        settingsViewModel.getProdEnvironment().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsProdEnvironment = onOff == 0 ? false : true;
                LocalCache.setProdValue(getActivity().getApplicationContext(), onOff);
            }
        });

        settingsViewModel.getBluetoothReader().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsBluetoothReader = onOff == 0 ? false : true;
                LocalCache.setBluetoothReaderValue(getActivity().getApplicationContext(), onOff);
            }
        });
        settingsViewModel.getAudioJackReader().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsAudioJackReader = onOff == 0 ? false : true;
                LocalCache.setAudioJackValue(getActivity().getApplicationContext(), onOff);

            }
        });
        settingsViewModel.getEnableContactless().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                enableContactless = enabled;
                LocalCache.setEnableContactlessValue(getActivity().getApplicationContext(), enabled);
            }
        });
        settingsViewModel.getEnable2In1Mode().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                enable2In1Mode = enabled;
                LocalCache.setEnable2InModeValue(getActivity().getApplicationContext(), enabled);
            }
        });
        settingsViewModel.getClearContactConfigurationCache().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                clearContactCache = enabled;
                LocalCache.setClearContactConfigValue(getActivity().getApplicationContext(), enabled);
            }
        });
        settingsViewModel.getClearContactlessConfigurationCache().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                clearContactlessCache = enabled;
                LocalCache.setClearContactlessConfigValue(getActivity().getApplicationContext(), enabled);
            }
        });

        settingsViewModel.getLast5OfBluetoothReader().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                last5View.setText(s);
                LocalCache.setSelectedBluetoothDeviceLast5(getActivity().getApplicationContext(), s);
            }
        });
    }

    @Override
    public void isReady() {
        if (!configuring) {
            if (okayToConfigure) {
                Toast.makeText(getActivity(), "\uD83D\uDED1 Applying Configuration \uD83D\uDED1️", Toast.LENGTH_LONG).show();
                applyConfiguration();
            } else {
                Toast.makeText(getActivity(), "Configuration Not Applied", Toast.LENGTH_LONG).show();
            }
        } else {
            if (configurationDialog != null && configurationDialog.isShowing()) {
                closePopup();
            }
            cardReaderService.addRemoteLogRequest(Constants.getSoftwareTypeAndVersion(), "Configuration applied to reader " + cardReaderService.getStoredDeviceSerialNumberOfConfiguredReader());
            Toast.makeText(getActivity(), "\uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 Configuration Applied \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void successfulTransactionToken(TransactionToken transactionToken) {
        System.out.println("here");
        //does not apply to configuration
    }

    @Override
    public void handleCardProcessingResponse(CardProcessingResponse cardProcessingResponse) {
        System.out.println("here");
        //does not apply to configuration
    }

    @Override
    public void handleConfigurationErrors(String message) {

        if (configurationDialog != null && configurationDialog.isShowing()) {
            addPopupMessage(configurationDialog,message);
        }

        Toast.makeText(getActivity(), "Configuration Failed \uD83D\uDC4E", Toast.LENGTH_LONG).show();
    }

    @Override
    public void lcdDisplay(int mode, final String[] lines, int timeout) {
        if (lines != null && lines.length > 0) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (configurationDialog != null && configurationDialog.isShowing()) {
                        addPopupMessage(configurationDialog, lines[0]);
                    }
                }
            });
        }
    }

    private void closePopup() {
        if (configurationDialog != null) {
            configurationDialog.hide();
            TextView textView = (TextView) configurationDialog.findViewById(R.id.popupMessages);
            if (textView != null) {
                textView.setText("");
            }
        }
    }

    private void addPopupMessage(AlertDialog alertDialog, String message) {
        if (alertDialog != null && alertDialog.isShowing()) {
            TextView textView = (TextView) alertDialog.findViewById(R.id.popupMessages);
            if (textView == null) {
                return;
            }
            textView.append(message + "\n");
        }
    }


    @Override
    public void lcdDisplay(int mode, String[] lines, int timeout, byte[] languageCode, byte messageId) {
        System.out.println("here");

    }

    @Override
    public void deviceConnected() {
        updateReaderConnected("Reader Connected \uD83D\uDC9A️");
    }

    @Override
    public void deviceDisconnected() {
        updateReaderConnected("Reader Disconnected ❌");
    }

    @Override
    public void timeout(int errorCode) {
        Toast.makeText(getActivity(), "Configuration Timed out \uD83D\uDC4E", Toast.LENGTH_LONG).show();
    }

    @Override
    public void ICCNotifyInfo(byte[] dataNotify, String strMessage) {
        //does not apply to configuration
    }

    @Override
    public void msgBatteryLow() {
        Toast.makeText(getActivity(), "LOW BATTERY \uD83D\uDC4E", Toast.LENGTH_LONG).show();
    }

    @Override
    public void LoadXMLConfigFailureInfo(int index, String message) {
        Toast.makeText(getActivity(), "Loading of xml configuration file failed \uD83D\uDC4E", Toast.LENGTH_LONG).show();
    }

    @Override
    public void msgToConnectDevice() {
        updateReaderConnected("Press Button ⚠️");
    }

    @Override
    public void msgAudioVolumeAdjustFailed() {
        Toast.makeText(getActivity(), "Volume Adjust failed \uD83D\uDC4E", Toast.LENGTH_LONG).show();
    }

    @Override
    public void dataInOutMonitor(byte[] data, boolean isIncoming) {
        //Only grab this info if IDTech requires logging to research an issue.
    }

    @Override
    public void autoConfigProgress(int i) {
        //needed ?
    }

    @Override
    public void autoConfigCompleted(StructConfigParameters structConfigParameters) {
        Toast.makeText(getActivity(), "Peripheral configuration completed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void deviceConfigured() {
        System.out.println("here");
    }

    @Override
    public void handle(BluetoothDevice bluetoothDevice) {
        BluetoothLEController.setBluetoothDevice(bluetoothDevice);
        okayToConfigure = true;
        handler.post(doRegisterListen);
    }

    @Override
    public void handle(BluetoothScanMessage bluetoothScanMessage) {
        Toast.makeText(getActivity(), bluetoothScanMessage.getDisplayMessage(), Toast.LENGTH_LONG).show();
    }

    public class ConfigureReaderButtonListener implements View.OnClickListener {
        public void onClick(View arg0) {

            configuring = false;
            updateModelFromView();

            if (cardReaderService == null) {
                initCardReaderService();
            }

            if (!configurable()) {
                return;
            }

            displayConfigurationPopup();

            final TextView last5View = root.findViewById(R.id.settings_last_five_of_reader);
            String last5OfBluetoothReader = last5View.getText().toString();
            if (last5OfBluetoothReader == null || "".equals(last5OfBluetoothReader)) {
                Toast.makeText(getActivity(), "Last 5 of device serial number required to configure ", Toast.LENGTH_LONG).show();
            } else if (cardReaderService.device_isConnected() && okayToConfigure) {
                applyConfiguration();
            } else if (!cardReaderService.device_isConnected()) {
                okayToConfigure = false;
                if (isBluetoothReaderConfigured()) {
                    bluetoothLeService.scan(Constants.BLUETOOTH_READER_PREFIX + "-" + last5OfBluetoothReader);
                    Toast.makeText(getActivity(), "Configuring Reader Ending In " + last5OfBluetoothReader, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Plug In Audio Jack", Toast.LENGTH_SHORT).show();
                    okayToConfigure = true;
                    //handler.post(doRegisterListen);
                    cardReaderService.registerListen();
                    cardReaderService.device_configurePeripheralAndConnect();
                }
            }
        }
    }

    public class SelectBluetoothReaderButtonListener implements View.OnClickListener {
        public void onClick(View arg0) {

            updateModelFromView();
            if (MainActivityIdTech.getNavController() != null){
                MainActivityIdTech.getNavController().navigate(R.id.nav_bluetooth_scan);
            }
        }
    }

    private void updateModelFromView() {

        final RadioButton prodEnvRadioButton = root.findViewById(R.id.settings_prod_env);
        settingsViewModel.getProdEnvironment().setValue(prodEnvRadioButton.isChecked() ? 1 : 0);

        final RadioButton sandBoxEnvRadioButton = root.findViewById(R.id.settings_sandbox_env);
        settingsViewModel.getSandboxEnvironment().setValue(sandBoxEnvRadioButton.isChecked() ? 1 : 0);

        final RadioButton audioJackRadioButton = root.findViewById(R.id.settings_audiojack_reader);
        settingsViewModel.getAudioJackReader().setValue(audioJackRadioButton.isChecked() ? 1 : 0);

        final RadioButton radioButton = root.findViewById(R.id.settings_bluetooth_reader);
        settingsViewModel.getBluetoothReader().setValue(radioButton.isChecked() ? 1 : 0);

        final CheckBox enableContactlessCheckBox = root.findViewById(R.id.enableContactless);
        settingsViewModel.getEnableContactless().setValue(enableContactlessCheckBox.isChecked());

        final CheckBox enableContactlessConfigurationCheckBox = root.findViewById(R.id.checkboxContactlessConfigure);
        settingsViewModel.getConfigureContactless().setValue(enableContactlessConfigurationCheckBox.isChecked());

        final CheckBox enableContactConfigurationCheckBox = root.findViewById(R.id.checkboxAutoConfigure);
        settingsViewModel.getConfigureContact().setValue(enableContactConfigurationCheckBox.isChecked());

        final CheckBox clearContactlessCacheCheckbox = root.findViewById(R.id.clearContactlessCache);
        settingsViewModel.getClearContactlessConfigurationCache().setValue(clearContactlessCacheCheckbox.isChecked());

        final CheckBox clearContactCacheCheckbox = root.findViewById(R.id.clearReaderCache);
        settingsViewModel.getClearContactConfigurationCache().setValue(clearContactCacheCheckbox.isChecked());

        final CheckBox enable2In1ModeCheckbox = root.findViewById(R.id.enable2In1Mode);
        settingsViewModel.getEnable2In1Mode().setValue(enable2In1ModeCheckbox.isChecked());

        final TextView last5View = root.findViewById(R.id.settings_last_five_of_reader);
        settingsViewModel.getLast5OfBluetoothReader().setValue(last5View.getText().toString());

    }

    void applyConfiguration() {
        if (configurable()) {
            cardReaderService.setContactlessConfiguration(settingsViewModel.getConfigureContactless().getValue());
            cardReaderService.setContactless(settingsViewModel.getEnableContactless().getValue());
            cardReaderService.setAutoConfiguration(settingsViewModel.getConfigureContact().getValue());

            if (settingsViewModel.getClearContactConfigurationCache().getValue()) {
                cardReaderService.setReaderConfiguredSharedPreference(settingsViewModel.getClearContactConfigurationCache().getValue());
            }
            if (settingsViewModel.getClearContactlessConfigurationCache().getValue()) {
                cardReaderService.setReaderContactlessConfiguredSharedPreference(settingsViewModel.getClearContactlessConfigurationCache().getValue());
            }
            configuring = true;

            cardReaderService.addRemoteLogRequest(Constants.getSoftwareTypeAndVersion(), settingsViewModel.toString());

            cardReaderService.applyClearentConfiguration();
        } else {
            if (configurationDialog.isShowing()) {
                closePopup();
            }
        }
    }

    private boolean configurable() {
        if (settingsViewModel.getConfigureContact().getValue() || settingsViewModel.getConfigureContactless().getValue()) {
            return true;
        }
        Toast.makeText(getActivity(), "Configuration not enabled", Toast.LENGTH_SHORT).show();
        return false;
    }


    private Runnable doRegisterListen = new Runnable() {
        public void run() {
            cardReaderService.registerListen();
        }
    };

    private void displayConfigurationPopup() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (configurationDialog != null) {
                    configurationDialog.setTitle("Configuring Reader");
                    addPopupMessage(configurationDialog, "Do not cancel, disconnect, or switch apps...");
                    configurationDialog.show();
                } else {
                    AlertDialog.Builder configurationViewBuilder = new AlertDialog.Builder(getActivity());

                    configurationViewBuilder.setTitle("Configuring Reader");

                    View view = layoutInflater.inflate(R.layout.frame_swipe, viewGroup, false);
                    configurationViewBuilder.setView(view);

                    configurationViewBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            cardReaderService.device_cancelTransaction();
                            TextView textView = (TextView) configurationDialog.findViewById(R.id.popupMessages);
                            if (textView != null) {
                                textView.setText("");
                            }
                            Toast.makeText(getActivity(), "Configuration cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });

                    configurationViewBuilder.setCancelable(false);

                    configurationDialog = configurationViewBuilder.create();

                    addPopupMessage(configurationDialog, "Do not cancel, disconnect, or switch apps...");

                    configurationDialog.show();
                }
            }
        });
    }

    private boolean isBluetoothReaderConfigured() {
        Integer audioJackReader = settingsViewModel.getAudioJackReader().getValue();
        boolean audioJackReaderEnabled = audioJackReader == 0 ? false : true;
        return !audioJackReaderEnabled;
    }

    private void initCardReaderService() {

        ReaderInfo.DEVICE_TYPE device_type = ReaderInfo.DEVICE_TYPE.DEVICE_VP3300_BT;

        if (!isBluetoothReaderConfigured()) {
            device_type = ReaderInfo.DEVICE_TYPE.DEVICE_VP3300_AJ;
        }

        String baseUrl = Constants.SB_BASE_URL;
        String publicKey = Constants.SB_PUBLIC_KEY;
        Integer prodEnvironment = settingsViewModel.getProdEnvironment().getValue();
        boolean prodEnvironmentEnabled = prodEnvironment == 0 ? false : true;
        if (prodEnvironmentEnabled) {
            baseUrl = Constants.PROD_BASE_URL;
            publicKey = Constants.PROD_PUBLIC_KEY;
        }

        cardReaderService = new CardReaderService(device_type, this, getContext(), baseUrl, publicKey, true);

        boolean device_setDeviceTypeResponse = cardReaderService.device_setDeviceType(device_type);
        if (!device_setDeviceTypeResponse) {
            Toast.makeText(getActivity(), "Issue setting device type", Toast.LENGTH_LONG).show();
        }
        cardReaderService.setContactlessConfiguration(false);
        cardReaderService.setContactless(settingsViewModel.getEnableContactless().getValue());
        cardReaderService.setAutoConfiguration(false);

        cardReaderService.addRemoteLogRequest("Android_IDTech_VP3300_JDemo", "Initialized the VP3300 For Configuration");
    }

    private void updateReaderConnected(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                final TextView readerConnectView = root.findViewById(R.id.readerConnected);
                readerConnectView.setText(message);
            }
        });
    }
}