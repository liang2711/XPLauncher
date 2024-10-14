package com.zhuoho.xplauncher.S

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Switch
import androidx.tv.material3.SwitchDefaults
import androidx.tv.material3.Text
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.S.Composable.ConnectDialog
import com.zhuoho.xplauncher.S.Composable.CustomLinearProgerssBar
import com.zhuoho.xplauncher.S.Composable.DisPlayDialog
import com.zhuoho.xplauncher.S.Composable.connectReceiver
import com.zhuoho.xplauncher.data.Constant
import java.io.File

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun OnePage(){
    Box(){
        val viewModel: VMWiFiInfo = viewModel()
        val stateBar=viewModel.stateFlow
        Column(Modifier.fillMaxWidth()) {
            val viewModel: VMWiFiInfo = viewModel()
            //wifi开关
            var checkedIsConnection by remember { mutableStateOf(MainActivity.wifiHelp.isWifiEnable()) }
            //WiFi数据
            val updateWifi by viewModel.srFlow.collectAsState()
//        LaunchedEffect(key1 = updateWifi) {
//            Log.d("OnePage","  updateWifis length ${updateWifi.size}")
//        }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var isFocus by remember { mutableStateOf(false) }
                Text(
                    modifier = Modifier.padding(end = 5.dp),
                    color = Color.Black,
                    text = "Wifi",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
                Switch(
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = if (isFocus) Color.Green else Color.Red,
                        uncheckedThumbColor = if (isFocus) Color.Green else Color.Red
                    ),checked = checkedIsConnection, onCheckedChange = {
                        if (MainActivity.wifiHelp.setWifiEnable(it)){
                            checkedIsConnection=it
                            updateWifi.isNewData=false
                            Log.d(MainActivity.TAG,"${checkedIsConnection} change!!")
                        }
                    }, modifier = Modifier
                        .onFocusChanged {
                            isFocus = it.isFocused
                            if (isFocus)
                                Log.d(MainActivity.TAG, "焦点改变了")

                        }
                        .focusable(true))
            }
            Log.d(Constant.TAG,"Column------- ${checkedIsConnection}")
            //WiFi开关是否打开，updateWifi是否更新
            if (!checkedIsConnection||!updateWifi.isNewData)return
            Column(
                modifier = Modifier
                    .height(360.dp)
                    .verticalScroll(rememberScrollState()),
            ) {

                if (updateWifi==null || updateWifi.wiFiInfos.size==0)
                    return@Column

                for (index in updateWifi.wiFiInfos){
                    var isFocus by remember { mutableStateOf(false) }
                    var onConnect= remember { mutableStateOf(false) }
                    Text(
                        color = if (isFocus)Color.Black else Color.White,
                        text = "SSID:${index.scanResult.SSID} BSSID:${index.scanResult.BSSID}  level:${index.level} isC${index.isConnect} save:${index.isSaved} " +
                                "pwd:${index.wiFiSecurity} w:${if(index.wiFiInfo==null)"null" else "exis"}",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .onFocusChanged {
                                isFocus = it.isFocused
                                if (isFocus)
                                    Log.d(MainActivity.TAG, "焦点改变了")
                            }
                            .focusable()
                            .clickable {
                                onConnect.value = !onConnect.value
                            },
                    )
                    DisPlayDialog(onConnect.value, index){
                        onConnect.value = false
                        MainActivity.wifiHelp.startScan()
                    }
                }

            }
        }
        Log.d("mainBox","stateBar ${stateBar.value}")
        if (stateBar.value){
            CustomLinearProgerssBar()
        }
    }
}


@Composable
fun TwoPage(){

}
@Composable
fun ThreePage(){

}
@Composable
fun FourPage(){

}