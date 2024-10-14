package com.zhuoho.xplauncher.S

import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo

data class WiFiInfo(val scanResult: ScanResult,
                    var isSaved:Boolean,
                    var isConnect:Boolean,
                    val wifiConfiguration: WifiConfiguration?,
                    val level:Int,
                    val wiFiSecurity:WiFiSecurity, var wiFiInfo: WifiInfo?
)
