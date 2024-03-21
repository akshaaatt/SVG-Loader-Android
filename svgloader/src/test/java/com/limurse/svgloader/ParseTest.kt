package com.limurse.svgloader

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import com.limurse.svgloader.RenderOptions.Companion.create
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow

@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.JELLY_BEAN],
    shadows = [MockCanvas::class, MockPath::class, MockPaint::class]
)
@RunWith(
    RobolectricTestRunner::class
)
class ParseTest {
    @Test
    @Throws(SVGParseException::class)
    fun emptySVG() {
        // XmlPullParser
        val test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                "</svg>"
        val svg = SVG.getFromString(test)
        Assert.assertNotNull(svg.rootElement)
    }

    @Test
    @Throws(SVGParseException::class)
    fun emptySVGEntitiesEnabled() {
        // NOTE: Is *really* slow when running under JUnit (15-20secs).
        // However, the speed seems to be okay under normal usage (a real app).
        val test =
            "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\" [" +
                    "  <!ENTITY hello \"Hello World!\">" +
                    "]>" +
                    "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                    "</svg>"
        val svg = SVG.getFromString(test)
        Assert.assertNotNull(svg.rootElement)
    }

    @Test
    @Throws(SVGParseException::class)
    fun emptySVGEntitiesDisabled() {
        val test =
            "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\" [" +
                    "  <!ENTITY hello \"Hello World!\">" +
                    "]>" +
                    "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                    "</svg>"
        SVG.setInternalEntitiesEnabled(false)
        val svg = SVG.getFromString(test)
        Assert.assertNotNull(svg.rootElement)
    }

    @Test(expected = SVGParseException::class)
    @Throws(SVGParseException::class)
    fun unbalancedClose() {
        val test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                "</svg>" +
                "</svg>"
        val svg = SVG.getFromString(test)
    }

    @Test
    fun parsePath() {
        var test = "M100,200 C100,100 250,100 250,200 S400,300 400,200"
        var path = SVG.parsePath(test)
        Assert.assertEquals(
            "M 100 200 C 100 100 250 100 250 200 C 250 300 400 300 400 200",
            (Shadow.extract<Any>(path) as MockPath).pathDescription
        )

        // The arcs in a path get converted to cubic beziers
        test = "M-100 0 A 100 100 0 0 0 0,100"
        path = SVG.parsePath(test)
        Assert.assertEquals(
            "M -100 0 C -100 55.22848 -55.22848 100 0 100",
            (Shadow.extract<Any>(path) as MockPath).pathDescription
        )

        // Path with errors
        test = "M 0 0 L 100 100 C 200 200 Z"
        path = SVG.parsePath(test)
        Assert.assertEquals(
            "M 0 0 L 100 100",
            (Shadow.extract<Any>(path) as MockPath).pathDescription
        )
    }

