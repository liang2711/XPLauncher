package com.zhuoho.xplauncher.S.Composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomLinearProgerssBar(){
    Box(modifier = Modifier.fillMaxSize().background(Color(0x4D000000))){
        Column(modifier = Modifier.fillMaxWidth().align(Alignment.Center)) {
            LinearProgressIndicator(modifier = Modifier
                .fillMaxWidth()
                .height(7.dp),
                color = Color.Red,
                trackColor = Color.LightGray
            )
        }
    }
}