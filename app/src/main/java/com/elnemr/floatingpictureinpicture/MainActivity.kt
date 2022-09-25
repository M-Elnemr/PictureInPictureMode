package com.elnemr.floatingpictureinpicture

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val isPipSupported by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(
                PackageManager.FEATURE_PICTURE_IN_PICTURE
            )
        } else false
    }

    private var sourceRectHint = Rect()
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        videoView = findViewById<VideoView>(R.id.video)

        videoView?.let {
            it.setVideoURI(Uri.parse("android.resource://$packageName/${R.raw.sample}"))
            it.start()
            it.addOnLayoutChangeListener { _, left, top, right, bottom,
                                                   oldLeft, oldTop, oldRight, oldBottom ->
                if (left != oldLeft || right != oldRight || top != oldTop
                    || bottom != oldBottom) {
                    it.getGlobalVisibleRect(sourceRectHint)
                }
            }
        }

        findViewById<View>(R.id.show).setOnClickListener {
            if (isPipSupported) updatePipParams()?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enterPictureInPictureMode(it)
                }
            }
        }
    }

    private fun updatePipParams(): PictureInPictureParams? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams.Builder()
                .setSourceRectHint(sourceRectHint)
                .setAspectRatio(Rational(16, 9))
                .setActions(
                    listOf(
                        RemoteAction(
                            Icon.createWithResource(
                                applicationContext,
                                R.drawable.ic_launcher_foreground
                            ),
                            "play",
                            "play",
                            PendingIntent.getBroadcast(
                                applicationContext,
                                0,
                                Intent(applicationContext, VideoReceiver::class.java),
                                PendingIntent.FLAG_IMMUTABLE
                            )

                        )
                    )
                )
                .build()
        } else {
            null
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (isPipSupported) updatePipParams()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureInPictureMode(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }
}