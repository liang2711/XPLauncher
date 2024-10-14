package com.zhuoho.xplauncher.S

import android.os.Handler
import android.os.Looper

object  UITool {
    fun setTimer(delayInMillis: Long, onTimeout: () -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            onTimeout()
        }, delayInMillis)
    }
}