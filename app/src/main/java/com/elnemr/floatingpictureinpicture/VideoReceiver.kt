package com.elnemr.floatingpictureinpicture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class VideoReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Clicked")
    }
}