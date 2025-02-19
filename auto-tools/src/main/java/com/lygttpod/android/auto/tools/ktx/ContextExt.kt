package com.lygttpod.android.auto.tools.ktx

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.provider.Settings
import com.lygttpod.android.auto.tools.manager.FloatManager

fun Context?.gotoOverlayPermission() {
    this ?: return
    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    intent.data = Uri.parse("package:" + this.applicationContext.packageName)
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
    this.startActivity(intent)
}

fun Context?.canDrawOverlays(): Boolean {
    this ?: return false
    return Settings.canDrawOverlays(this)
}

fun Context?.showPrintFloat(application: Application) {
    this ?: return
    if (canDrawOverlays()) {
        FloatManager.showPrintFloat(application)
    } else {
        gotoOverlayPermission()
    }
}

