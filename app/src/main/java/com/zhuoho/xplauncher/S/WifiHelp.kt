package com.zhuoho.xplauncher.S

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.DhcpInfo
import android.net.IpConfiguration
import android.net.LinkAddress
import android.net.NetworkUtils
import android.net.StaticIpConfiguration
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.zhuoho.xplauncher.data.Constant
import java.net.Inet4Address
import java.net.InetAddress
import java.net.UnknownHostException

class WifiHelp (context: Context,
                           private val wifiManager:WifiManager=context.
                           getSystemService(Context.WIFI_SERVICE) as WifiManager){
    lateinit var context:Context
    init {
        this.context=context
    }
    val SCAN_RESULTS_AVAILABLE_ACTION: String = "android.net.wifi.SCAN_RESULTS"

    //当前WiFi的开关状态
    fun isWifiEnable():Boolean{
        return wifiManager.isWifiEnabled
    }

    //设置开关状态
    fun setWifiEnable(enable:Boolean):Boolean{
        return wifiManager.setWifiEnabled(enable)
    }

    //请求WiFi扫描
    fun startScan():Boolean{
        return wifiManager.startScan()
    }

    fun scanResults():List<ScanResult>?{
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null
        }
        return  wifiManager.scanResults;
    }

    /**
     * 获取当前连接的WiFi名称
     */
    fun getConnectWifiSSID(mList: List<WiFiInfo>):Boolean{
        val wifiInfo=wifiManager.connectionInfo
        var connectWiFiInfo=""
        if (wifiInfo!=null){
            val s=wifiInfo.ssid
            if (s.length > 2 && s.toCharArray()[0] == '"' && s.toCharArray()[s.length - 1] == '"') {
                connectWiFiInfo=s.substring(1, s.length - 1)
//                return s.substring(1, s.length - 1)
                //return true
            }
        }
        val sc=mList.find { it.scanResult.SSID==connectWiFiInfo }
        if (sc!=null){
            sc.isConnect=true
            sc.wiFiInfo=wifiInfo
            return true
        }
        return false
    }
    fun getConnectWifiDSSID():String?{
        val wifiInfo=wifiManager.connectionInfo
//        if (wifiInfo!=null){
//            val s=wifiInfo.bssid
//            if (s.isEmpty())return null
//            if (s.length > 2 && s.toCharArray()[0] == '"' && s.toCharArray()[s.length - 1] == '"') {
//                return s.substring(1, s.length - 1)
//            }
//        }
        return null
    }

    /**
     * 获取WiFi状态
     */
    fun getStateInt(): Int {
        return wifiManager.wifiState
    }

    /**
     * 判断WiFi是否保存过
     */
    @SuppressLint("MissingPermission")
    fun isWiFiSaved(ssid:String): WifiConfiguration?{
        val configs = wifiManager.configuredNetworks
        configs.forEach { if(it.SSID.equals("\"" + ssid + "\"")){ return it } }
        return null
    }

    @SuppressLint("MissingPermission")
    fun getWifiSavedS(): List<WifiConfiguration>?{
        return wifiManager.configuredNetworks
    }

    /**
     * 获取当前SSID的WiFi配置
     * @param[ssid] 字符串或一串16进制的数字
     */
    @SuppressLint("MissingPermission")
    fun getWiFiConfig(ssid: String?): WifiConfiguration? {
        val configs = wifiManager.configuredNetworks
        if(Constant.DEBUG) Log.i(Constant.TAG,"configs size = ${configs.size}")
        if (configs != null && configs.size > 0 ) {
            for( config in configs){
                if(Constant.DEBUG) Log.i(Constant.TAG,"config = $config")
                val configSSID = config.SSID.replace("\"", "")
                if (ssid == configSSID) return config
            }
        }
        return null
    }


    /**
     * 忘记连接过WiFi
     * @param[config] 指定WiFi的配置
     */
    fun forgetWiFi(config: WifiConfiguration?):Boolean {
        var success = false
        if(config?.isPasspoint == true){
            wifiManager.removePasspointConfiguration(config.FQDN)
            success = true
        }else{
            config?.networkId?.let {
                wifiManager.disableNetwork(it)
                wifiManager.removeNetwork(it)
                wifiManager.disconnect()
                success = true
            }
        }
        return success
    }

    /**
     * 连接到保存过的WiFi
     * @param[ssid] 字符串或一串16进制的数字
     */
    fun connectSavedWiFi(ssid:String):Boolean{
        if(Constant.DEBUG) Log.i(Constant.TAG,"ssid = $ssid")
        val config = getWiFiConfig(ssid)
        config?.networkId?.let { enableNetwork(it,true) }
        return wifiManager.saveConfiguration()
    }

    /**
     * 使能网络
     */
    fun enableNetwork(networkId: Int, b: Boolean): Boolean {
        return wifiManager.enableNetwork(networkId, b)
    }
    /**
     * 创建WiFi配置
     */
    fun createWiFiConfig(wiFiInfo:WiFiInfo,pwd:String):Int{
        var networkId = -1

        val config = WifiConfiguration().apply {
            SSID = "\"" + wiFiInfo.scanResult.SSID + "\""
            preSharedKey = "\"" + pwd + "\""
            hiddenSSID = false
            status = WifiConfiguration.Status.ENABLED
        }
        networkId = wifiManager.addNetwork(config)

        return networkId
    }

    /**
     * 创建无密码WiFi配置
     */
    fun createNoPwConfig(ssid:String):Int{
        val config = WifiConfiguration().apply {
            allowedAuthAlgorithms.clear()
            allowedGroupCiphers.clear()
            allowedKeyManagement.clear()
            allowedPairwiseCiphers.clear()
            allowedProtocols.clear()
            SSID = "\"" + ssid + "\""
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        }
        return wifiManager.addNetwork(config)
    }

    /**
     * 获取WiFi连接过程的状态
     */
    fun getState():SupplicantState{
        val info = wifiManager.connectionInfo
        return info.supplicantState
    }

    /**
     * WiFi连接过程处理
     * @param[state] 请求状态
     */

    fun getWiFiInfo(w: WiFiInfo):Wireless{
        var ip="0.0.0.0"
        var subnet="0.0.0."
        var gateway="0.0.0.0"
        var wiFiInfo=w.wiFiInfo

        if (wiFiInfo==null){
            wiFiInfo=wifiManager.connectionInfo
        }

        var dhcpInfo=wifiManager.dhcpInfo


        ip= intToInetAddress(wiFiInfo!!.ipAddress).toString()
        gateway= intToInetAddress(dhcpInfo.gateway).toString()
        var dns1=intToInetAddress(dhcpInfo.dns1)
        var dns2=intToInetAddress(dhcpInfo.dns2)

        subnet= intToInetAddress(dhcpInfo.netmask).toString()
        return Wireless(w.scanResult.SSID,w.scanResult.BSSID,isWiFiDHCP(),ip,subnet,gateway,dns1.toString(),dns2.toString())
    }

    private fun intToInetAddress(ip:Int):InetAddress?{
        val bytes= byteArrayOf(
            (ip and 0xff).toByte(),
            (ip shr 8 and 0xff).toByte(),
            (ip shr 16 and 0xff).toByte(),
            (ip shr 24 and 0xff).toByte()
        )
        return try {
            InetAddress.getByAddress(bytes)
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 设置静态的ip
     * */
    fun setStaticIp(w:Wireless) {
        val wifiConfig = getWiFiConfig(w.SSID)
        val netmaskPrefixLength = netmaskToPrefixLength(w.subnetMask!!)
        if("0.0.0.0" == w.ipv4 || "0.0.0.0" == w.subnetMask || "0.0.0.0" == w.gateway || "0.0.0.0" == w.dns1){
            return
        }
        if (wifiConfig != null) {
            val staticIpConfig = StaticIpConfiguration()
            staticIpConfig.ipAddress = LinkAddress(InetAddress.getByName(w.ipv4), netmaskPrefixLength)
            staticIpConfig.gateway = InetAddress.getByName(w.gateway)
            staticIpConfig.dnsServers.add(InetAddress.getByName(w.dns1))
            staticIpConfig.dnsServers.add(InetAddress.getByName(w.dns2))

            wifiConfig.ipAssignment = IpConfiguration.IpAssignment.STATIC
            wifiConfig.staticIpConfiguration = staticIpConfig

            wifiManager.updateNetwork(wifiConfig)
            wifiManager.disconnect()
            wifiManager.reconnect()
        }
    }

    private fun netmaskToPrefixLength(netmask: String): Int {
        val maskAsInt = InetAddress.getByName(netmask).address.fold(0) { acc, byte ->
            acc or (byte.toInt() and 0xFF)
        }
        return Integer.bitCount(maskAsInt)
    }

    /**
     * 保存WiFi配置
     */
    private fun saveConfiguration(config:WifiConfiguration){
        wifiManager.save(config,null)
    }


    /**
     * 更新WiFi配置
     */
    private fun updateConfiguration(config:WifiConfiguration){
        wifiManager.updateNetwork(config)
    }

    /**
     * WiFi断开连接
     */
    private fun disconnectWiFi():Boolean{
        return wifiManager.disconnect()
    }

    /**
     * 重新连接
     */
    private fun reconnectWiFi():Boolean{
        return wifiManager.reconnect()
    }

    fun isStaticIpConfigured(w:Wireless): Boolean {
        val wifiConfig = getWiFiConfig(w.SSID)
        return try {
            val ipAssignmentField = wifiConfig?.javaClass?.getField("ipAssignment")
            ipAssignmentField?.isAccessible = true
            val ipAssignment = ipAssignmentField?.get(wifiConfig) as Enum<*>
            ipAssignment.name == "STATIC"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isDynamicIpConfigured(w:Wireless): Boolean {
        val wifiConfig = getWiFiConfig(w.SSID)
        return try {
            val ipAssignmentField = wifiConfig?.javaClass?.getField("ipAssignment")
            ipAssignmentField?.isAccessible = true
            val ipAssignment = ipAssignmentField?.get(wifiConfig) as Enum<*>
            ipAssignment.name == "DHCP"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    /**
     * 设置WiFi自动获取ip
     */
    fun setWiFiWithDHCP(proxy:Boolean,w:Wireless?):Boolean{
        if(!wifiManager.isWifiEnabled()){ return false }
        val wifiConfig = getWiFiConfig(w?.SSID)
        val staticConfig = wifiConfig?.getStaticIpConfiguration()
        staticConfig?.clear()

        wifiConfig?.setIpAssignment(IpConfiguration.IpAssignment.DHCP)
        val ipConfig:IpConfiguration
        if(wifiConfig != null){
            ipConfig = wifiConfig.getIpConfiguration()
        }else{
            ipConfig = IpConfiguration()
        }
        ipConfig.setStaticIpConfiguration(staticConfig)
        wifiConfig?.setIpConfiguration(ipConfig)
        if(!proxy){
            wifiConfig?.setIpConfiguration(IpConfiguration(wifiConfig.getIpAssignment(), IpConfiguration.ProxySettings.NONE, wifiConfig.getStaticIpConfiguration(), null))
            saveConfiguration(wifiConfig!!)
            updateConfiguration(wifiConfig)
            disconnectWiFi()
            reconnectWiFi()
        }
        return true
    }


    private fun isWiFiDHCP(): Boolean {
        val dhcpInfo: DhcpInfo = wifiManager.dhcpInfo
        return dhcpInfo.leaseDuration != 0
    }


    fun checkPermissions(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

}