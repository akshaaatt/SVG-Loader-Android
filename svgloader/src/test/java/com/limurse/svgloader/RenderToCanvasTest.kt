package com.limurse.svgloader

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.os.Build
import com.limurse.svgloader.PreserveAspectRatio.Companion.of
import com.limurse.svgloader.RenderOptions.Companion.create
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.JELLY_BEAN],
    shadows = [MockCanvas::class, MockPath::class, MockPaint::class]
)
class RenderToCanvasTest {
    @Test
    @Throws(SVGParseException::class)
    fun renderToCanvas() {
        val test = """<svg viewBox="0 0 200 100">
  <rect width="200" height="100" fill="green"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val bm1 = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val bmcanvas1 = Canvas(bm1)
        svg.renderToCanvas(bmcanvas1)
        val ops = (Shadow.extract<Any>(bmcanvas1) as MockCanvas).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(1 0 0 1 0 50))", ops[1])
        Assert.assertEquals(
            "drawPath('M 0 0 L 200 0 L 200 100 L 0 100 L 0 0 Z', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; ts:16; tf:android.graphics.Typeface@0; color:#ff008000))",
            ops[3]
        )
    }

    @Test
    @Throws(SVGParseException::class)
    fun renderToCanvasWithViewPort() {
        val test = """<svg viewBox="0 0 200 100">
  <rect width="200" height="100" fill="green"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val bm2 = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val bmcanvas2 = Canvas(bm2)
        svg.renderToCanvas(bmcanvas2, RectF(50f, 50f, 150f, 150f))
        val ops = (Shadow.extract<Any>(bmcanvas2) as MockCanvas).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(0.5 0 0 0.5 50 75))", ops[1])
        Assert.assertEquals(
            "drawPath('M 0 0 L 200 0 L 200 100 L 0 100 L 0 0 Z', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; ts:16; tf:android.graphics.Typeface@0; color:#ff008000))",
            ops[3]
        )
    }

    //--------------------------------------------------------------------------
    @Test
    @Throws(SVGParseException::class)
    fun renderViewToCanvas() {
        val test = """<svg viewBox="0 0 100 100">
  <view id="test" viewBox="25 25 50 50"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val bm1 = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val bmcanvas1 = Canvas(bm1)
        svg.renderViewToCanvas("test", bmcanvas1)
        val ops = (Shadow.extract<Any>(bmcanvas1) as MockCanvas).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(4 0 0 4 -100 -100))", ops[1])
    }

    @Test
    @Throws(SVGParseException::class)
    fun renderViewToCanvasViewPort() {
        val test = """<svg viewBox="0 0 100 100">
  <view id="test" viewBox="25 25 50 50"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val bm1 = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val bmcanvas1 = Canvas(bm1)
        svg.renderViewToCanvas("test", bmcanvas1, RectF(100f, 100f, 200f, 200f))
        val ops = (Shadow.extract<Any>(bmcanvas1) as MockCanvas).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(2 0 0 2 50 50))", ops[1])
    }

    //--------------------------------------------------------------------------
    @Test
    @Throws(SVGParseException::class)
    fun renderToCanvasWithViewPortRO() {
        val test = """<svg viewBox="0 0 200 100">
  <rect width="200" height="100" fill="green"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val bm2 = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val bmcanvas2 = Canvas(bm2)
        val opts = create().viewPort(100f, 100f, 100f, 50f)
        svg.renderToCanvas(bmcanvas2, opts)
        val ops = (Shadow.extract<Any>(bmcanvas2) as MockCanvas).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(0.5 0 0 0.5 100 100))", ops[1])
        Assert.assertEquals(
            "drawPath('M 0 0 L 200 0 L 200 100 L 0 100 L 0 0 Z', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; ts:16; tf:android.graphics.Typeface@0; color:#ff008000))",
            ops[3]
        )
    }

    @Test
    @Throws(SVGParseException::class)
    fun renderToCanvasRO() {
        val test = """
            <svg>
            </svg>
            """.trimIndent()
        val svg = SVG.getFromString(test)
        val bm2 = Bitmap.createBitmap(200, 300, Bitmap.Config.ARGB_8888)
        val bmcanvas2 = Canvas(bm2)

        // Step 1
        var opts = create()
        opts.viewPort(0f, 0f, 200f, 300f)
            .viewBox(0f, 0f, 100f, 50f)
        svg.renderToCanvas(bmcanvas2, opts)
        val mock = Shadow.extract<MockCanvas>(bmcanvas2)
        val ops = mock.operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(2 0 0 2 0 100))", ops[1])

        // Step 2
        mock.clearOperations()
        opts = create()
        opts.viewPort(0f, 0f, 200f, 300f)
            .viewBox(0f, 0f, 100f, 50f)
            .preserveAspectRatio(of("xMinYMax meet"))
        svg.renderToCanvas(bmcanvas2, opts)

        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(2 0 0 2 0 200))", ops[1])

        // Step 3
        mock.clearOperations()
        opts = create()
        opts.viewPort(0f, 0f, 200f, 300f)
            .viewBox(0f, 0f, 100f, 50f)
            .preserveAspectRatio(of("none"))
        svg.renderToCanvas(bmcanvas2, opts)

        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(2 0 0 6 0 0))", ops[1])
    }
}
