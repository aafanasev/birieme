package com.github.aafanasev.birieme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.SurfaceHolder
import android.view.WindowInsets
import com.github.aafanasev.birieme.mapper.TimeMapper
import com.github.aafanasev.birieme.renderer.BackgroundRenderer
import com.github.aafanasev.birieme.renderer.TimeRenderer
import java.util.*

class MyWatchFace : CanvasWatchFaceService() {

    companion object {
        private const val UPDATE_RATE_MS = 4000
        private const val MSG_UPDATE_TIME = 0
    }

    override fun onCreateEngine() = Engine()

    inner class Engine : CanvasWatchFaceService.Engine() {

        private lateinit var calendar: Calendar

        private lateinit var timeMapper: TimeMapper
        private lateinit var timeRenderer: TimeRenderer
        private lateinit var backgroundRenderer: BackgroundRenderer

        private var isAmbient: Boolean = false
        private var isLowBitAmbient: Boolean = false
        private var isBurnInProtection: Boolean = false
        private var isTimeZoneReceiverRegistered = false

        private val updateTimeHandler: Handler = EngineHandler(this, MSG_UPDATE_TIME)

        private val timeZoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                calendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            setWatchFaceStyle(WatchFaceStyle.Builder(this@MyWatchFace).build())

            calendar = Calendar.getInstance()

            applicationContext.let {
                timeMapper = TimeMapper(it)
                timeRenderer = TimeRenderer(it)
                backgroundRenderer = BackgroundRenderer(it)
            }
        }

        override fun onDestroy() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            isLowBitAmbient = properties.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false)
            isBurnInProtection = properties.getBoolean(WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false)
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            isAmbient = inAmbientMode

            if (isLowBitAmbient) {
                timeRenderer.updateAntiAlias(!inAmbientMode)
            }

            updateTimer()
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            val now = System.currentTimeMillis()
            calendar.timeInMillis = now

            backgroundRenderer.render(canvas, bounds, isAmbient)

            val hours = calendar.get(Calendar.HOUR)
            val minutes = calendar.get(Calendar.MINUTE)

            val lines = timeMapper.map(hours, minutes)
            timeRenderer.render(canvas, bounds, lines)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()

                calendar.timeZone = TimeZone.getDefault()

                invalidate()
            } else {
                unregisterReceiver()
            }

            updateTimer()
        }

        private fun registerReceiver() {
            if (isTimeZoneReceiverRegistered) {
                return
            }

            isTimeZoneReceiverRegistered = true
            registerReceiver(timeZoneReceiver, IntentFilter(Intent.ACTION_TIMEZONE_CHANGED))
        }

        private fun unregisterReceiver() {
            if (!isTimeZoneReceiverRegistered) {
                return
            }

            isTimeZoneReceiverRegistered = false
            unregisterReceiver(timeZoneReceiver)
        }

        override fun onApplyWindowInsets(insets: WindowInsets) {
            super.onApplyWindowInsets(insets)

            timeRenderer.updateTextSize(
                    primaryTextSize = resources.getDimension(R.dimen.text_primary),
                    secondaryTextSize = resources.getDimension(R.dimen.text_secondary)
            )
        }

        private fun updateTimer() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)

            if (shouldTimerBeRunning()) {
                updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        private fun shouldTimerBeRunning() = isVisible && !isInAmbientMode

        fun handleUpdateTimeMessage() {
            invalidate()

            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = UPDATE_RATE_MS - timeMs % UPDATE_RATE_MS
                updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }

    }

}
