package com.limurse.svgloader

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Picture
import android.graphics.RectF
import com.limurse.svgloader.RenderOptions.Companion.create
import com.limurse.svgloader.SVGParseException
import com.limurse.svgloader.utils.SVGBase
import com.limurse.svgloader.utils.SVGBase.Svg
import java.io.IOException
import java.io.InputStream

/**
 * AndroidSVG is a library for reading, parsing and rendering SVG documents on Android devices.
 *
 *
 * All interaction with AndroidSVG is via this class.
 *
 *
 * Typically, you will call one of the SVG loading and parsing classes then call the renderer,
 * passing it a canvas to draw upon.
 *
 * <h3>Usage summary</h3>
 *
 *
 *  * Use one of the static `getFromX()` methods to read and parse the SVG file.  They will
 * return an instance of this class.
 *  * Call one of the `renderToX()` methods to render the document.
 *
 *
 * <h3>Usage example</h3>
 *
 * <pre>
 * `SVG.registerExternalFileResolver(myResolver);
 *
 * SVG  svg = SVG.getFromAsset(getContext().getAssets(), svgPath);
 *
 * Bitmap  newBM = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
 * Canvas  bmcanvas = new Canvas(newBM);
 * bmcanvas.drawRGB(255, 255, 255);  // Clear background to white
 *
 * svg.renderToCanvas(bmcanvas);
` *
</pre> *
 *
 * For more detailed information on how to use this library, see the documentation at `http://code.google.com/p/androidsvg/`
 */
