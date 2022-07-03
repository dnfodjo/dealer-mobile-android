package com.moveitech.dealerpay.IDTECHPack.payment.receipt;

import com.clearent.idtech.android.PublicOnReceiverListener;

public interface PostReceipt {
    void doReceipt(ReceiptRequest receiptRequest, PublicOnReceiverListener publicOnReceiverListener);
}