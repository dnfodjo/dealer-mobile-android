package com.moveitech.dealerpay.dataModel.utilModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ApiErrorResponse(
    @SerializedName("Code")
    val code: Int = 0,
    @SerializedName("Message")
    val message: String = ""
) : Serializable