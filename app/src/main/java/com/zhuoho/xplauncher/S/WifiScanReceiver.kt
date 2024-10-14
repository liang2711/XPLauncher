package com.zhuoho.xplauncher.S

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.data.Constant
import java.util.concurrent.TimeUnit

class WifiScanReceiver : BroadcastReceiver(){
    val wifiViewModel by lazy { VMWiFiInfo() }
    lateinit var wifiManager: WifiManager
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(MainActivity.TAG,"WifiScanReceiver  ${intent!!.action}")
        when(intent!!.action){
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION->{
                val success=intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED,false)?:false
                wifiManager=context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                if (success){
                    scanSuccess(context)
                }else{
                    scanFailure(context)
                }
            }
            //wifi开启
            WifiManager.WIFI_STATE_CHANGED_ACTION->{
                val wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                val wifiScanWorkRequest = PeriodicWorkRequestBuilder<WifiScanWorker>(30, TimeUnit.MINUTES)
                    .build()
                val workManager=WorkManager.getInstance(context!!)
                when (wifiState) {
                    WifiManager.WIFI_STATE_ENABLED -> {
                        // WiFi 已开启
                        Log.d("WifiStateReceiver", "WiFi 已开启 ${wifiManager.startScan()}")
                        workManager.enqueue(wifiScanWorkRequest)
                    }
                    WifiManager.WIFI_STATE_DISABLED -> {
                        // WiFi 已关闭
                        Log.d("WifiStateReceiver", "WiFi 已关闭")
                        workManager.cancelWorkById(wifiScanWorkRequest.id)
                    }
                }
            }

        }

    }
    private fun scanSuccess(context: Context?){
        //权限检查
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(Constant.TAG,"")
            return
        }
        val result=wifiManager.scanResults

        for (scanResult in result){
            Log.d("${Constant.TAG} WifiScanReceiver", "SSID: ${scanResult.SSID}, BSSID: ${scanResult.BSSID}, Signal Level: ${scanResult.level}")
        }
        wifiViewModel.updateState(true)
        wifiViewModel.updateListofScanResult(result)
    }
    private fun scanFailure(context: Context?) {
        val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val results = wifiManager.scanResults
        // 尝试获取上一次的扫描结果
        for (scanResult in results) {
            Log.d("WifiScanReceiver", "SSID: ${scanResult.SSID}, BSSID: ${scanResult.BSSID}, Signal Level: ${scanResult.level}")
        }
    }
}