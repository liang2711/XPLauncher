package com.zhuoho.xplauncher.data

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable

data class AppInfo(
    val activityName: String,
    val appIcon: Drawable?,
    val pkgName:String,
    val newInstall:Boolean,
    val appLabel:String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        TODO("appIcon"),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(activityName)
        parcel.writeString(pkgName)
        parcel.writeByte(if (newInstall) 1 else 0)
        parcel.writeString(appLabel)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppInfo> {
        override fun createFromParcel(parcel: Parcel): AppInfo {
            return AppInfo(parcel)
        }

        override fun newArray(size: Int): Array<AppInfo?> {
            return arrayOfNulls(size)
        }
    }
}
