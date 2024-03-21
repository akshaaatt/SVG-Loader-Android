
package com.limurse.svgloader

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.ceil

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.JELLY_BEAN],
    shadows = [MockCanvas::class, MockPath::class, MockPaint::class]
)
class RenderTest {
    /*
    * Checks that calling renderToCanvas() does not have any side effects for the Canvas object.
    * See Issue #50. https://github.com/BigBadaboom/androidsvg/issues/50
    */
    @Test
    @Throws(SVGParseException::class)
    fun renderToCanvasPreservesState() {
        //disableLogging();
        val test =
            "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100\" height=\"100\" viewBox=\"0 0 20 20\">" +
                    "  <circle cx=\"10\" cy=\"10\" r=\"10\" transform=\"scale(2)\"/>" +
                    "  <g transform=\"rotate(45)\"></g>" +
                    "</svg>"
        val svg = SVG.getFromString(test)
        val newBM = Bitmap.createBitmap(
            ceil(svg.documentWidth.toDouble()).toInt(),
            ceil(svg.documentHeight.toDouble()).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBM)
        val beforeMatrix = canvas.getMatrix()
        val beforeClip = canvas.getClipBounds()
        val beforeSaves = canvas.saveCount

        //canvas.save(); canvas.scale(2f, 2f); canvas.restore();
        svg.renderToCanvas(canvas)
        val afterMatrix = canvas.getMatrix()
        Assert.assertEquals(beforeMatrix, afterMatrix)
        Assert.assertTrue(beforeMatrix.isIdentity)
        Assert.assertTrue(afterMatrix.isIdentity)
        val afterClip = canvas.getClipBounds()
        Assert.assertEquals(beforeClip, afterClip)
        val afterSaves = canvas.saveCount
        Assert.assertEquals(beforeSaves.toLong(), afterSaves.toLong())
    }
}
