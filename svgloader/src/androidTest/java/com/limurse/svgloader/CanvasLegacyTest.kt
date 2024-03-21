package com.limurse.svgloader

import android.graphics.Canvas
import com.limurse.svgloader.utils.CanvasLegacy
import org.junit.Test

class CanvasLegacyTest {
    @Test
    fun testSave() {
        val canvas = Canvas()
        CanvasLegacy.save(canvas, CanvasLegacy.MATRIX_SAVE_FLAG)
        canvas.restore()
    }
}