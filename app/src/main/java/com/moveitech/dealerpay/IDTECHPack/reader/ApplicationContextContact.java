package com.moveitech.dealerpay.IDTECHPack.reader;

import android.content.Context;

import com.clearent.idtech.android.PublicOnReceiverListener;
import com.clearent.idtech.android.family.ApplicationContext;
import com.idtechproducts.device.ReaderInfo;

public class ApplicationContextContact implements ApplicationContext {

    private ReaderInfo.DEVICE_TYPE deviceType;
    private PublicOnReceiverListener publicOnReceiverListener;
    private Context context;
    private String paymentsBaseUrl;
    private String paymentsPublicKey;
    private String idTechXmlConfigurationFileLocation;
    private Boolean autoConfiguration = false;

    public ApplicationContextContact(ReaderInfo.DEVICE_TYPE deviceType, PublicOnReceiverListener publicOnReceiverListener, Context context, String paymentsBaseUrl, String paymentsPublicKey, String idTechXmlConfigurationFileLocation) {
        this.deviceType = deviceType;
        this.publicOnReceiverListener = publicOnReceiverListener;
        this.context = context;
        this.paymentsBaseUrl = paymentsBaseUrl;
        this.paymentsPublicKey = paymentsPublicKey;
        this.idTechXmlConfigurationFileLocation = idTechXmlConfigurationFileLocation;
    }

    @Override
    public ReaderInfo.DEVICE_TYPE getDeviceType() {
        return deviceType;
    }

    @Override
    public PublicOnReceiverListener getPublicOnReceiverListener() {
        return publicOnReceiverListener;
    }

    @Override
    public Context getAndroidContext() {
        return context;
    }

    @Override
    public String getPaymentsBaseUrl() {
        return paymentsBaseUrl;
    }

    @Override
    public String getPaymentsPublicKey() {
        return paymentsPublicKey;
    }

    @Override
    public String getIdTechXmlConfigurationFileLocation() {
        return idTechXmlConfigurationFileLocation;
    }

    @Override
    public boolean disableAutoConfiguration() {
        return !autoConfiguration;
    }

    public void setAutoConfiguration(Boolean autoConfiguration) {
        this.autoConfiguration = autoConfiguration;
    }

}
