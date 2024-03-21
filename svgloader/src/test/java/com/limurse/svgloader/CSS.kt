
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
import kotlin.math.ceil

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.JELLY_BEAN],
    shadows = [MockCanvas::class, MockPath::class, MockPaint::class]
)
class CSS {
    /* !important not supported yet
   @Test
   public void important() throws SVGParseException
   {
      //disableLogging();
      String  test = "<svg width=\"100\" height=\"100\">" +
                     "  <rect id=\"one\" width=\"10\" height=\"10\"/>" +
                     "  <rect id=\"two\" width=\"10\" height=\"10\" x=\"10\"/>" +
                     "  <rect id=\"three\" width=\"10\" height=\"10\" x=\"20\"/>" +
                     "  <rect id=\"four\" width=\"10\" height=\"10\" x=\"30\"/>" +
                     "  <style>" +
                     "    rect { fill: #0f0 ! important; }" +
                     "    rect { fill: #f00; }" +
                     "    #four { fill: #f00; }" +
                     "  </style>" +
                     "</svg>";
      SVG  svg = SVG.getFromString(test);

      Bitmap newBM = Bitmap.createBitmap((int) Math.ceil(svg.getDocumentWidth()),
                                         (int) Math.ceil(svg.getDocumentHeight()),
                                         Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(newBM);

      RenderOptions renderOptions = RenderOptions.create().css("");
      svg.renderToCanvas(canvas, renderOptions);

      MockCanvas    mock = ((MockCanvas) Shadow.extract(canvas));
      List<String> ops = mock.getOperations();
      //System.out.println(String.join(",", ops));

      assertEquals("#ff00ff00", mock.paintProp(3, "color"));
      assertEquals("#ff000000", mock.paintProp(6, "color"));
      assertEquals("#ff00ff00", mock.paintProp(9, "color"));
      assertEquals("#ff000000", mock.paintProp(12, "color"));
   }
*/
    @Test
    @Throws(SVGParseException::class)
    fun use() {
        //disableLogging();
        val test = "<svg width=\"100\" height=\"100\">" +
                "  <defs>" +
                "    <rect id=\"r\" width=\"10\" height=\"10\"/>" +
                "  </defs>" +
                "  <style>" +
                "    use { fill: #0f0; }" +
                "  </style>" +
                "  <use href=\"#r\"/>" +
                "</svg>"
        val svg = SVG.getFromString(test)
        val newBM = Bitmap.createBitmap(
            ceil(svg.documentWidth.toDouble()).toInt(),
            ceil(svg.documentHeight.toDouble()).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBM)
        val renderOptions = create().css("")
        svg.renderToCanvas(canvas, renderOptions)
        val mock = Shadow.extract<Any>(canvas) as MockCanvas
        //List<String> ops = mock.getOperations();
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("#ff00ff00", mock.paintProp(5, "color"))
    }

    // Issue 204
    @Test
    @Throws(SVGParseException::class)
    fun nonAsciiClassNames() {
        //disableLogging();
        val test = "<svg width=\"100\" height=\"100\">" +
                "  <style>" +
                "    .зеленый {fill:#0f0}" +
                "  </style>" +
                "  <rect class=\"зеленый\" width=\"10\" height=\"10\"/>" +
                "</svg>"
        val svg = SVG.getFromString(test)
        val newBM = Bitmap.createBitmap(
            ceil(svg.documentWidth.toDouble()).toInt(),
            ceil(svg.documentHeight.toDouble()).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBM)
        val renderOptions = create().css("")
        svg.renderToCanvas(canvas, renderOptions)
        val mock = Shadow.extract<Any>(canvas) as MockCanvas
        //List<String> ops = mock.getOperations();
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("#ff00ff00", mock.paintProp(3, "color"))
    }
}