    /*
   @Test
   public void issue177() throws SVGParseException
   {
      String  test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                     "  <defs></defs>" +
                     "  <g></g>" +
                     "  <a></a>" +
                     "  <use></use>" +
                     "  <image></image>" +
                     "  <text>" +
                     "    <tspan></tspan>" +
                     "    <textPath></textPath>" +
                     "  </text>" +
                     "  <switch></switch>" +
                     "  <symbol></symbol>" +
                     "  <marker></marker>" +
                     "  <linearGradient>" +
                     "    <stop></stop>" +
                     "  </linearGradient>" +
                     "  <radialGradient></radialGradient>" +
                     "  <clipPath></clipPath>" +
                     "  <pattern></pattern>" +
                     "  <view></view>" +
                     "  <mask></mask>" +
                     "  <solidColor></solidColor>" +
                     "  <g>" +
                     "    <path>" +
                     "      <style media=\"print\">" +
                     "      </style>" +
                     "    </path>" +
                     "  </g>" +
                     "</svg>";

      try {
         SVG  svg = SVG.getFromString(test);
         fail("Should have thrown ParseException");
      } catch (SVGParseException e) {
         // passed!
      }
   }
*/
    /*
    * Checks that A elements are parsed and rendered correctly.
    * @throws SVGParseException
    */
    @Test
    @Throws(SVGParseException::class)
    fun parseA() {
        var test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                "<a>" +
                "  <rect width=\"10\" height=\"10\" fill=\"red\"/>" +
                "</a>" +
                "</svg>"
        var svg = SVG.getFromString(test)
        val bm = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        val bmcanvas = Canvas(bm)

        // Test that A element has been inserted in the DOM tree correctly
        val opts = create()
        opts.css("a rect { fill: green; }")
        svg.renderToCanvas(bmcanvas, opts)
        val mock = Shadow.extract<MockCanvas>(bmcanvas)
        var ops = mock.operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(
            "drawPath('M 0 0 L 10 0 L 10 10 L 0 10 L 0 0 Z', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; ts:16; tf:android.graphics.Typeface@0; color:#ff008000))",
            ops[4]
        )


        // Test that A elements are being visited properly while rendering
        test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                "<a fill=\"green\">" +
                "  <rect width=\"10\" height=\"10\"/>" +
                "</a>" + "</svg>"
        svg = SVG.getFromString(test)
        mock.clearOperations()
        svg.renderToCanvas(bmcanvas)
        ops = mock.operations
        //System.out.println(String.join(",", ops));
        Assert.assertEquals(
            "drawPath('M 0 0 L 10 0 L 10 10 L 0 10 L 0 0 Z', Paint(f:ANTI_ALIAS|LINEAR_TEXT|SUBPIXEL_TEXT; h:OFF; s:FILL; ts:16; tf:android.graphics.Typeface@0; color:#ff008000))",
            ops[4]
        )
    }

    /**
     * Issue 186
     * CSS properties without a value are badly parsed.
     */
    @Test
    @Throws(SVGParseException::class)
    fun issue186() {
        val test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                "<text style=\"text-decoration:;fill:green\">Test</text>" +
                "</svg>"
        val svg = SVG.getFromString(test)
        val bm = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        val bmcanvas = Canvas(bm)
        svg.renderToCanvas(bmcanvas)
        val mock = Shadow.extract<MockCanvas>(bmcanvas)
        //List<String>  ops = mock.getOperations();
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("#ff008000", mock.paintProp(3, "color"))
    }

    @Test
    @Throws(SVGParseException::class)
    fun parseStyleLeadingColon() {
        val test = "<svg xmlns=\"http://www.w3.org/2000/svg\">" +
                "<text style=\"fill:green;:fill:red\">Test</text>" +
                "</svg>"
        val svg = SVG.getFromString(test)
        val bm = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        val bmcanvas = Canvas(bm)
        svg.renderToCanvas(bmcanvas)
        val mock = Shadow.extract<MockCanvas>(bmcanvas)
        //List<String>  ops = mock.getOperations();
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("#ff008000", mock.paintProp(3, "color"))
    }

    /**
     * Issue 199
     * Semi-thread safe parsing properties (enableInternalEntities and externalFileResolver)
     */
    @Test
    @Throws(SVGParseException::class)
    fun issue199() {
        val test = "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>"
        val svg = SVG.getFromString(test)
        Assert.assertTrue(svg.isInternalEntitiesEnabled)
        Assert.assertNull(svg.externalFileResolver)
        SVG.setInternalEntitiesEnabled(false)
        val resolver: SVGExternalFileResolver = SimpleAssetResolver(null)
        SVG.registerExternalFileResolver(resolver)
        val svg2 = SVG.getFromString(test)
        Assert.assertFalse(svg2.isInternalEntitiesEnabled)
        Assert.assertEquals(resolver, svg2.externalFileResolver)

        // Ensure settings for "svg" haven't changed
        Assert.assertTrue(svg.isInternalEntitiesEnabled)
        Assert.assertNull(svg.externalFileResolver)
    }
}
