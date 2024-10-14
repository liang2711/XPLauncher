package com.zhuoho.xplauncher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.util.Log
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.data.Constant

@Suppress("UNREACHABLE_CODE")
class WifiConnectReceiver( var bssid: String,
                           val onCallBack: (Boolean) -> Unit = {}): BroadcastReceiver() {
    lateinit var wifiManager: WifiManager
    override fun onReceive(context: Context, intent: Intent) {
        if (intent==null||context==null)return
        val action = intent.action
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        when (action) {
            WifiManager.NETWORK_STATE_CHANGED_ACTION->{
                val networkInfo=intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                if (networkInfo!=null && networkInfo.isConnected){
                    val wifiInfo=wifiManager.connectionInfo
                    Log.d(Constant.TAG,"Connected to ${wifiInfo.ssid}")
                    if (wifiInfo!=null && wifiInfo.bssid.equals(bssid)){

                    }
                }
            }
            WifiManager.SUPPLICANT_STATE_CHANGED_ACTION->{
                connectProcess(context,intent, MainActivity.wifiHelp.getState())
            }
            }
        }
    private fun connectProcess(context: Context,intent: Intent, state: SupplicantState) {
        when(state){
            SupplicantState.SCANNING ->{ if(Constant.DEBUG) Log.i(Constant.TAG,"WiFi正在扫描") }
            SupplicantState.ASSOCIATING -> { if(Constant.DEBUG) Log.i(Constant.TAG,"正在关联AP") }
            SupplicantState.AUTHENTICATING -> { if(Constant.DEBUG) Log.i(Constant.TAG,"正在验证") }
            SupplicantState.ASSOCIATED -> { if(Constant.DEBUG) Log.i(Constant.TAG,"关联AP成功") }
            SupplicantState.COMPLETED ->{
                if(Constant.DEBUG) Log.i(Constant.TAG,"连接成功")
                onCallBack(true)
//                val wifiManager: WifiManager =context.getSystemService(Context.WIFI_SERVICE) as WifiManager
//                wifiManager.startScan()
            }
            SupplicantState.DORMANT,
            SupplicantState.DISCONNECTED ->{
                if(Constant.DEBUG) Log.i(Constant.TAG,"连接不成功")
                    onCallBack(false)
            }
            else ->{ if(Constant.DEBUG) Log.i(Constant.TAG,"其他情况") }
        }

        //获取错误结果
        val connectResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR,123)
        if(Constant.DEBUG){ Log.i(Constant.TAG,"WiFi Link Error: $connectResult") }
        if(connectResult == WifiManager.ERROR_AUTHENTICATING){
            if(Constant.DEBUG) Log.i(Constant.TAG,"密码错误")
        }
    }
}