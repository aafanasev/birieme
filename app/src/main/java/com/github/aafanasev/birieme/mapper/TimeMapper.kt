package com.github.aafanasev.birieme.mapper

import android.content.Context
import com.github.aafanasev.birieme.R
import com.github.aafanasev.birieme.renderer.TimeRenderer
import com.github.aafanasev.birieme.renderer.TimeRenderer.Line.PrimaryLine
import com.github.aafanasev.birieme.renderer.TimeRenderer.Line.SecondaryLine

class TimeMapper(context: Context) {

    private val hoursArray = context.resources.getStringArray(R.array.hours)
    private val hoursInPast = context.resources.getStringArray(R.array.hours_in_past)
    private val minuteDecades = context.resources.getStringArray(R.array.minute_decades)
    private val minuteDecimals = context.resources.getStringArray(R.array.minute_decimals)

    private val wordExact = context.getString(R.string.word_exact)
    private val wordHalf = context.getString(R.string.word_half)
    private val wordPast = context.getString(R.string.word_past)
    private val wordBefore = context.getString(R.string.word_before)
    private val wordBefore2 = context.getString(R.string.word_before_2)

    fun map(hours: Int, minutes: Int): List<TimeRenderer.Line> = when {
        minutes == 0 -> {
            listOf(
                    PrimaryLine(hoursArray[hours]),
                    SecondaryLine(wordExact)
            )
        }
        minutes == 30 -> {
            listOf(
                    PrimaryLine(hoursArray[hours]),
                    SecondaryLine(wordHalf)
            )
        }
        minutes < 30 -> {
            listOf(
                    PrimaryLine(hoursInPast[hours]),
                    PrimaryLine(minutesAsStr(minutes)),
                    SecondaryLine(wordPast)
            )
        }
        minutes > 30 -> {
            val upcomingHour = if (hours == 11) 0 else hours + 1
            val minutesAsStr = minutesAsStr(60 - minutes)

            listOf(
                    PrimaryLine(hoursArray[upcomingHour]),
                    SecondaryLine(wordBefore),
                    PrimaryLine(minutesAsStr),
                    SecondaryLine(wordBefore2)
            )
        }
        else -> throw IllegalArgumentException("$hours:$minutes")
    }

    private fun minutesAsStr(minutes: Int): String {
        val decades = minutes / 10
        val decimals = minutes % 10

        return when {
            decades == 0 -> minuteDecimals[decimals - 1]
            decimals == 0 -> minuteDecades[decades - 1]
            else -> {
                val decadesAsStr = minuteDecades[decades - 1]
                val decimalsAsStr = minuteDecimals[decimals - 1]
                "$decadesAsStr $decimalsAsStr"
            }
        }
    }

}