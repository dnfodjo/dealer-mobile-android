package com.moveitech.dealerpay.util

import com.moveitech.dealerpay.dataModel.utilModel.ApiErrorResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


object ErrorUtils {
    fun parseError(apiError: String, t: Throwable): ApiErrorResponse {
        try {
            val json = JSONObject(apiError)
            var error = if (json.has("extras")) {
                ApiErrorResponse(
                    json.getJSONObject("extras").optInt("code"),
                    json.getJSONObject("extras").optString("msg")
                )
            } else {
                ApiErrorResponse(422, json.optString("message"))
            }
            if (error.message.contains("Field")) {
                error = getProperErrorMessage(json)
            }
            return error
        } catch (ex: Exception) {
            ex.printStackTrace()
            return parseError(t)
        }
    }


    fun parseError(t: Throwable): ApiErrorResponse {
        try {
            return ApiErrorResponse(
                0,
                t.message!!
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            return ApiErrorResponse(
                0,
                ""
            )
        }
    }

    private fun getProperErrorMessage(json: JSONObject): ApiErrorResponse {
        val jsonObject = json.getJSONObject("errors")
//        var code=json.optInt("status", 0)
        var message = ""
        val iter = jsonObject.keys()
        if (iter.hasNext()) {
            val key = iter.next()
            try {
                var value = jsonObject[key] as JSONArray
                message = value.getString(0)
            } catch (e: JSONException) {
                // Something went wrong!
            }
        }
        return ApiErrorResponse(422, message)
    }
}