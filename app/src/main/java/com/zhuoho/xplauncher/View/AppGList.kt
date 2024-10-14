package com.zhuoho.xplauncher.View

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.zhuoho.xplauncher.MainActivity
import com.zhuoho.xplauncher.data.AppInfo

class AppGList constructor(list:List<AppInfo>){
    private lateinit var list:List<AppInfo>;
    init {
        this.list=list
    }

    @Composable
    fun show(){
        if (list==null || list.size==0){
            Log.d(MainActivity.TAG,"AppGList list is null")
            return
        }
        //当?不被定义时，list不管有木有null都执行
        list?.let {
            TvLazyVerticalGrid(
                columns= TvGridCells.Fixed(5), contentPadding = PaddingValues(16.dp)
        ) {
               items(list.size){
                   item->
                   var isFocused by remember { mutableStateOf(false) }
                   val scale by animateFloatAsState(if (isFocused) 1.1f else 1.0f)
                   var appInfo=list.get(item)
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
                           .clickable( onClick = {},indication = null, interactionSource = remember { MutableInteractionSource() }),
                       shape = RoundedCornerShape(16.dp),
                   ) {
                       Column(
                           verticalArrangement = Arrangement.Center,
                           horizontalAlignment = Alignment.CenterHorizontally,
                           modifier = Modifier.fillMaxSize().background(Color(0x4D000000)),
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
                               fontSize = 16.sp,
                               maxLines = 1,
                               color = Color.White,
                               text = appInfo.appLabel
                           )
                        }
                   }
               }
            }
        }
    }
}