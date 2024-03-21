
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
class RenderCSS {
    /*
    * Checks that calling renderToCanvas() does not have any side effects for the Canvas object.
    * See Issue #50. https://github.com/BigBadaboom/androidsvg/issues/50
    */
    @Test
    @Throws(SVGParseException::class)
    fun renderWithCSS() {
        //disableLogging();
        val test = "<svg width=\"100\" height=\"100\">" +
                "  <rect width=\"10\" height=\"10\"/>" +
                "</svg>"
        val svg = SVG.getFromString(test)
        val newBM = Bitmap.createBitmap(
            ceil(svg.documentWidth.toDouble()).toInt(),
            ceil(svg.documentHeight.toDouble()).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(newBM)
        var renderOptions = create().css("")
        svg.renderToCanvas(canvas, renderOptions)
        val mock = Shadow.extract<Any>(canvas) as MockCanvas
        //List<String>  ops = mock.getOperations();
        //System.out.println(String.join(",", ops));
        Assert.assertEquals("#ff000000", mock.paintProp(3, "color"))


        // Step 2
        mock.clearOperations()
        renderOptions = create().css("rect { fill: red }")
        svg.renderToCanvas(canvas, renderOptions)
        //System.out.println(String.join(",", ops));

        // rect should be red now
        Assert.assertEquals("#ffff0000", mock.paintProp(3, "color"))


        // Step 3: Make sure temp CSS hasn't stuck around
        mock.clearOperations()
        svg.renderToCanvas(canvas)
        //System.out.println(String.join(",", ops));

        // rect should be black again
        Assert.assertEquals("#ff000000", mock.paintProp(3, "color"))
    }
}
