package com.zhuoho.xplauncher.S

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.zhuoho.xplauncher.MainActivity

class WifiScanWorker(context: Context,workerParams: WorkerParameters)
    : Worker(context,workerParams) {
    override fun doWork(): Result {
        Log.d(MainActivity.TAG,"开启扫描")
        if (MainActivity.wifiHelp.startScan())
            return Result.success()
        Log.e(MainActivity.TAG,"扫描失败")
        return Result.failure()
    }
}