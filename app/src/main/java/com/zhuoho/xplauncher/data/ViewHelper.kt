package com.zhuoho.xplauncher.data

import android.app.ActivityManager
import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Process
import androidx.core.content.ContextCompat
import com.zhuoho.xplauncher.R

class ViewHelper {
    private lateinit var context:Context;
    constructor(context: Context){
        this.context=context
    }

    fun getList(isList:Boolean):List<AppInfo>{
        val mList:MutableList<AppInfo> = ArrayList()
        var apps:LauncherApps? = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        var manager:ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var list: MutableList<LauncherActivityInfo>? = apps?.getActivityList(null,Process.myUserHandle())
        var iconDpi = manager.launcherLargeIconDensity
        if (!isList)
            mList.add(AppInfo(activityName = "com.zhuoho.xplauncher.AppListActivity", pkgName = context.packageName, appIcon = ContextCompat.getDrawable(context,
            R.mipmap.ic_launcher), newInstall = false,appLabel="Apps"))
        if (list != null) {
            for (item:LauncherActivityInfo in list){
                var info = AppInfo(
                    activityName=item.name,
                    pkgName=item.componentName.packageName,
                    newInstall=false,
                    appLabel=item.label.toString(),
                    appIcon = item.getBadgedIcon(iconDpi))
                mList.add(info)
            }
        }
        return mList
    }

}