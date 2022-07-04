package com.moveitech.dealerpay.IDTECHPack.payment;

import com.clearent.idtech.android.PublicOnReceiverListener;
import com.clearent.idtech.android.token.domain.TransactionToken;

public interface PostPayment {
    PostTransactionRequest createPostTransactionRequest(TransactionToken transactionToken, String amount, String apiKey, String baseUrl);
    void doSale(PostTransactionRequest postTransactionRequest, PublicOnReceiverListener publicOnReceiverListener);
}