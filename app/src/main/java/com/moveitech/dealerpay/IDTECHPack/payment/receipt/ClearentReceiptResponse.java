package com.moveitech.dealerpay.IDTECHPack.payment.receipt;

public class ClearentReceiptResponse {

    private String message;

    public ClearentReceiptResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
