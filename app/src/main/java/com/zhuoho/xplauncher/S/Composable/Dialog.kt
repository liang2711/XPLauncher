package com.zhuoho.xplauncher.S.Composable

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.CheckBox
import android.widget.TabHost
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.registerReceiver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.S.UITool
import com.zhuoho.xplauncher.S.VMWiFiInfo
import com.zhuoho.xplauncher.S.WiFiInfo
import com.zhuoho.xplauncher.S.WiFiSecurity
import com.zhuoho.xplauncher.S.WifiHelp
import com.zhuoho.xplauncher.S.Wireless
import com.zhuoho.xplauncher.data.Constant
import com.zhuoho.xplauncher.receiver.WifiConnectReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var connectReceiver:WifiConnectReceiver?=null

@Composable
fun ConnectDialog(visible:Boolean,
                  wiFiInfo: WiFiInfo,
                  onDismiss: () -> Unit = {}){
    Log.d(Constant.TAG,"ConnectDialog  ${wiFiInfo.scanResult.SSID}")
    val viewModel: VMWiFiInfo = viewModel()
    val context= LocalContext.current
    var isEnd=false
    val scope = rememberCoroutineScope()
    val intentFilter = IntentFilter().apply {
        addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
    }
    var isRegisterReceiverFlags=false

    Dialog(onDismissRequest = {
        onDismiss()
        if (connectReceiver!=null&&isRegisterReceiverFlags)context.unregisterReceiver(connectReceiver)
    }) {
        Surface(
            shape = RoundedCornerShape(16.dp), // 设置圆角
            shadowElevation = 8.dp, // 设置阴影高度
            modifier = Modifier
                .size(width = 200.dp, height = 120.dp)
                .graphicsLayer { // 使用 graphicsLayer 可以更灵活地设置阴影
                    shadowElevation = 8.dp.toPx()
                    shape = RoundedCornerShape(16.dp)
                    clip = true
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                var inputText by remember { mutableStateOf("") }
                //如果当前wifi没有密码且没有保存的WiFi显示设置密码框
                if (wiFiInfo.wiFiSecurity!=WiFiSecurity.NONE&&!wiFiInfo.isSaved){
                    BasicTextField(value = inputText,
                        onValueChange = {
                            inputText=it
                        },
                        decorationBox = {
                                innerTextField ->
                            Box{
                                Surface(shape = RoundedCornerShape(8.dp)) {
                                    Row(
                                        Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
                                            if (inputText.isEmpty()) Text(text = "password", color = Color(0x88000000))
                                            innerTextField()
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                Button(onClick = {
                    connectReceiver=WifiConnectReceiver(wiFiInfo.scanResult.BSSID){
                        if (it){
                            viewModel.updateState(true)
//                            MainActivity.wifiHelp.startScan()
//                            WifiHelp(context).startScan()
//                            viewModel.updateListofScanResult()
//                            Toast.makeText(context,"连接成功",Toast.LENGTH_SHORT).show()
                        }else{
//                            Toast.makeText(context,"连接失败",Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                        isEnd=true
                    }
                    context.registerReceiver(connectReceiver,intentFilter)
                    var isConnect=false
                    if (wiFiInfo.isSaved){
                        isConnect=MainActivity.wifiHelp.connectSavedWiFi(wiFiInfo.scanResult.SSID)
                        if (isConnect){
                            isRegisterReceiverFlags=true
                            Log.d(Constant.TAG,"ConnectDialog  saved=${wiFiInfo.isSaved}")
                        }
                    }else{
                        var networkId=0
                        if (wiFiInfo.wiFiSecurity == WiFiSecurity.NONE){
                            networkId= MainActivity.wifiHelp.createNoPwConfig(wiFiInfo.scanResult.SSID)
                        }else{
                            if (inputText.isEmpty()){
                                Toast.makeText(context,"请输入密码",Toast.LENGTH_SHORT).show()
                            }
                            networkId= MainActivity.wifiHelp.createWiFiConfig(wiFiInfo,inputText)
                        }
                        isConnect=MainActivity.wifiHelp.enableNetwork(networkId,true)
                        if (isConnect){
                            isRegisterReceiverFlags=true
                            Log.d(Constant.TAG,"ConnectDialog  ${wiFiInfo.wiFiSecurity}")
                        }
                    }
                    UITool.setTimer(20000){
                        if(visible){
//                            Toast.makeText(context,"连接失败",Toast.LENGTH_SHORT).show()
                            onDismiss()
                        }
                    }
                },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color.Transparent)
                ){
                    Text(text = "连接")
                }
                //如果保存了有忘记选项
                if (wiFiInfo.isSaved){
                    Button(onClick = {
                        var configs=MainActivity.wifiHelp.getWiFiConfig(wiFiInfo.scanResult.SSID)
                        MainActivity.wifiHelp.forgetWiFi(configs)
                        MainActivity.wifiHelp.startScan()
                        onDismiss()
                    },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(Color.Transparent)){
                        Text(text = "忘记")
                    }
                }
            }
        }
    }
}

@Composable
fun WiFiInfoDialog(wiFiInfo: WiFiInfo,
                   onDismiss: () -> Unit = {}){
//    var w=MainActivity.wifiHelp.getWiFiInfo(wiFiInfo)
    val scope = rememberCoroutineScope()
    val context= LocalContext.current
    var w by remember {
        mutableStateOf(MainActivity.wifiHelp.getWiFiInfo(wiFiInfo))
    }
    Log.d(Constant.TAG,w.toString())
    var isDhcp by remember {
        mutableStateOf(w.isDhcp)
    }
    var padd=8.dp
    Box(
        modifier = Modifier.size(240.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            w.ipv4?.let { Text(text = "ipv4: $it") }
            Spacer(modifier = Modifier.width(padd))
            w.SSID?.let { Text(text = "SSID: $it") }
            Spacer(modifier = Modifier.width(padd))
            w.BSSID?.let { Text(text = "BSSID: $it") }
            Spacer(modifier = Modifier.width(padd))
            w.gateway?.let { Text(text = "gateway: $it") }
            Spacer(modifier = Modifier.width(padd))
            w.dns1?.let { Text(text = "dns1: $it") }
            Spacer(modifier = Modifier.width(padd))
            w.dns2?.let { Text(text = "dns2: $it") }
            Spacer(modifier = Modifier.width(padd))
            Row() {
                Checkbox(checked = isDhcp, onCheckedChange = {
                    isDhcp=true
                })
                Text(text = "DHCP")
                Spacer(modifier = Modifier.width(30.dp))
                Checkbox(checked = !isDhcp, onCheckedChange = {
                    isDhcp=false
                })
                Text(text = "STATIC")
            }
            Row {
                Button(onClick = {
                    var wp=Wireless(w.SSID,w.BSSID,false,
                        "192.168.1.128","255.255.255.0","192.168.1.1",
                        "192.168.1.1","192.168.1.1")
                    w=wp
                    isDhcp=false
                    onDismiss()
                    scope.launch {
                        MainActivity.wifiHelp.setStaticIp(wp)
                        MainActivity.wifiHelp.startScan()
//                        var isStaticIpConfigured=MainActivity.wifiHelp.isStaticIpConfigured(wp)
//                        if (isStaticIpConfigured){
//                            Toast.makeText(context,"设置静态ip成功",Toast.LENGTH_SHORT).show()
//                        }else{
//                            Toast.makeText(context,"设置静态ip失败",Toast.LENGTH_SHORT).show()
//                        }
                    }
                }) {
                    Text(text = "设置静态的IP")
                }
                Button(onClick = {
                    isDhcp=true
                    onDismiss()
                    scope.launch{
                        MainActivity.wifiHelp.setWiFiWithDHCP(false,w)
                        MainActivity.wifiHelp.startScan()
//                        var isDynamicIpConfigured=MainActivity.wifiHelp.isDynamicIpConfigured(w)
//                        if (isDynamicIpConfigured){
//                            Toast.makeText(context,"设置动态ip成功",Toast.LENGTH_SHORT).show()
//                        }else{
//                            Toast.makeText(context,"设置动态ip失败",Toast.LENGTH_SHORT).show()
//                        }
                    }
                }) {
                    Text(text = "设置动态的IP")
                }
            }
        }
    }
}

@Composable
fun DisPlayDialog(visible:Boolean,
           wiFiInfo: WiFiInfo,
           onDismiss: () -> Unit = {}){
    if (!visible)return
    if (wiFiInfo.isConnect)WiFiInfoDialog(wiFiInfo,onDismiss)
        else ConnectDialog(visible,wiFiInfo,onDismiss)
}