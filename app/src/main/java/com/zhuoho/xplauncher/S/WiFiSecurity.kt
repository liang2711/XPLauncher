package com.zhuoho.xplauncher.S

import android.net.wifi.ScanResult


enum class WiFiSecurity(security :String){


    NONE("Nothing"),
    WEP("WEP"),
    PSK("WPA/WPA2 PSK"),
    EAP("802.1x EAP"),
    OWE("OWE"),
    SAE("SAE");
    companion object{
        fun getSecurity(result: ScanResult):WiFiSecurity {
            if (result.capabilities.contains("WEP")) {
                return WEP
            } else if (result.capabilities.contains("PSK")) {
                return PSK
            } else if (result.capabilities.contains("EAP")) {
                return EAP
            } else if (result.capabilities.contains("OWE")) {
                return OWE
            } else if (result.capabilities.contains("SAE")) {
                return SAE
            }
            return NONE
        }
    }
}