class SVG // Users should use one of the getFromX() methods to create an instance of SVG
private constructor(private val base: SVGBase) {
    @get:Suppress("unused")
    val isInternalEntitiesEnabled: Boolean
        /**
         * Indicates whether internal entities were enabled when this SVG was parsed.
         *
         *
         *
         * *Note: prior to release 1.5, this was a static method of (@code SVG}.  In 1.5, it was
         * changed to a instance method to coincide with the change making parsing settings thread safe.*
         *
         *
         * @return true if internal entity expansion is enabled in the parser
         * @since 1.5
         */
        get() = base.isInternalEntitiesEnabled

    @get:Suppress("unused")
    val externalFileResolver: SVGExternalFileResolver
        /**
         * Get the [SVGExternalFileResolver] in effect when this SVG was parsed..
         *
         * @return the current external file resolver instance
         * @since 1.5
         */
        get() = base.externalFileResolver

    @get:Suppress("unused")
    @set:Suppress("unused")
    var renderDPI: Float
        /**
         * Get the current render DPI setting.
         * @return the DPI value
         */
        get() = base.renderDPI
        /**
         * Set the DPI (dots-per-inch) value to use when rendering.  The DPI setting is used in the
         * conversion of "physical" units - such an "pt" or "cm" - to pixel values.  The default DPI is 96.
         *
         *
         * You should not normally need to alter the DPI from the default of 96 as recommended by the SVG
         * and CSS specifications.
         *
         * @param dpi the DPI value that the renderer should use.
         */
        set(dpi) {
            base.renderDPI = dpi
        }
    //===============================================================================
    // SVG document rendering to a Picture object (indirect rendering)
    /**
     * Renders this SVG document to a Picture object.
     *
     *
     * An attempt will be made to determine a suitable initial viewport from the contents of the SVG file.
     * If an appropriate viewport can't be determined, a default viewport of 512x512 will be used.
     *
     * @return a Picture object suitable for later rendering using `Canvas.drawPicture()`
     */
    fun renderToPicture(): Picture {
        return base.renderToPicture(null)
    }

    /**
     * Renders this SVG document to a [Picture].
     *
     * @param widthInPixels the width of the initial viewport
     * @param heightInPixels the height of the initial viewport
     * @return a Picture object suitable for later rendering using [Canvas.drawPicture]
     */
    @Suppress("unused")
    fun renderToPicture(widthInPixels: Int, heightInPixels: Int): Picture {
        return renderToPicture(widthInPixels, heightInPixels, null)
    }

    /**
     * Renders this SVG document to a [Picture].
     *
     * @param renderOptions options that describe how to render this SVG on the Canvas.
     * @return a Picture object suitable for later rendering using [Canvas.drawPicture]
     * @since 1.3
     */
    @Suppress("unused")
    fun renderToPicture(renderOptions: RenderOptions?): Picture {
        return base.renderToPicture(renderOptions)
    }

    /**
     * Renders this SVG document to a [Picture].
     *
     * @param widthInPixels the width of the `Picture`
     * @param heightInPixels the height of the `Picture`
     * @param renderOptions options that describe how to render this SVG on the Canvas.
     * @return a Picture object suitable for later rendering using [Canvas.drawPicture]
     * @since 1.3
     */
    @Suppress("unused")
    fun renderToPicture(
        widthInPixels: Int,
        heightInPixels: Int,
        renderOptions: RenderOptions?
    ): Picture {
        return base.renderToPicture(widthInPixels, heightInPixels, renderOptions)
    }

    /**
     * Renders this SVG document to a [Picture] using the specified view defined in the document.
     *
     *
     * A View is an special element in a SVG document that describes a rectangular area in the document.
     * Calling this method with a `viewId` will result in the specified view being positioned and scaled
     * to the viewport.  In other words, use [.renderToPicture] to render the whole document, or use this
     * method instead to render just a part of it.
     *
     * @param viewId the id of a view element in the document that defines which section of the document is to be visible.
     * @param widthInPixels the width of the initial viewport
     * @param heightInPixels the height of the initial viewport
     * @return a Picture object suitable for later rendering using `Canvas.drawPicture()`, or null if the viewId was not found.
     */
    @Suppress("unused")
    fun renderViewToPicture(viewId: String?, widthInPixels: Int, heightInPixels: Int): Picture {
        return base.renderViewToPicture(viewId, widthInPixels, heightInPixels)
    }
    //===============================================================================
    // SVG document rendering to a canvas object (direct rendering)
    /**
     * Renders this SVG document to a Canvas object.  The full width and height of the canvas
     * will be used as the viewport into which the document will be rendered.
     *
     * @param canvas the canvas to which the document should be rendered.
     * @since 1.3
     */
    @Suppress("unused")
    fun renderToCanvas(canvas: Canvas?) {
        renderToCanvas(canvas, null as RenderOptions?)
    }

    /**
     * Renders this SVG document to a Canvas object.
     *
     * @param canvas the canvas to which the document should be rendered.
     * @param viewPort the bounds of the area on the canvas you want the SVG rendered, or null for the whole canvas.
     */
    @Suppress("unused")
    fun renderToCanvas(canvas: Canvas?, viewPort: RectF?) {
        base.renderToCanvas(canvas, viewPort)
    }

    /**
     * Renders this SVG document to a Canvas object.
     *
     * @param canvas the canvas to which the document should be rendered.
     * @param renderOptions options that describe how to render this SVG on the Canvas.
     * @since 1.3
     */
    @Suppress("unused")
    fun renderToCanvas(canvas: Canvas?, renderOptions: RenderOptions?) {
        base.renderToCanvas(canvas, renderOptions)
    }

    /**
     * Renders this SVG document to a Canvas using the specified view defined in the document.
     *
     *
     * A View is an special element in a SVG documents that describes a rectangular area in the document.
     * Calling this method with a `viewId` will result in the specified view being positioned and scaled
     * to the viewport.  In other words, use [.renderToPicture] to render the whole document, or use this
     * method instead to render just a part of it.
     *
     *
     * If the `<view>` could not be found, nothing will be drawn.
     *
     * @param viewId the id of a view element in the document that defines which section of the document is to be visible.
     * @param canvas the canvas to which the document should be rendered.
     */
    @Suppress("unused")
    fun renderViewToCanvas(viewId: String?, canvas: Canvas?) {
        renderToCanvas(canvas, create().view(viewId!!))
    }

    /**
     * Renders this SVG document to a Canvas using the specified view defined in the document.
     *
     *
     * A View is an special element in a SVG documents that describes a rectangular area in the document.
     * Calling this method with a `viewId` will result in the specified view being positioned and scaled
     * to the viewport.  In other words, use [.renderToPicture] to render the whole document, or use this
     * method instead to render just a part of it.
     *
     *
     * If the `<view>` could not be found, nothing will be drawn.
     *
     * @param viewId the id of a view element in the document that defines which section of the document is to be visible.
     * @param canvas the canvas to which the document should be rendered.
     * @param viewPort the bounds of the area on the canvas you want the SVG rendered, or null for the whole canvas.
     */
    @Suppress("unused")
    fun renderViewToCanvas(viewId: String?, canvas: Canvas?, viewPort: RectF?) {
        base.renderViewToCanvas(viewId, canvas, viewPort)
    }

    @get:Suppress("unused")
    val documentTitle: String
        /**
         * Returns the contents of the `<title>` element in the SVG document.
         *
         * @return title contents if available, otherwise an empty string.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getDocumentTitle()

    @get:Suppress("unused")
    val documentDescription: String
        /**
         * Returns the contents of the `<desc>` element in the SVG document.
         *
         * @return desc contents if available, otherwise an empty string.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getDocumentDescription()

    @get:Suppress("unused")
    val documentSVGVersion: String
        /**
         * Returns the SVG version number as provided in the root `<svg>` tag of the document.
         *
         * @return the version string if declared, otherwise an empty string.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getDocumentSVGVersion()

    @get:Suppress("unused")
    val viewList: Set<String>
        /**
         * Returns a list of ids for all `<view>` elements in this SVG document.
         *
         *
         * The returned view ids could be used when calling and of the `renderViewToX()` methods.
         *
         * @return the list of id strings.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getViewList()

    @get:Suppress("unused")
    @set:Suppress("unused")
    var documentWidth: Float
        /**
         * Returns the width of the document as specified in the SVG file.
         *
         *
         * If the width in the document is specified in pixels, that value will be returned.
         * If the value is listed with a physical unit such as "cm", then the current
         * `RenderDPI` value will be used to convert that value to pixels. If the width
         * is missing, or in a form which can't be converted to pixels, such as "100%" for
         * example, -1 will be returned.
         *
         * @return the width in pixels, or -1 if there is no width available.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getDocumentWidth()
        /**
         * Change the width of the document by altering the "width" attribute
         * of the root `<svg>` element.
         *
         * @param pixels The new value of width in pixels.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        set(pixels) {
            base.setDocumentWidth(pixels)
        }

    /**
     * Change the width of the document by altering the "width" attribute
     * of the root `<svg>` element.
     *
     * @param value A valid SVG 'length' attribute, such as "100px" or "10cm".
     * @throws SVGParseException if `value` cannot be parsed successfully.
     * @throws IllegalArgumentException if there is no current SVG document loaded.
     */
    @Suppress("unused")
    @Throws(SVGParseException::class)
    fun setDocumentWidth(value: String?) {
        base.setDocumentWidth(value)
    }

    @get:Suppress("unused")
    @set:Suppress("unused")
    var documentHeight: Float
        /**
         * Returns the height of the document as specified in the SVG file.
         *
         *
         * If the height in the document is specified in pixels, that value will be returned.
         * If the value is listed with a physical unit such as "cm", then the current
         * `RenderDPI` value will be used to convert that value to pixels. If the height
         * is missing, or in a form which can't be converted to pixels, such as "100%" for
         * example, -1 will be returned.
         *
         * @return the height in pixels, or -1 if there is no height available.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getDocumentHeight()
        /**
         * Change the height of the document by altering the "height" attribute
         * of the root `<svg>` element.
         *
         * @param pixels The new value of height in pixels.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        set(pixels) {
            base.setDocumentHeight(pixels)
        }

    /**
     * Change the height of the document by altering the "height" attribute
     * of the root `<svg>` element.
     *
     * @param value A valid SVG 'length' attribute, such as "100px" or "10cm".
     * @throws SVGParseException if `value` cannot be parsed successfully.
     * @throws IllegalArgumentException if there is no current SVG document loaded.
     */
    @Suppress("unused")
    @Throws(SVGParseException::class)
    fun setDocumentHeight(value: String?) {
        base.setDocumentHeight(value)
    }

    /**
     * Change the document view box by altering the "viewBox" attribute
     * of the root `<svg>` element.
     *
     *
     * The viewBox generally describes the bounding box dimensions of the
     * document contents.  A valid viewBox is necessary if you want the
     * document scaled to fit the canvas or viewport the document is to be
     * rendered into.
     *
     *
     * By setting a viewBox that describes only a portion of the document,
     * you can reproduce the effect of image sprites.
     *
     * @param minX the left coordinate of the viewBox in pixels
     * @param minY the top coordinate of the viewBox in pixels.
     * @param width the width of the viewBox in pixels
     * @param height the height of the viewBox in pixels
     * @throws IllegalArgumentException if there is no current SVG document loaded.
     */
    @Suppress("unused")
    fun setDocumentViewBox(minX: Float, minY: Float, width: Float, height: Float) {
        base.setDocumentViewBox(minX, minY, width, height)
    }

    @get:Suppress("unused")
    val documentViewBox: RectF
        /**
         * Returns the viewBox attribute of the current SVG document.
         *
         * @return the document's viewBox attribute as a `android.graphics.RectF` object, or null if not set.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getDocumentViewBox()

    @get:Suppress("unused")
    @set:Suppress("unused")
    var documentPreserveAspectRatio: PreserveAspectRatio?
        /**
         * Return the "preserveAspectRatio" attribute of the root `<svg>`
         * element in the form of an [PreserveAspectRatio] object.
         *
         * @return the preserveAspectRatio setting of the document's root `<svg>` element.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getDocumentPreserveAspectRatio()
        /**
         * Change the document positioning by altering the "preserveAspectRatio"
         * attribute of the root `<svg>` element.  See the
         * documentation for [PreserveAspectRatio] for more information
         * on how positioning works.
         *
         * @param preserveAspectRatio the new `preserveAspectRatio` setting for the root `<svg>` element.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        set(preserveAspectRatio) {
            base.setDocumentPreserveAspectRatio(preserveAspectRatio)
        }

    @get:Suppress("unused")
    val documentAspectRatio: Float
        /**
         * Returns the aspect ratio of the document as a width/height fraction.
         *
         *
         * If the width or height of the document are listed with a physical unit such as "cm",
         * then the current `renderDPI` setting will be used to convert that value to pixels.
         *
         *
         * If the width or height cannot be determined, -1 will be returned.
         *
         * @return the aspect ratio as a width/height fraction, or -1 if the ratio cannot be determined.
         * @throws IllegalArgumentException if there is no current SVG document loaded.
         */
        get() = base.getDocumentAspectRatio()
    val rootElement: Svg
        //===============================================================================================
        get() = base.rootElement

    companion object {
        /**
         * Returns the version number of this library.
         *
         * @return the version number in string format
         */
        //static final String  TAG = "SVG";
        @get:Suppress("unused")
        val version = "1.5"

        /**
         * Read and parse an SVG from the given `InputStream`.
         *
         * @param is the input stream from which to read the file.
         * @return an SVG instance on which you can call one of the render methods.
         * @throws SVGParseException if there is an error parsing the document.
         */
        @JvmStatic
        @Throws(SVGParseException::class)
        fun getFromInputStream(`is`: InputStream?): SVG {
            return SVG(SVGBase.getFromInputStream(`is`))
        }

        /**
         * Read and parse an SVG from the given `String`.
         *
         * @param svg the String instance containing the SVG document.
         * @return an SVG instance on which you can call one of the render methods.
         * @throws SVGParseException if there is an error parsing the document.
         */
        @JvmStatic
        @Suppress("unused")
        @Throws(SVGParseException::class)
        fun getFromString(svg: String?): SVG {
            return SVG(SVGBase.getFromString(svg))
        }

        /**
         * Read and parse an SVG from the given resource location.
         *
         * @param context the Android context of the resource.
         * @param resourceId the resource identifier of the SVG document.
         * @return an SVG instance on which you can call one of the render methods.
         * @throws SVGParseException if there is an error parsing the document.
         */
        @JvmStatic
        @Throws(SVGParseException::class)
        fun getFromResource(context: Context, resourceId: Int): SVG {
            return getFromResource(context.resources, resourceId)
        }

        /**
         * Read and parse an SVG from the given resource location.
         *
         * @param resources the set of Resources in which to locate the file.
         * @param resourceId the resource identifier of the SVG document.
         * @return an SVG instance on which you can call one of the render methods.
         * @throws SVGParseException if there is an error parsing the document.
         * @since 1.2.1
         */
        @Throws(SVGParseException::class)
        fun getFromResource(resources: Resources?, resourceId: Int): SVG {
            return SVG(SVGBase.getFromResource(resources, resourceId))
        }

        /**
         * Read and parse an SVG from the assets folder.
         *
         * @param assetManager the AssetManager instance to use when reading the file.
         * @param filename the filename of the SVG document within assets.
         * @return an SVG instance on which you can call one of the render methods.
         * @throws SVGParseException if there is an error parsing the document.
         * @throws IOException if there is some IO error while reading the file.
         */
        @Suppress("unused")
        @Throws(SVGParseException::class, IOException::class)
        fun getFromAsset(assetManager: AssetManager?, filename: String?): SVG {
            return SVG(SVGBase.getFromAsset(assetManager, filename))
        }

        /**
         * Parse an SVG path definition from the given `String`.
         *
         * `Path  path = SVG.parsePath("M 0,0 L 100,100");
         * path.setFillType(Path.FillType.EVEN_ODD);
         *
         * // You could render the path to a Canvas now
         * Paint paint = new Paint();
         * paint.setStyle(Paint.Style.FILL);
         * paint.setColor(Color.RED);
         * canvas.drawPath(path, paint);
         *
         * // Or perform other operations on it
         * RectF bounds = new RectF();
         * path.computeBounds(bounds, false);
        ` *
         *
         * Note that this method does not throw any exceptions or return any errors. Per the SVG
         * specification, if there are any errors in the path definition, the valid portion of the
         * path up until the first error is returned.
         *
         * @param pathDefinition an SVG path element definition string
         * @return an Android `Path`
         * @since 1.5
         */
        fun parsePath(pathDefinition: String?): Path {
            return SVGBase.parsePath(pathDefinition)
        }
        //===============================================================================
        /**
         * Tells the parser whether to allow the expansion of internal entities.
         * An example of a document containing an internal entities is:
         *
         * `<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.0//EN" "http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd" [
         * <!ENTITY hello "Hello World!">
         * ]>
         * <svg>
         * <text>&hello;</text>
         * </svg>
        ` *
         *
         * Entities are useful in some circumstances, but SVG files that use them are quite rare.  Note
         * also that enabling entity expansion makes you vulnerable to the
         * [Billion Laughs Attack](https://en.wikipedia.org/wiki/Billion_laughs_attack)
         *
         * Entity expansion is enabled by default.
         *
         * @param enable Set true if you want to enable entity expansion by the parser.
         * @since 1.3
         */
        @Suppress("unused")
        fun setInternalEntitiesEnabled(enable: Boolean) {
            SVGBase.setInternalEntitiesEnabled(enable)
        }

        /**
         * Register an [SVGExternalFileResolver] instance that the renderer should use when resolving
         * external references such as images, fonts, and CSS stylesheets.
         *
         *
         *
         * *Note: prior to release 1.3, this was an instance method of (@code SVG}.  In 1.3, it was
         * changed to a static method so that users can resolve external references to CSS files while
         * the SVG is being parsed.*
         *
         *
         * @param fileResolver the resolver to use.
         * @since 1.3
         */
        @Suppress("unused")
        fun registerExternalFileResolver(fileResolver: SVGExternalFileResolver?) {
            SVGBase.registerExternalFileResolver(fileResolver)
        }

        /**
         * De-register the current [SVGExternalFileResolver] instance.
         *
         * @since 1.3
         */
        @Suppress("unused")
        fun deregisterExternalFileResolver() {
            SVGBase.deregisterExternalFileResolver()
        }
        //===============================================================================
        // Other document utility API functions
    }
}
