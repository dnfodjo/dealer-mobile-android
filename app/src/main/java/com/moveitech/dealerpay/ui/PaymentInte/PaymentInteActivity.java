package com.moveitech.dealerpay.ui.PaymentInte;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.clearent.idtech.android.PublicOnReceiverListener;
import com.clearent.idtech.android.domain.CardProcessingResponse;
import com.clearent.idtech.android.domain.ClearentPaymentRequest;
import com.clearent.idtech.android.family.HasManualTokenizingSupport;
import com.clearent.idtech.android.token.domain.TransactionToken;
import com.google.android.material.textfield.TextInputEditText;
import com.idtechproducts.device.Common;
import com.idtechproducts.device.ErrorCode;
import com.idtechproducts.device.ErrorCodeInfo;
import com.idtechproducts.device.ReaderInfo;
import com.idtechproducts.device.StructConfigParameters;
import com.idtechproducts.device.bluetooth.BluetoothLEController;
import com.moveitech.dealerpay.IDTECHPack.Constants;
import com.moveitech.dealerpay.IDTECHPack.Fragments.PaymentViewModel;
import com.moveitech.dealerpay.IDTECHPack.Fragments.SettingsViewModel;
import com.moveitech.dealerpay.IDTECHPack.Util.LocalCache;
import com.moveitech.dealerpay.IDTECHPack.payment.CreditCard;
import com.moveitech.dealerpay.IDTECHPack.payment.PostPayment;
import com.moveitech.dealerpay.IDTECHPack.payment.PostPaymentImpl;
import com.moveitech.dealerpay.IDTECHPack.payment.PostTransactionRequest;
import com.moveitech.dealerpay.IDTECHPack.payment.manual.ManualEntryService;
import com.moveitech.dealerpay.IDTECHPack.payment.receipt.PostReceipt;
import com.moveitech.dealerpay.IDTECHPack.payment.receipt.PostReceiptImpl;
import com.moveitech.dealerpay.IDTECHPack.payment.receipt.ReceiptDetail;
import com.moveitech.dealerpay.IDTECHPack.payment.receipt.ReceiptRequest;
import com.moveitech.dealerpay.IDTECHPack.reader.CardReaderService;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothLeService;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothScanListener;
import com.moveitech.dealerpay.IDTECHPack.reader.blutooth.BluetoothScanMessage;
import com.moveitech.dealerpay.MainActivity;
import com.moveitech.dealerpay.R;
import com.moveitech.dealerpay.ui.BottomSheetS.BottomSheetAmountFragment;

public class PaymentInteActivity extends AppCompatActivity implements BottomSheetAmountFragment.OnBottomSheetClick, BluetoothScanListener, PublicOnReceiverListener, HasManualTokenizingSupport {

    private Toolbar toolbar;
    private AppCompatImageView backImg;
    private CardView amountCard;
    private AppCompatTextView amountTxt;
    private BottomSheetAmountFragment bottomSheetAmountFragment;
    private String oldAmountReal;
    private String oldAmountToShow;
    private AppCompatButton payBtn,cnclBtn;
    private PaymentViewModel paymentViewModel;
    private SettingsViewModel settingsViewModel;
    private AppCompatTextView connectionTxt;
    private TextInputEditText lastFiveDigitEdit;


    // variables used form clearnet payment SDK //
    private Boolean settingsProdEnvironment = false;
    private Boolean settingsBluetoothReader = false;
    private Boolean settingsAudioJackReader = false;
    private Boolean enableContactless = false;
    private Boolean enable2In1Mode = false;
    private Boolean clearContactCache = false;
    private Boolean clearContactlessCache = false;
    private String last5OfBluetoothReader = null;
    private Boolean runningManualEntry = false;

    private Boolean runningTransaction = false;
    private boolean isReady = false;

    private LayoutInflater layoutInflater;


    private BluetoothLeService bluetoothLeService;
    private CardReaderService cardReaderService;
    private ManualEntryService manualEntryService;


    private Handler handler = new Handler();
    private AlertDialog transactionAlertDialog;

    private boolean okayToConfigure = false;
    private boolean configuring = false;
//    private AlertDialog configurationDialog;

