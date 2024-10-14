package com.zhuoho.xplauncher.S

data class Wireless(
    var SSID:String?,
    var BSSID:String?,
    var isDhcp:Boolean,
    var ipv4:String?,
    var subnetMask:String?,
    var gateway:String?,
    var dns1:String?,
    var dns2:String?,
)
