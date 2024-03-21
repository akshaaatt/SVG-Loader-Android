package com.limurse.svgloader

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
    shadows = [MockCanvas::class, MockPath::class, MockPicture::class, MockPaint::class]
)
class RenderToPictureTest {
    @Test
    @Throws(SVGParseException::class)
    fun renderToPicture() {
        val test = """<svg viewBox="0 0 200 100">
  <rect width="200" height="100" fill="green"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val picture = svg.renderToPicture()
        val ops = (Shadow.extract<Any>(picture) as MockPicture).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(512, picture.getWidth().toLong())
        Assert.assertEquals(512, picture.getHeight().toLong())
        Assert.assertEquals("concat(Matrix(2.56 0 0 2.56 0 128))", ops[1])
        Assert.assertEquals(
            "drawPath('M 0 0 L 200 0 L 200 100 L 0 100 L 0 0 Z', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; ts:16; tf:android.graphics.Typeface@0; color:#ff008000))",
            ops[3]
        )
    }

    @Test
    @Throws(SVGParseException::class)
    fun renderToPictureIntrinsic() {
        // Calc height of picture given only width
        var test = """<svg width="400" viewBox="0 0 200 100">
</svg>"""
        var svg = SVG.getFromString(test)
        var picture = svg.renderToPicture()
        var ops = (Shadow.extract<Any>(picture) as MockPicture).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(400, picture.getWidth().toLong())
        Assert.assertEquals(200, picture.getHeight().toLong())
        Assert.assertEquals("concat(Matrix(2 0 0 2 0 0))", ops[1])

        // Calc width of picture given only height
        test = """<svg height="400" viewBox="0 0 200 100">
</svg>"""
        svg = SVG.getFromString(test)
        picture = svg.renderToPicture()
        ops = (Shadow.extract<Any>(picture) as MockPicture).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(800, picture.getWidth().toLong())
        Assert.assertEquals(400, picture.getHeight().toLong())
        Assert.assertEquals("concat(Matrix(4 0 0 4 0 0))", ops[1])
    }

    @Test
    @Throws(SVGParseException::class)
    fun renderToPictureWithDims() {
        val test = """<svg viewBox="0 0 200 100">
  <rect width="200" height="100" fill="green"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val picture = svg.renderToPicture(400, 400)
        val ops = (Shadow.extract<Any>(picture) as MockPicture).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(400, picture.getWidth().toLong())
        Assert.assertEquals(400, picture.getHeight().toLong())
        Assert.assertEquals("concat(Matrix(2 0 0 2 0 100))", ops[1])
        Assert.assertEquals(
            "drawPath('M 0 0 L 200 0 L 200 100 L 0 100 L 0 0 Z', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; ts:16; tf:android.graphics.Typeface@0; color:#ff008000))",
            ops[3]
        )
    }

    //--------------------------------------------------------------------------
    @Test
    @Throws(SVGParseException::class)
    fun renderViewToPicture() {
        val test = """<svg viewBox="0 0 100 100">
  <view id="test" viewBox="25 25 50 50"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val picture = svg.renderViewToPicture("test", 200, 300)
        val ops = (Shadow.extract<Any>(picture) as MockPicture).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(4 0 0 4 -100 -50))", ops[1])
    }

    //--------------------------------------------------------------------------
    @Test
    @Throws(SVGParseException::class)
    fun renderToPictureWithDimsRO() {
        val test = """<svg viewBox="0 0 200 100">
  <rect width="200" height="100" fill="green"/>
</svg>"""
        val svg = SVG.getFromString(test)
        val opts = create().viewPort(100f, 100f, 200f, 300f)
        val picture = svg.renderToPicture(400, 400, opts)
        val ops = (Shadow.extract<Any>(picture) as MockPicture).operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(400, picture.getWidth().toLong())
        Assert.assertEquals(400, picture.getHeight().toLong())
        Assert.assertEquals("concat(Matrix(1 0 0 1 100 200))", ops[1])
        Assert.assertEquals(
            "drawPath('M 0 0 L 200 0 L 200 100 L 0 100 L 0 0 Z', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; ts:16; tf:android.graphics.Typeface@0; color:#ff008000))",
            ops[3]
        )
    }

    @Test
    @Throws(SVGParseException::class)
    fun renderToPictureRO() {
        val test = """
            <svg>
            </svg>
            """.trimIndent()
        val svg = SVG.getFromString(test)

        // Step 1
        var opts = create()
        opts.viewPort(0f, 0f, 200f, 300f)
            .viewBox(0f, 0f, 100f, 50f)
        var picture = svg.renderToPicture(opts)
        var mock = Shadow.extract<Any>(picture) as MockPicture
        var ops = mock.operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(2 0 0 2 0 100))", ops[1])

        // Step 2
        opts = create()
        opts.viewPort(0f, 0f, 200f, 300f)
            .viewBox(0f, 0f, 100f, 50f)
            .preserveAspectRatio(of("xMinYMax meet"))
        picture = svg.renderToPicture(opts)
        mock = Shadow.extract<Any>(picture) as MockPicture
        ops = mock.operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(2 0 0 2 0 200))", ops[1])

        // Step 3
        opts = create()
        opts.viewPort(0f, 0f, 200f, 300f)
            .viewBox(0f, 0f, 100f, 50f)
            .preserveAspectRatio(of("none"))
        picture = svg.renderToPicture(opts)
        mock = Shadow.extract<Any>(picture) as MockPicture
        ops = mock.operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("concat(Matrix(2 0 0 6 0 0))", ops[1])
    }
}
