package com.limurse.svgloader.utils

/*
 * Thrown by the CSS parser if a problem is found while parsing a CSS file.
 */
class CSSParseException : Exception {
    constructor(msg: String?) : super(msg)
    constructor(msg: String?, cause: Exception?) : super(msg, cause)
}
