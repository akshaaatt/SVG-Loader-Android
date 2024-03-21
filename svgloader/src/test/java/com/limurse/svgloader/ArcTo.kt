package com.limurse.svgloader

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.KITKAT],
    shadows = [MockCanvas::class, MockPath::class, MockPaint::class]
)
class ArcTo {
    @Test
    @Throws(SVGParseException::class)
    fun testIssue155() {
        val test = "<svg>" +
                "  <path d=\"M 163.637 412.021 a 646225.813 646225.813 0 0 1 -36.313 162\"/>" +
                "</svg>"
        val svg = SVG.getFromString(test)
        val newBM = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBM)
        svg.renderToCanvas(canvas)
        val ops = (Shadow.extract<Any>(canvas) as MockCanvas).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(6, ops.size.toLong())
        Assert.assertEquals(
            "drawPath('M 163.63701 412.02103 C 151.5 466.03125 139.375 520.0156 127.32401 574.021', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; color:#ff000000; ts:16; tf:android.graphics.Typeface@0))",
            ops[3]
        )
    }

    @Test
    @Throws(SVGParseException::class)
    fun testIssue156() {
        val test = "<svg>" +
                "  <path d=\"M 422.776 332.659 a 539896.23 539896.23 0 0 0-22.855-26.558\"/>" +
                "</svg>"
        val svg = SVG.getFromString(test)
        val newBM = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBM)
        svg.renderToCanvas(canvas)
        val ops = (Shadow.extract<Any>(canvas) as MockCanvas).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(6, ops.size.toLong())
        Assert.assertEquals(
            "drawPath('M 422.77603 332.65903 C 415.15625 323.8125 407.53125 314.96875 399.92102 306.101', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; color:#ff000000; ts:16; tf:android.graphics.Typeface@0))",
            ops[3]
        )
    }
}
