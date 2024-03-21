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
    sdk = [Build.VERSION_CODES.O],
    shadows = [MockCanvas::class, MockPath::class, MockPaint::class]
)
class FontFeatureSettingsTest {
    @Test
    @Throws(SVGParseException::class)
    fun fontFeatures() {
        val test = """<svg>
  <text style="font-feature-settings: 'liga' 0, 'clig', 'pnum' on, 'swsh' 42">Test</text>
</svg>"""
        val svg = SVG.getFromString(test)
        val bm1 = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm1)
        svg.renderToCanvas(canvas)
        val mock = Shadow.extract<Any>(canvas) as MockCanvas

        //List<String>  ops = ((MockCanvas) Shadow.extract(canvas)).getOperations();
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(
            "'onum' 0,'subs' 0,'unic' 0,'calt' 1,'dlig' 0,'c2pc' 0,'mkmk' 1,'swsh' 42,'zero' 0,'hlig' 0,'c2sc' 0,'sups' 0,'pcap' 0,'jp78' 0,'pwid' 0,'trad' 0,'ordn' 0,'titl' 0,'fwid' 0,'frac' 0,'locl' 1,'pnum' 1,'smpl' 0,'kern' 1,'tnum' 0,'liga' 0,'lnum' 0,'clig' 1,'jp90' 0,'rlig' 1,'ccmp' 1,'ruby' 0,'jp83' 0,'smcp' 0,'afrc' 0,'jp04' 0,'mark' 1",
            mock.paintProp(3, "ff")
        )
    }

    //-----------------------------------------------------------------------------------------------
    @Test
    @Throws(SVGParseException::class)
    fun fontVariation() {
        val test = """<svg>
  <text style="font-variation-settings: 'wght' 100, 'slnt' -14">Test</text>
</svg>"""
        val svg = SVG.getFromString(test)
        val bm1 = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm1)
        svg.renderToCanvas(canvas)
        val mock = Shadow.extract<Any>(canvas) as MockCanvas

        //List<String>  ops = ((MockCanvas) Shadow.extract(canvas)).getOperations();
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(
            "'wdth' 100,'slnt' -14,'wght' 100",
            mock.paintProp(3, "fv")
        )
    }

    //-----------------------------------------------------------------------------------------------
    @Test
    @Throws(SVGParseException::class)
    fun fontStretch() {
        val test = """<svg>
  <text style="font-stretch: ultra-condensed">Test
  <tspan style="font-stretch: normal">Test</tspan></text>
  <text style="font-stretch: ultra-expanded">Test</text>
  <text style="font-stretch: 80%">Test</text>
  <text style="font-stretch: 66">Test</text>
  <text style="font-stretch: -10%">Test</text>
</svg>"""
        val svg = SVG.getFromString(test)
        val bm1 = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm1)
        svg.renderToCanvas(canvas)
        val mock = Shadow.extract<Any>(canvas) as MockCanvas

        //List<String>  ops = ((MockCanvas) Shadow.extract(canvas)).getOperations();
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("'wdth' 50,'wght' 400", mock.paintProp(5, "fv"))
        Assert.assertEquals("'wdth' 100,'wght' 400", mock.paintProp(7, "fv"))
        Assert.assertEquals("'wdth' 200,'wght' 400", mock.paintProp(11, "fv"))
        Assert.assertEquals("'wdth' 80,'wght' 400", mock.paintProp(14, "fv"))
        Assert.assertEquals("'wdth' 100,'wght' 400", mock.paintProp(17, "fv"))
        Assert.assertEquals("'wdth' 100,'wght' 400", mock.paintProp(20, "fv"))
    } //-----------------------------------------------------------------------------------------------
}
