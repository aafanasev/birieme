package com.github.aafanasev.birieme.renderer

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
class BackgroundRendererTest {

    @Mock
    lateinit var canvas: Canvas

    lateinit var backgroundRenderer: BackgroundRenderer

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        backgroundRenderer = BackgroundRenderer(ShadowApplication.getInstance().applicationContext)
    }

    @Test
    fun `test ambient mode rendering`() {
        val rect = Rect().apply {
            bottom = 0
            top = 100
            left = 0
            right = 100
        }

        backgroundRenderer.render(canvas, rect, true)

        verify(canvas).drawColor(Color.BLACK)
    }

}