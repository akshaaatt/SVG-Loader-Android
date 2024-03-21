package com.limurse.svgloader

import com.limurse.svgloader.utils.CSSBase

/**
 * This is a container for pre-parsed CSS that can be used to avoid parsing raw CSS string on each
 * render. It can be passed to RenderOptions like this:
 * <pre class="code-block">
 * `CSS css = CSS.getFromString("...some complex and long css here that takes time to parse...")
 * RenderOption renderOptions = RenderOptions.create();
 * renderOptions.css(css) // And now you can reuse the already parsed css
 * svg1.renderToCanvas(canvas, renderOptions);
 * svg2.renderToCanvas(canvas, renderOptions);
 * svg3.renderToCanvas(canvas, renderOptions);
` *
</pre> *
 */
class CSS private constructor(css: String) : CSSBase(css) {
    companion object {
        /**
         * @param css css string to parse
         * @return pre-parsed CSS
         */
        fun getFromString(css: String): CSS {
            return CSS(css)
        }
    }
}
