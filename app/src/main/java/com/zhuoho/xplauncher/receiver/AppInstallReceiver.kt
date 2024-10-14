package com.zhuoho.xplauncher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppInstallReceiver(private val updateInstall:()->Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action==Intent.ACTION_PACKAGE_ADDED)
            updateInstall()
    }
}