    private String info = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_inte);

        // tO SHOW ACTIVITY ON FULL SCREEN //
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        bluetoothLeService = new BluetoothLeService(PaymentInteActivity.this, Constants.BLUETOOTH_SCAN_PERIOD);

        paymentViewModel = ViewModelProviders.of(PaymentInteActivity.this).get(PaymentViewModel.class);
        settingsViewModel = ViewModelProviders.of(PaymentInteActivity.this).get(SettingsViewModel.class);

        settingsViewModel.getEnableContactless().setValue(true);


        if (cardReaderService != null) {
            cardReaderService.unregisterListen();
        }
        if (manualEntryService == null) {
            manualEntryService = ManualEntryService.getInstance(this);
        }

        connectionTxt = findViewById(R.id.connected_txt);
        lastFiveDigitEdit = findViewById(R.id.et_five_digit);

        oldAmountReal =  paymentViewModel.getPaymentAmount().getValue();
        oldAmountToShow = "Total Amount : $" + oldAmountReal;

        initializeReader();

        observeConfigurationValues();
        syncLocalCache();

        updateReaderConnected("Disconnected");

        layoutInflater = getLayoutInflater();


        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        amountCard = findViewById(R.id.amount_card);

        amountTxt = findViewById(R.id.amount_txt);

        amountTxt.setText(oldAmountToShow);

        backImg = findViewById(R.id.back);

        payBtn = findViewById(R.id.pay_btn);
        cnclBtn = findViewById(R.id.cancel_btn);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backIntent = new Intent(PaymentInteActivity.this, MainActivity.class);
                backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backIntent);
                finish();

            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent backIntent = new Intent(PaymentInteActivity.this, MainActivity.class);
                backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backIntent);
                finish();
            }
        });



        amountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                oldAmountToShow = amountTxt.getText().toString().trim();

                bottomSheetAmountFragment = new BottomSheetAmountFragment();
                bottomSheetAmountFragment.show(getSupportFragmentManager(), bottomSheetAmountFragment.getTag());
                bottomSheetAmountFragment.setOnBottomSheetClick(PaymentInteActivity.this);



            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s = lastFiveDigitEdit.getText().toString().trim();

                oldAmountToShow = amountTxt.getText().toString().trim();


                if (TextUtils.isEmpty(amountTxt.getText().toString().trim())){

                    Toast.makeText(PaymentInteActivity.this, "Amount is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(s)){
                    Toast.makeText(PaymentInteActivity.this, "Last five digits of bluetooth reader are missing", Toast.LENGTH_SHORT).show();
                    return;
                }



                last5OfBluetoothReader = s;
                settingsViewModel.getLast5OfBluetoothReader().setValue(s);
                Common.setBLEDeviceName(s);
                LocalCache.setSelectedBluetoothDeviceLast5(getApplicationContext(), last5OfBluetoothReader);


                // start doing transaction here //

                updateModelFromView();
                runningManualEntry = false;

                if (cardReaderService == null) {
                    initCardReaderService();
                }

                closePopup();

                System.out.println("AMOUNT: " + oldAmountReal);
                System.out.println("MANUAL CARD TRANSACTION: " + isManualCardEntry());
                System.out.println("CARD SERVICE CONNECTION: " + cardReaderService.device_isConnected());

                if (isManualCardEntry()) {
                    runningManualEntry = true;
                    displayTransactionPopup();
                    CreditCard creditCard = manualEntryService.createCreditCard(paymentViewModel.getCardNumber().getValue(), paymentViewModel.getCardExpirationDate().getValue(), paymentViewModel.getCardCVV().getValue());
                    manualEntryService.createTransactionToken(creditCard);
                } else if (cardReaderService.device_isConnected()) {

                    String amount = paymentViewModel.getPaymentAmount().getValue();

                    if (amount == null || "".equals(amount)) {
                        Toast.makeText(PaymentInteActivity.this, "Amount Required", Toast.LENGTH_LONG).show();
                    } else {
                        handler.post(doSwipeProgressBar);
                    }
                } else if (!cardReaderService.device_isConnected()) {

                    displayTransactionPopup();

                    System.out.println("SETTINGSBLUETOOTHREADER: " + settingsBluetoothReader);

                    if (settingsBluetoothReader) {

                        System.out.println("DEVICE DISSCONNECTED ELSE IF: " + last5OfBluetoothReader + " Sett:" + settingsViewModel.getLast5OfBluetoothReader().getValue());
                        bluetoothLeService.scan(Constants.BLUETOOTH_READER_PREFIX + "-" + last5OfBluetoothReader);
                        Toast.makeText(PaymentInteActivity.this, "Connecting Bluetooth Reader Ending In " + last5OfBluetoothReader, Toast.LENGTH_LONG).show();

                    } else if (settingsAudioJackReader) {
                        handler.post(doRegisterListen);
                        cardReaderService.device_configurePeripheralAndConnect();
                        Toast.makeText(PaymentInteActivity.this, "Connecting Audio Jack Reader", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(PaymentInteActivity.this, "Connect Reader First", Toast.LENGTH_LONG).show();
                }

            }
        });

        cnclBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // cancel the transaction here //
            }
        });

    }

    @Override
    public void updateClicked(String editVal) {

        if (bottomSheetAmountFragment != null && bottomSheetAmountFragment.isVisible()) {

            bottomSheetAmountFragment.dismiss();
            bottomSheetAmountFragment = null;

        }

        if (TextUtils.isEmpty(editVal)){

            // set old value //
            paymentViewModel.getPaymentAmount().setValue(oldAmountReal);
            amountTxt.setText(oldAmountToShow);

        }else{

            // set new One
            oldAmountReal = editVal;
            oldAmountToShow = "Total Amount : $" + editVal;
            amountTxt.setText(oldAmountToShow);
            paymentViewModel.getPaymentAmount().setValue(oldAmountReal);

        }
    }

    public void initializeReader() {
        if (cardReaderService != null) {
            releaseSDK();
        }
        isReady = false;
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


    private void observeConfigurationValues() {

        settingsViewModel.getProdEnvironment().observe(PaymentInteActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsProdEnvironment = onOff == 0 ? false : true;
            }
        });

        settingsViewModel.getBluetoothReader().observe(PaymentInteActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsBluetoothReader = onOff == 0 ? false : true;
                LocalCache.setBluetoothReaderValue(getApplicationContext(), onOff);
            }
        });

        settingsViewModel.getAudioJackReader().observe(PaymentInteActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsAudioJackReader = onOff == 0 ? false : true;
            }
        });
        settingsViewModel.getEnableContactless().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                enableContactless = enabled;
            }
        });
        settingsViewModel.getEnable2In1Mode().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                enable2In1Mode = enabled;
            }
        });
        settingsViewModel.getEnableContactless().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                enableContactless = enabled;
            }
        });
        settingsViewModel.getEnable2In1Mode().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                enable2In1Mode = enabled;
            }
        });

        settingsViewModel.getClearContactlessConfigurationCache().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                clearContactlessCache = enabled;
            }
        });

        settingsViewModel.getLast5OfBluetoothReader().observe(PaymentInteActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                last5OfBluetoothReader = s;
                Common.setBLEDeviceName(s);
            }
        });

    }


    private void updateModelFromView() {

        paymentViewModel.getPaymentAmount().setValue(oldAmountReal);

        paymentViewModel.getCustomerEmailAddress().setValue("");

        paymentViewModel.getCardNumber().setValue("");

        paymentViewModel.getCardExpirationDate().setValue("");

        paymentViewModel.getCardCVV().setValue("");


        settingsViewModel.getProdEnvironment().setValue(0);
        settingsViewModel.getSandboxEnvironment().setValue(1);
        settingsViewModel.getAudioJackReader().setValue(0);
        settingsViewModel.getBluetoothReader().setValue(1);
        settingsViewModel.getEnableContactless().setValue(true);
        settingsViewModel.getConfigureContactless().setValue(false);
        settingsViewModel.getConfigureContact().setValue(false);
        settingsViewModel.getClearContactlessConfigurationCache().setValue(false);
        settingsViewModel.getClearContactConfigurationCache().setValue(false);
        settingsViewModel.getEnable2In1Mode().setValue(false);

        String s = lastFiveDigitEdit.getText().toString().trim();
        settingsViewModel.getLast5OfBluetoothReader().setValue(s);

    }

    @Override
    public void handle(BluetoothDevice bluetoothDevice) {
        System.out.println("BLUTOOTH DEVICE: " + bluetoothDevice.toString());
        BluetoothLEController.setBluetoothDevice(bluetoothDevice);
        okayToConfigure = true;
        handler.post(doRegisterListen);
    }

    @Override
    public void handle(BluetoothScanMessage bluetoothScanMessage) {

        System.out.println("BLUTOOTHSCAN MESSAGE: " + bluetoothScanMessage);

        if(!BluetoothScanMessage.SCAN_STOPPED.equals(bluetoothScanMessage)) {
            addPopupMessage(transactionAlertDialog, bluetoothScanMessage.getDisplayMessage());
        }
    }

    private Runnable doRegisterListen = new Runnable() {
        public void run() {
            cardReaderService.registerListen();
        }
    };

    private Runnable doSwipeProgressBar = new Runnable() {
        public void run() {
            runningTransaction = false;
            if (cardReaderService.device_isConnected()) {
                if (transactionAlertDialog != null && !transactionAlertDialog.isShowing()) {
                    transactionAlertDialog.show();
                } else {
                    displayTransactionPopup();
                }
                handler.post(doStartTransaction);
            } else {
                Toast.makeText(PaymentInteActivity.this, "Connect Reader First", Toast.LENGTH_LONG).show();
            }
        }
    };



    private Runnable doStartTransaction = new Runnable() {
        @Override
        public void run() {
            String amount = paymentViewModel.getPaymentAmount().getValue();
            ClearentPaymentRequest clearentPaymentRequest = new ClearentPaymentRequest(Double.valueOf(amount), 0.00, 0, 60, null);
            clearentPaymentRequest.setEmailAddress(paymentViewModel.getCustomerEmailAddress().getValue());

            int ret = 0;
            if (enable2In1Mode) {
                ret = cardReaderService.emv_startTransaction(clearentPaymentRequest);
            } else {
                ret = cardReaderService.device_startTransaction(clearentPaymentRequest);
            }
            if (ret == ErrorCode.NO_CONFIG) {
                addPopupMessage(transactionAlertDialog, "Reader is not configured");
                handler.post(doEnableButtons);
                handler.post(doUpdateStatus);
            } else if (ret == ErrorCode.SUCCESS || ret == ErrorCode.RETURN_CODE_OK_NEXT_COMMAND) {
                runningTransaction = true;
                addPopupMessage(transactionAlertDialog, "Please tap, insert, or swipe...");
            } else if (cardReaderService.device_setDeviceType(ReaderInfo.DEVICE_TYPE.DEVICE_VP3300_AJ)) {
                addPopupMessage(transactionAlertDialog, "Failed to start transaction. Check for low battery amber light or reconnect reader");
                handler.post(doEnableButtons);
                handler.post(doUpdateStatus);
            } else if (!cardReaderService.device_isConnected() && cardReaderService.device_setDeviceType(ReaderInfo.DEVICE_TYPE.DEVICE_VP3300_BT)) {
                addPopupMessage(transactionAlertDialog, "Card reader not connected. Press button on reader and try again");
                handler.post(doEnableButtons);
            } else {
                addPopupMessage(transactionAlertDialog, "Failed to start transaction. Cancel and try again");
                info = "cannot swipe/tap card\n";
                info += "Status: " + cardReaderService.device_getResponseCodeString(ret) + "";
                handler.post(doEnableButtons);
                handler.post(doUpdateStatus);
            }
        }
    };

    private Runnable doUpdateStatus = new Runnable() {
        public void run() {
            //   infoText.setText(info);
        }
    };


    private void initCardReaderService() {
        ReaderInfo.DEVICE_TYPE device_type = ReaderInfo.DEVICE_TYPE.DEVICE_VP3300_BT;
        if (settingsAudioJackReader) {
            device_type = ReaderInfo.DEVICE_TYPE.DEVICE_VP3300_AJ;
        }

        String baseUrl = Constants.SB_BASE_URL;
        String publicKey = Constants.SB_PUBLIC_KEY;
        if (settingsProdEnvironment) {
            baseUrl = Constants.PROD_BASE_URL;
            publicKey = Constants.PROD_PUBLIC_KEY;
        }

        cardReaderService = new CardReaderService(device_type, this, PaymentInteActivity.this, baseUrl, publicKey, true);

        boolean device_setDeviceTypeResponse = cardReaderService.device_setDeviceType(device_type);
        if (!device_setDeviceTypeResponse) {
            Toast.makeText(PaymentInteActivity.this, "Issue setting device type", Toast.LENGTH_LONG).show();
        }
        cardReaderService.setContactlessConfiguration(false);
        cardReaderService.setContactless(enableContactless);
        cardReaderService.setAutoConfiguration(false);

        cardReaderService.addRemoteLogRequest("Android_IDTech_VP3300_JDemo", "Initialized the VP3300 For Payments");
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


//    private void addPopupMessageConfig(AlertDialog alertDialog, String message) {
//        if (alertDialog != null && alertDialog.isShowing()) {
//            TextView textView = (TextView) alertDialog.findViewById(R.id.popupMessages);
//            if (textView == null) {
//                return;
//            }
//            textView.append(message + "\n");
//        }
//    }


    private void closePopup() {
        if (transactionAlertDialog != null) {
            transactionAlertDialog.hide();
            TextView textView = (TextView) transactionAlertDialog.findViewById(R.id.popupMessages);
            if (textView != null) {
                textView.setText("");
            }
        }
    }

//    private void closePopupConfig() {
//        if (configurationDialog != null) {
//            configurationDialog.hide();
//            TextView textView = (TextView) configurationDialog.findViewById(R.id.popupMessages);
//            if (textView != null) {
//                textView.setText("");
//            }
//        }
//    }



    private void displayTransactionPopup() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (transactionAlertDialog != null) {

                    System.out.println("TRANSACTION DIALOG IS  NOT NULL IF");

                    if (runningManualEntry) {
                        transactionAlertDialog.setTitle("Processing card");
                        addPopupMessage(transactionAlertDialog, "");
                    } else {
                        transactionAlertDialog.setTitle("Processing payment");
                        addPopupMessage(transactionAlertDialog, "Wait for instructions...");
                    }
                    transactionAlertDialog.show();
                } else {

                    System.out.println("TRANSACTION DIALOG IS NULL ELSE");

                    AlertDialog.Builder transactionViewBuilder = new AlertDialog.Builder(PaymentInteActivity.this);

                    System.out.println("RUNNING MANUAL ENTERY: " + runningManualEntry);

                    if (runningManualEntry) {
                        transactionViewBuilder.setTitle("Processing card");
                        addPopupMessage(transactionAlertDialog, "");
                    } else {
                        transactionViewBuilder.setTitle("Processing payment");
                        addPopupMessage(transactionAlertDialog, "Wait for instructions...");
                    }

                    View view = layoutInflater.inflate(R.layout.frame_swipe, null);
                    transactionViewBuilder.setView(view);

                    transactionViewBuilder.setCancelable(false);
                    transactionViewBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            cardReaderService.device_cancelTransaction();
                            TextView textView = (TextView) transactionAlertDialog.findViewById(R.id.popupMessages);
                            if (textView != null) {
                                textView.setText("");
                            }
                            handler.post(doEnableButtons);
                        }
                    });
                    transactionAlertDialog = transactionViewBuilder.create();
                    transactionAlertDialog.show();
                }
                if(!cardReaderService.device_isConnected()) {
                    addPopupMessage(transactionAlertDialog,"Press button on reader");
                }
            }
        });
    }

    private boolean isManualCardEntry() {
        if ((paymentViewModel.getCardNumber().getValue() != null && !"".equals(paymentViewModel.getCardNumber().getValue()))) {
            return true;
        }
        return false;
    }


    private Runnable doEnableButtons = new Runnable() {
        public void run() {
            payBtn.setEnabled(true);
        }
    };


    private void updateReaderConnected(final String message) {
        PaymentInteActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                    addPopupMessage(transactionAlertDialog, "Bluetooth disconnected. Press button on reader.");
                }
                connectionTxt.setText(message);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();

        releaseSDK();
        updateModelFromView();

    }

    @Override
    protected void onDestroy() {

        if (cardReaderService != null) {
            cardReaderService.unregisterListen();
        }

        super.onDestroy();
    }

    @Override
    public void isReady() {

        if (!configuring) {
            if (okayToConfigure) {
                Toast.makeText(PaymentInteActivity.this, "\uD83D\uDED1 Applying Configuration \uD83D\uDED1️", Toast.LENGTH_LONG).show();
                applyConfiguration();
            } else {
                Toast.makeText(PaymentInteActivity.this, "Configuration Not Applied", Toast.LENGTH_LONG).show();
            }
        } else {
            if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
//                closePopup();
            }
            cardReaderService.addRemoteLogRequest(Constants.getSoftwareTypeAndVersion(), "Configuration applied to reader " + cardReaderService.getStoredDeviceSerialNumberOfConfiguredReader());
            Toast.makeText(PaymentInteActivity.this, "\uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 Configuration Applied \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28 \uD83D\uDC28", Toast.LENGTH_LONG).show();
        }

        if (cardReaderService.device_isConnected()) {

            if (runningTransaction) {
                return;
            }
            updateReaderConnected("Connected");
            if (!isReady && transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                String amount = paymentViewModel.getPaymentAmount().getValue();
                if (amount == null || "".equals(amount)) {
                    closePopup();
                    Toast.makeText(PaymentInteActivity.this, "Amount Required", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    handler.post(doStartTransaction);
                }
            } else if (!isReady && transactionAlertDialog != null && !transactionAlertDialog.isShowing()) {
                String amount = paymentViewModel.getPaymentAmount().getValue();
                if (amount == null || "".equals(amount)) {
                    Toast.makeText(PaymentInteActivity.this, "Amount Required", Toast.LENGTH_LONG).show();
                } else {
                    transactionAlertDialog.show();
                    handler.post(doStartTransaction);
                }
            }

            isReady = true;

        }
    }

    @Override
    public void successfulTransactionToken(TransactionToken transactionToken) {

        System.out.println("here");

        handler.post(doSuccessUpdates);
        PostPayment postPayment = new PostPaymentImpl();

        String apiKey = Constants.SB_API_KEY;
        if (settingsProdEnvironment) {
            apiKey = Constants.PROD_API_KEY;
        }

        String baseUrl = Constants.SB_BASE_URL;
        if (settingsProdEnvironment) {
            baseUrl = Constants.PROD_BASE_URL;
        }

        PostTransactionRequest postTransactionRequest = postPayment.createPostTransactionRequest(transactionToken, paymentViewModel.getPaymentAmount().getValue(), apiKey, baseUrl);
        postPayment.doSale(postTransactionRequest, this);

    }

    @Override
    public void handleCardProcessingResponse(CardProcessingResponse cardProcessingResponse) {

        switch (cardProcessingResponse) {
            case TERMINATE:
                PaymentInteActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                            addPopupMessage(transactionAlertDialog, "Transaction terminated. Look for possible follow up action");
                        }
                    }
                });
                break;
            default:
                PaymentInteActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        payBtn.setEnabled(true);
                        if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                            addPopupMessage(transactionAlertDialog, cardProcessingResponse.getDisplayMessage());
                        }
                    }
                });
        }

    }

    @Override
    public void handleManualEntryError(String message) {

        info += "\nFailed to get a transaction token from a manually entered card. Error - " + message;
        handler.post(doUpdateStatus);
        payBtn.setEnabled(true);
    }

    @Override
    public void handleConfigurationErrors(String message) {

        // from Settings Fragment //
        if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
            addPopupMessage(transactionAlertDialog,message);
        }

        Toast.makeText(PaymentInteActivity.this, "Configuration Failed \uD83D\uDC4E", Toast.LENGTH_LONG).show();
        info += "\nThe reader failed to configure. Error - " + message;
        handler.post(doUpdateStatus);
        handler.post(disablePopupWhenConfigurationFails);
    }

    @Override
    public void lcdDisplay(int mode, String[] lines, int timeout) {

        if (lines != null && lines.length > 0) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                        addPopupMessage(transactionAlertDialog, lines[0]);
                    }
                }
            });
        }

        if (lines != null && lines.length > 0) {
            PaymentInteActivity.this.runOnUiThread(new Runnable() {
                public void run() {

                    if (lines[0].contains("TIME OUT")) {
                        handler.post(doEnableButtons);
                    }
                    info += "\n";
                    Log.i("WATCH1", lines[0]);
                    info += lines[0] + "\n";
                    handler.post(doUpdateStatus);
                    if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                        addPopupMessage(transactionAlertDialog, lines[0]);
                        String checkTransactionMessage = "Transaction successful. Transaction Id:";
                        String checkReceiptMessage = "PostReceipt sent successfully";
                        String checkFailedTransactionFailed = "Transaction failed";
                        if (lines[0].contains(checkFailedTransactionFailed)) {
                            if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                                closePopup();
                            }
                            paymentViewModel.setSuccessfulTransaction(false);
                            showPaymentFailed();
                        } else if (lines[0].contains(checkTransactionMessage)) {
                            if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                                closePopup();
                            }
                            paymentViewModel.setSuccessfulTransaction(true);
                            showPaymentSuccess(lines[0]);
                            runSampleReceipt(lines[0]);
                        } else if (lines[0].contains(checkReceiptMessage)) {
                            Toast.makeText(PaymentInteActivity.this, "Sent PostReceipt", Toast.LENGTH_LONG).show();
                            if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                                closePopup();
                            }
                        }
                    }
                }
            });
        }

    }

    @Override
    public void lcdDisplay(int i, String[] strings, int i1, byte[] bytes, byte b) {

        //Clearent runs with a terminal major configuration of 5C, so no prompts. We should be able to
        //monitor all messaging using the other lcdDisplay method as well as the response and error handlers.
    }

    @Override
    public void deviceConnected() {

        System.out.println("device connected");
        updateReaderConnected("Connected");

    }

    @Override
    public void deviceDisconnected() {

        updateReaderConnected("Disconnected");


        PaymentInteActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                    addPopupMessage(transactionAlertDialog, "Bluetooth disconnected. Press button on reader.");
                }
            }
        });

        isReady = false;

    }

    @Override
    public void timeout(int i) {
        info += ErrorCodeInfo.getErrorCodeDescription(i);
        handler.post(showTimeout);
    }

    @Override
    public void ICCNotifyInfo(byte[] dataNotify, String strMessage) {
        if (strMessage != null && strMessage.length() > 0) {
            String strHexResp = Common.getHexStringFromBytes(dataNotify);

            info += "ICC Notification Info: " + strMessage + "\n" + "Resp: " + strHexResp;
            handler.post(doUpdateStatus);
        }
    }

    @Override
    public void msgBatteryLow() {
        Toast.makeText(PaymentInteActivity.this, "LOW BATTERY", Toast.LENGTH_LONG).show();
    }

    @Override
    public void LoadXMLConfigFailureInfo(int i, String s) {

        info += "XML loading error...";
        handler.post(doUpdateStatus);
    }

    @Override
    public void msgToConnectDevice() {
        updateReaderConnected("Press Button ⚠️");
    }

    @Override
    public void msgAudioVolumeAdjustFailed() {
        info += "SDK could not adjust volume...";
        handler.post(doUpdateStatus);
    }

    @Override
    public void dataInOutMonitor(byte[] bytes, boolean b) {

        //monitor for debugging and support purposes only.

    }

    @Override
    public void autoConfigProgress(int i) {
        Log.i("WATCH", "autoConfigProgress");
    }

    @Override
    public void autoConfigCompleted(StructConfigParameters structConfigParameters) {
        Log.i("WATCH", "autoConfigCompleted");
    }

    @Override
    public void deviceConfigured() {
        Log.i("WATCH", "device is configured");

    }

    @Override
    public String getPaymentsBaseUrl() {
        if (settingsProdEnvironment) {
            return Constants.PROD_BASE_URL;
        }
        return Constants.SB_BASE_URL;
    }

    @Override
    public String getPaymentsPublicKey() {
        if (settingsProdEnvironment) {
            return Constants.PROD_PUBLIC_KEY;
        }
        return Constants.SB_PUBLIC_KEY;
    }


    private Runnable doSuccessUpdates = new Runnable() {
        public void run() {
            if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                if (runningManualEntry) {
                    addPopupMessage(transactionAlertDialog, "Card tokenized");
                } else {
                    addPopupMessage(transactionAlertDialog, "Please remove card");
                }
                addPopupMessage(transactionAlertDialog, "Running transaction");
            }
            handler.post(doUpdateStatus);
            payBtn.setEnabled(true);
        }
    };

    private Runnable disablePopupWhenConfigurationFails = new Runnable() {
        public void run() {
            if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                closePopup();
            }
        }
    };


    private void runSampleReceipt(String line) {
        String[] parts = line.split(":");
        ReceiptRequest receiptRequest = new ReceiptRequest();

        if (settingsProdEnvironment) {
            receiptRequest.setApiKey(Constants.PROD_API_KEY);
        } else {
            receiptRequest.setApiKey(Constants.SB_API_KEY);
        }

        String baseUrl = Constants.SB_BASE_URL;
        if (settingsProdEnvironment) {
            baseUrl = Constants.PROD_BASE_URL;
        }
        receiptRequest.setBaseUrl(baseUrl);
        ReceiptDetail receiptDetail = new ReceiptDetail();
        System.out.println(paymentViewModel.getCustomerEmailAddress().getValue());
        receiptDetail.setEmailAddress(paymentViewModel.getCustomerEmailAddress().getValue());
        receiptDetail.setTransactionId(parts[1]);
        receiptRequest.setReceiptDetail(receiptDetail);

        PostReceipt postReceipt = new PostReceiptImpl();
        postReceipt.doReceipt(receiptRequest, this);
    }


    private void showPaymentSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentInteActivity.this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("\uD83D\uDCB3 OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


    private void showPaymentFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentInteActivity.this);
        builder.setMessage("Payment Failed")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private Runnable showTimeout = new Runnable() {
        public void run() {
            if (transactionAlertDialog != null && transactionAlertDialog.isShowing()) {
                addPopupMessage(transactionAlertDialog, "Timed out");
            }
        }
    };




    private void syncLocalCache() {

        settingsViewModel.getProdEnvironment().observe(PaymentInteActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsProdEnvironment = onOff == 0 ? false : true;
                LocalCache.setProdValue(getApplicationContext(), onOff);
            }
        });

        settingsViewModel.getBluetoothReader().observe(PaymentInteActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsBluetoothReader = onOff == 0 ? false : true;
                LocalCache.setBluetoothReaderValue(getApplicationContext(), onOff);
            }
        });
        settingsViewModel.getAudioJackReader().observe(PaymentInteActivity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer onOff) {
                settingsAudioJackReader = onOff == 0 ? false : true;
                LocalCache.setAudioJackValue(getApplicationContext(), onOff);

            }
        });
        settingsViewModel.getEnableContactless().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                enableContactless = enabled;
                LocalCache.setEnableContactlessValue(getApplicationContext(), enabled);
            }
        });
        settingsViewModel.getEnable2In1Mode().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                enable2In1Mode = enabled;
                LocalCache.setEnable2InModeValue(getApplicationContext(), enabled);
            }
        });
        settingsViewModel.getClearContactConfigurationCache().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                clearContactCache = enabled;
                LocalCache.setClearContactConfigValue(getApplicationContext(), enabled);
            }
        });
        settingsViewModel.getClearContactlessConfigurationCache().observe(PaymentInteActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                clearContactlessCache = enabled;
                LocalCache.setClearContactlessConfigValue(getApplicationContext(), enabled);
            }
        });

        settingsViewModel.getLast5OfBluetoothReader().observe(PaymentInteActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                lastFiveDigitEdit.setText(s);
                LocalCache.setSelectedBluetoothDeviceLast5(getApplicationContext(), s);

                lastFiveDigitEdit.setSelection(s.length());
            }
        });
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
            if (transactionAlertDialog.isShowing()) {
//                closePopup();
            }
        }
    }


    private boolean configurable() {
        if (settingsViewModel.getConfigureContact().getValue() || settingsViewModel.getConfigureContactless().getValue()) {
            return true;
        }
        Toast.makeText(PaymentInteActivity.this, "Configuration not enabled", Toast.LENGTH_SHORT).show();
        return false;
    }

}