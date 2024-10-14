package com.zhuoho.xplauncher.View

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.ExoPlayer
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceColors
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.contentColorFor
import com.zhuoho.xplauncher.AppListActivity
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.R
import com.zhuoho.xplauncher.data.AppInfo
import com.zhuoho.xplauncher.receiver.AppInstallReceiver
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HeaderXY() {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularImage()
            Spacer(modifier = Modifier.width(16.dp))
            DateTimeDisplay()
        }
        Image(
            alignment = Alignment.Center,
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 24.dp)
                .size(32.dp)
                .align(Alignment.CenterStart)
        )
    }
}

@Composable
private fun CircularImage() {
    for (index in 1 until 8) {
        var isFocus by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(targetValue = if (isFocus) 1.2f else 1f)
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(32.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color(0x4D000000))
                .onFocusChanged {
                    isFocus = it.isFocused
                    if (it.isFocused) {
                        Log.d(MainActivity.TAG, "焦点 ${index}")
                    }
                }
                .focusable()
                .clickable {
                    Log.d(MainActivity.TAG, "点击了")
                }
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun DateTimeDisplay() {
    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = LocalDateTime.now()
            delay(1000L) // 每秒更新一次时间
        }
    }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    val amPm = if (currentDateTime.hour < 12) "AM" else "PM"
    val todayDate = currentDateTime.toLocalDate().format(dateFormatter)
    val dayOfWeek = currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${currentDateTime.format(timeFormatter)} ${amPm}",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 26.sp
        )
        VerticalDivider(
            modifier = Modifier
                .padding(6.dp)
                .height(26.dp),
            thickness = 2.dp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${todayDate}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
            )
            Text(
                text = "${dayOfWeek}",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
            )
        }
    }
}

@Composable
fun MainAppDisplayC(){
    var context= LocalContext.current
    var installMessage by remember { mutableStateOf("no new app") }
    val items:MutableSet<AppInfo> by remember { mutableStateOf(mutableSetOf<AppInfo>()) }
    var list=MainActivity.getList();
    if (items==null){
        return
    }
    DisposableEffect(Unit) {
        val receiver=AppInstallReceiver{
            installMessage="new app install"
        }
        val intentFilter=IntentFilter(Intent.ACTION_PACKAGE_ADDED).apply {
            addDataScheme("package")

        }
        context.registerReceiver(receiver,intentFilter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
    if (items.size>=4)return
    for(item in list){
        when(item.pkgName){
            //这个+是listOf特有的是操作数重载，+=add和addall
            "com.google.android.youtube.tv"->{items.add(item)}
            "com.android.vending"->{items.add(item)}
            "com.android.androidtools"->{items.add(item)}
            "com.netflix.ninja"->{items.add(item)}
            else->{}
        }
    }
    if(items==null) return
    MainAppDisplay(items.toList())
}

@Composable
fun MainAppDisplay(items:List<AppInfo>?) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        for (index in 1..5) {
            var isF by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(if (isF)1.1f else 1f)
            if (index == 1) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .weight(2.5f)
                        .scale(scale)
                        .padding(start = 16.dp, end = 16.dp)
                        .onFocusChanged {
                            isF = it.isFocused;
                            if (isF) {
                                Log.d(MainActivity.TAG, "vod")
                            }
                        }
                        .focusable()
                        .clickable {
                            Log.d(MainActivity.TAG, "vod click")
                        },

                ) {
                    Text(
                        text = "text1",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxSize()
                    )
                }
                continue
            }
            var item:AppInfo?
            if (items!!.size>(index-2)){
                item=items!!.get(index-2)
            }else item=null
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(contentColor = Color.Transparent),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .weight(1f)
                    .scale(scale)
                    .padding(end = 16.dp)
                    .border(
                        width = if (isF) 2.dp else 0.dp,
                        color = if (isF) Color.White else Color.Transparent,
                    )
                    .onFocusChanged {
                        isF = it.isFocused;
                        if (isF) {
                            Log.d(MainActivity.TAG, "vod")
                        }
                    }
                    .focusable()
                    .clickable {
                        Log.d(MainActivity.TAG, "vod click")
                    },
            ) {
                if (item==null){
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                            .background(Brush.linearGradient(listOf(Color.White, Color.Black))),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(72.dp).clickable {  },
                        )
                    }
                    return@Card
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                        .background(Brush.linearGradient(listOf(Color.Black, Color.Green))),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = Modifier.size(62.dp),
                        painter = BitmapPainter(item.appIcon!!.toBitmap().asImageBitmap()),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = item.appLabel,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun EndAppDisplay(){
    var context= LocalContext.current
    var installMessage by remember { mutableStateOf("no new app") }
    var items:List<AppInfo>?=MainActivity.getList()
//    LaunchedEffect(Unit) {
//        items=MainActivity.getList()
//    }

    if (items==null){
        return
    }
    DisposableEffect(Unit) {
        val receiver=AppInstallReceiver{
            installMessage="new app install"
        }
        val intentFilter=IntentFilter(Intent.ACTION_PACKAGE_ADDED).apply {
            addDataScheme("package")

        }
        context.registerReceiver(receiver,intentFilter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
    RowListView(context,items!!)
}

@Composable
fun RowListView(context: Context,items:List<AppInfo>){
    Box(modifier = Modifier.fillMaxWidth()){
        TvLazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(items.size) { item ->
                var isFocused by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(if (isFocused) 1.1f else 1.0f)
                var appInfo=items.get(item)
                Surface(
                    modifier = Modifier
                        .size(width = 150.dp, height = 180.dp)
                        .padding(8.dp)
                        .onFocusChanged { isFocused = it.isFocused }
                        .scale(scale)
                        .focusable()
                        .border(
                            width = if (isFocused) 6.dp else 0.dp,
                            color = if (isFocused) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(16.dp),
                        )
                        .clickable(
                            onClick = {
                                val intent: Intent
                                if (appInfo.activityName.contains("com.zhuoho.xplauncher.AppListActivity") || appInfo.pkgName.equals(
                                        context.packageName
                                    )
                                ) {
                                    intent = Intent(context, AppListActivity::class.java)
                                } else intent = Intent().setComponent(
                                    ComponentName(
                                        appInfo.pkgName,
                                        appInfo.activityName
                                    )
                                )
                                context.startActivity(intent)
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x4D000000)),
                    ) {
                        Image(
                            painter = BitmapPainter(appInfo.appIcon!!.toBitmap().asImageBitmap()),
                            contentDescription = null,
                            modifier = Modifier.size(62.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            modifier = Modifier.padding(top = 6.dp),
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            maxLines = 1,
                            color = Color.White,
                            text = appInfo.appLabel
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .height(200.dp)
                .width(16.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.Black)
                    )
                )
                .align(Alignment.CenterEnd)
                .padding(top = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
fun VideoPlayer(
    data: String?,
    modifier: Modifier = Modifier,
    isFixHeight: Boolean = false,
    useExoController: Boolean = false,
    cache: Cache? = null,
    onSingleTap: (exoPlayer: ExoPlayer) -> Unit = {},
    onDoubleTap: (exoPlayer: ExoPlayer, offset: Offset) -> Unit = { _, _ -> },
    onVideoDispose: () -> Unit = {},
    onVideoGoBackground: () -> Unit = {}
){
    val context= LocalContext
}



