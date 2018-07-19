package com.github.aafanasev.birieme.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import com.github.aafanasev.birieme.R

class BackgroundRenderer(context: Context) {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.background)
    }

    fun render(canvas: Canvas, bounds: Rect, isAmbient: Boolean) {
        if (isAmbient) {
            canvas.drawColor(Color.BLACK)
        } else {
            canvas.drawRect(0f, 0f, bounds.width().toFloat(), bounds.height().toFloat(), paint)
        }
    }

}