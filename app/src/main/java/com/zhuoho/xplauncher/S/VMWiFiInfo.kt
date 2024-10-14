package com.zhuoho.xplauncher.S

import android.net.wifi.ScanResult
import android.util.Log
import androidx.collection.objectIntMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.data.Constant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VMWiFiInfo: ViewModel() {
    private val _sr= MutableStateFlow(WiFis(ArrayList(),false))
    private val _state= MutableStateFlow(false)
    private var wiFiInfos:MutableList<WiFiInfo> = ArrayList()
    val srFlow:StateFlow<WiFis> = _sr
    val stateFlow:StateFlow<Boolean> = _state
    fun updateListofScanResult( list:List<ScanResult>){
//        val state=MainActivity.wifiHelp.getState()
        Log.d("VMWiFiInfo","list  ${list.size}")
        var saveds=MainActivity.wifiHelp.getWifiSavedS()
//        val connectWiFiInfo=MainActivity.wifiHelp.getConnectWifiDSSID()
        wiFiInfos=ArrayList()

        for (index in list){
            wiFiInfos.add(WiFiInfo(scanResult = index, false,false,null,index.level,WiFiSecurity.getSecurity(index),null))
        }

        saveds.let {
            for (index in it!!){
                val sw=wiFiInfos.find { ("\"" + it.scanResult.SSID + "\"").equals(index.SSID) }
                if (sw!=null)
                    sw.isSaved=true
            }
        }

        //获取当前已连接WiFi
        var OnconnectWiFiInfo=MainActivity.wifiHelp.getConnectWifiSSID(wiFiInfos)
        if (OnconnectWiFiInfo){
            Log.d(Constant.TAG,"成功找连接WiFi")
        }else{
            Log.d(Constant.TAG,"失败找连接WiFi")
        }
//        val sc=wiFiInfos.find { it.scanResult.SSID==connectWiFiInfo }
//        if (sc!=null)
//            sc.isConnect=true
        wiFiInfos.sortWith(
            compareByDescending<WiFiInfo> { it.isConnect }
                .thenByDescending { it.isSaved }
                .thenBy { it.level }
        )
        Log.d("VMWiFiInfo","wiFiInfos  ${wiFiInfos.size}")
        viewModelScope.launch {
            _sr.value = WiFis(wiFiInfos as ArrayList<WiFiInfo>,true)
        }
    }
    fun updateState( state:Boolean){
        Log.d("VMWiFiInfo","updateState ")
        viewModelScope.launch {
            _state.value= state
        }
    }
}
