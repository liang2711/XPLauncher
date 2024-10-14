package com.zhuoho.xplauncher

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import com.zhuoho.xplauncher.S.Composable.CustomLinearProgerssBar
import com.zhuoho.xplauncher.S.VMWiFiInfo
import com.zhuoho.xplauncher.S.WiFiInfo
import com.zhuoho.xplauncher.S.WifiHelp
import com.zhuoho.xplauncher.S.mainBox
import com.zhuoho.xplauncher.View.EndAppDisplay
import com.zhuoho.xplauncher.View.HeaderXY
import com.zhuoho.xplauncher.View.MainAppDisplay
import com.zhuoho.xplauncher.View.MainAppDisplayC
import com.zhuoho.xplauncher.data.AppInfo
import com.zhuoho.xplauncher.data.ViewHelper
import com.zhuoho.xplauncher.receiver.WifiBroadcastReceiver
import com.zhuoho.xplauncher.receiver.WifiConnectReceiver
import com.zhuoho.xplauncher.ui.theme.XPLauncherTheme

class MainActivity : ComponentActivity() {
    companion object {
        val TAG = "MainActivity"
        lateinit var context: Context
        lateinit var wifiHelp:WifiHelp
        fun init(context: Context) {
            this.context = context
            wifiHelp = WifiHelp(Companion.context)
        }

        private val holder by lazy { ViewHelper(context) }
//      public val wifiHelp: WifiHelp by lazy { WifiHelp(context) }
        fun getList(): List<AppInfo> {
            return holder.getList(false);
        }

        var COUNT = 0;
    }

    private lateinit var wifiBroadcastReceiver: WifiBroadcastReceiver

    private val viewModel by lazy { ViewModelProvider(this)[VMWiFiInfo::class.java] }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(this)
        wifiBroadcastReceiver = WifiBroadcastReceiver(
            updateState = viewModel::updateState,
            updateListofScanResult = viewModel::updateListofScanResult
        )

        val intentFilter = IntentFilter().apply {
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        }

        registerReceiver(wifiBroadcastReceiver, intentFilter)
        setContent {
            XPLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    colors = SurfaceDefaults.colors(containerColor = Color.Black)
                ) {
//                    noConnectDialog(WiFiInfo())
                    showSetting()
//                    CustomLinearProgerssBar()
//                    Test(false,false,true)
//                    FocusRequesterExample()
//                    showLauncher()
                }
            }
        }
    }

    @Composable
    fun showSetting() {
        mainBox()
    }

    @Composable
    fun showLauncher() {
        Column(
            modifier = Modifier
                .padding(top = 36.dp, start = 24.dp, end = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HeaderXY()
            MainAppDisplayC()
            EndAppDisplay()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiBroadcastReceiver)
    }
}