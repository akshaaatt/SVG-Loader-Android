package com.limurse.svgloader.utils

import com.limurse.svgloader.utils.CSSParser.Ruleset

/*
   This is just a link to CSSParser class. As CSSParser is package-protected and we don't want it
   to leak as a public API, we just gaining access through this inheritance.
*/
open class CSSBase protected constructor(css: String?) {
    @JvmField
    var cssRuleset: Ruleset

    init {
        cssRuleset = CSSParser(CSSParser.Source.RenderOptions, null).parse(css)
    }
}
