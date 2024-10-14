package com.zhuoho.xplauncher

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.tv.material3.Surface
import com.zhuoho.xplauncher.View.AppGList
import com.zhuoho.xplauncher.data.AppInfo
import com.zhuoho.xplauncher.data.ViewHelper
import com.zhuoho.xplauncher.ui.theme.XPLauncherTheme

class AppListActivity: ComponentActivity() {
    val holder by lazy { ViewHelper(this) }
    val list:List<AppInfo> by lazy { holder.getList(true) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(){
            XPLauncherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ){
                    AppGList(list).show()
                }
            }
        }
    }
}