package com.zhuoho.xplauncher.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.S.VMWiFiInfo
import com.zhuoho.xplauncher.S.WifiScanWorker
import com.zhuoho.xplauncher.data.Constant
import java.util.concurrent.TimeUnit

class WifiBroadcastReceiver(
    val updateState: (Boolean) -> Unit = {},
    val updateListofScanResult: (List<ScanResult>) -> Unit = {}
) : BroadcastReceiver(){

    lateinit var wifiManager: WifiManager
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        when (action) {
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                Log.d("WifiBroadcastReceiver", "SCAN_RESULTS_AVAILABLE_ACTION")
                // 处理WiFi扫描结果
                if (ActivityCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                val scanResults =  wifiManager.scanResults
                val success=intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED,false)?:false
                if (success){
                    scanSuccess(context,scanResults)
                }else{
                    scanFailure(context,scanResults)
                }

            }
            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                // 处理WiFi状态变化
                val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                val wifiScanWorkRequest = PeriodicWorkRequestBuilder<WifiScanWorker>(30, TimeUnit.MINUTES)
                    .build()
                val workManager= WorkManager.getInstance(context!!)
                when (wifiState) {
                    WifiManager.WIFI_STATE_ENABLED ->{
                        Log.d("WifiBroadcastReceiver", "WiFi is enabled")
                        workManager.enqueue(wifiScanWorkRequest)
                    }
                    WifiManager.WIFI_STATE_DISABLED -> {
                        Log.d("WifiBroadcastReceiver", "WiFi is disabled")
                        workManager.cancelWorkById(wifiScanWorkRequest.id)
                    }
                    else -> Log.d("WifiBroadcastReceiver", "WiFi state changed: $wifiState")
                }
            }
            ConnectivityManager.CONNECTIVITY_ACTION->{
                Log.d("WifiBroadcastReceiver", "CONNECTIVITY_ACTION")
            }
        }
    }
    private fun scanSuccess(context: Context?,results:List<ScanResult>){
        //权限检查
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(Constant.TAG,"没有WiFi权限")
            return
        }
        Log.d(Constant.TAG,"scanSuccess")
        for (scanResult in results){
            Log.d("${Constant.TAG} WifiScanReceiver", "SSID: ${scanResult.SSID}, BSSID: ${scanResult.BSSID}, Signal Level: ${scanResult.level}")
        }
        updateListofScanResult(results)
        updateState(false)
    }
    private fun scanFailure(context: Context?,results:List<ScanResult>) {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Log.d(Constant.TAG,"scanFailure")
        // 尝试获取上一次的扫描结果
        for (scanResult in results) {
            Log.d("WifiScanReceiver", "SSID: ${scanResult.SSID}, BSSID: ${scanResult.BSSID}, Signal Level: ${scanResult.level}")
        }
        updateListofScanResult(results)
        updateState(false)
    }
}