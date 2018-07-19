package com.github.aafanasev.birieme.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import com.github.aafanasev.birieme.R

class TimeRenderer(context: Context) {

    companion object {
        private const val LETTER_BIGGEST = "A"
        private const val LETTER_SMALLEST = "a"

        private val TYPEFACE_PRIMARY = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        private val TYPEFACE_SECONDARY = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private val textWidthCache = hashMapOf<String, Int>()

    private val paintTextPrimary = Paint().apply {
        typeface = TYPEFACE_PRIMARY
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.time_primary)
    }

    private val paintTextSecondary = Paint().apply {
        typeface = TYPEFACE_SECONDARY
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.time_secondary)
    }

    private val textPrimaryHeight by lazy(LazyThreadSafetyMode.NONE) {
        val tempRect = Rect()
        paintTextPrimary.getTextBounds(LETTER_BIGGEST, 0, 1, tempRect)
        tempRect.height()
    }

    private val textSecondaryHeight by lazy(LazyThreadSafetyMode.NONE) {
        val tempRect = Rect()
        paintTextSecondary.getTextBounds(LETTER_SMALLEST, 0, 1, tempRect)
        tempRect.height()
    }

    private val lineGap = context.resources.getDimensionPixelSize(R.dimen.line_gap)

    fun render(canvas: Canvas, bounds: Rect, lines: List<Line>) {
        val tempRect = Rect()
        var topOffset = (bounds.height() - getTextHeight(lines)) / 2

        lines.forEach {
            val paint: Paint
            val lineHeight: Int

            if (it is Line.PrimaryLine) {
                paint = paintTextPrimary
                lineHeight = textPrimaryHeight
            } else {
                paint = paintTextSecondary
                lineHeight = textSecondaryHeight
            }

            paint.getTextBounds(it.text, 0, it.text.length, tempRect)

            val lineWidth = getTextWidth(it.text, paint, tempRect)
            val leftOffset = (bounds.width() - lineWidth) / 2

            canvas.drawText(it.text, leftOffset.toFloat(), topOffset.toFloat() + lineHeight, paint)

            topOffset += lineHeight + lineGap
        }
    }

    fun updateAntiAlias(isAntiAlias: Boolean) {
        paintTextPrimary.isAntiAlias = isAntiAlias
        paintTextSecondary.isAntiAlias = isAntiAlias
    }

    fun updateTextSize(primaryTextSize: Float, secondaryTextSize: Float) {
        paintTextPrimary.textSize = primaryTextSize
        paintTextSecondary.textSize = secondaryTextSize
    }

    private fun getTextHeight(lines: List<Line>): Int {
        val textHeight = lines.sumBy {
            val lineHeight = if (it is Line.PrimaryLine) textPrimaryHeight else textSecondaryHeight
            lineHeight + lineGap
        }

        return textHeight - lineGap
    }

    private fun getTextWidth(text: String, paint: Paint, rect: Rect): Int {
        if (!textWidthCache.containsKey(text)) {
            paint.getTextBounds(text, 0, text.length, rect)
            textWidthCache[text] = rect.width()
        }

        return textWidthCache[text]!!
    }

    sealed class Line(val text: String) {
        class PrimaryLine(text: String) : Line(text)
        class SecondaryLine(text: String) : Line(text)
    }

}