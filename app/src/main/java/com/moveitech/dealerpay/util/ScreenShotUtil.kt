package com.moveitech.dealerpay.util

import android.graphics.Bitmap
import android.os.Environment
import android.text.format.DateFormat
import android.view.View
import java.io.File
import java.util.*


object ScreenShotUtil {

        fun takeScreenshot(view: View): Bitmap? {
            val now = Date()
            DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
            return try {
                // image naming and path  to include sd card  appending name you choose for file

                // create bitmap screen capture
                view.isDrawingCacheEnabled = true
                 Bitmap.createBitmap(view.drawingCache)
            } catch (e: Throwable) {
                // Several error may come out with file handling or DOM
                e.printStackTrace()
                null
            }
        }



}