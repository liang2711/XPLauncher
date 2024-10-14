package com.zhuoho.xplauncher.S

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Button
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.S.Composable.ConnectDialog
import com.zhuoho.xplauncher.S.Composable.CustomLinearProgerssBar
import com.zhuoho.xplauncher.data.Constant
import java.sql.Connection

@SuppressLint("StateFlowValueCalledInComposition")
@Preview
@Composable
fun mainBox(){
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 60.dp, top = 30.dp, bottom = 30.dp, end = 60.dp)
                .focusable(false),
            colors = CardDefaults.cardColors(containerColor = Color.Blue)
        ) {
            Box(
                modifier = Modifier,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 26.dp)
                        .fillMaxWidth()
                ) {
                    var option= arrayOf("无线设置","显示设置","高级设置","其他设置")
                    for (index in 0..3){
                        var isFocus by remember { mutableStateOf(false) }
                        val animationColor by animateColorAsState(targetValue = if (isFocus)Color.Black else Color.White)
                        Text(text = option[index],
                            textAlign = TextAlign.Center,
                            fontSize = 28.sp,
                            color = animationColor,
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged {
                                    isFocus = it.isFocused
                                    if (isFocus)
                                        Log.d(MainActivity.TAG, "焦点改变了")
                                }
                                .focusable()
                        )
                    }
                }
            }
            OnePage()
        }
    }

}