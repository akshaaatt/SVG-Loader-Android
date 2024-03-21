package com.limurse.svgloader.utils

import java.text.DecimalFormat

class CSSFontVariationSettings {
    private val settings: HashMap<String, Float>

    private class FontVariationEntry(var name: String, var `val`: Float) {
    }

    constructor() {
        settings = HashMap()
    }

    private constructor(initialMap: HashMap<String, Float>) {
        settings = initialMap
    }

    constructor(other: CSSFontVariationSettings) {
        settings = HashMap(other.settings)
    }

    fun addSetting(key: String, value: Float) {
        settings[key] = value
    }

    fun applySettings(variationSet: CSSFontVariationSettings?) {
        if (variationSet == null) return
        settings.putAll(variationSet.settings)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for ((key, value) in settings) {
            if (sb.length > 0) sb.append(',')
            sb.append("'")
            sb.append(key)
            sb.append("' ")
            val format = DecimalFormat("#.##")
            sb.append(format.format(value))
        }
        return sb.toString()
    }

    companion object {
        private const val NORMAL = "normal"
        const val VARIATION_WEIGHT = "wght"
        const val VARIATION_ITALIC = "ital"
        const val VARIATION_OBLIQUE = "slnt"
        const val VARIATION_WIDTH = "wdth"
        const val VARIATION_ITALIC_VALUE_ON = 1f
        const val VARIATION_OBLIQUE_VALUE_ON = -14f // -14 degrees

        //-----------------------------------------------------------------------------------------------
        // Parsing font-variation-settings property value
        /*
    * Parse the value of the CSS property "font-variation-settings".
    *
    * Format is: normal | [ <string> <number>]#
    */
        @JvmStatic
        fun parseFontVariationSettings(`val`: String?): CSSFontVariationSettings? {
            val result = CSSFontVariationSettings()
            val scan = TextScanner(`val`)
            scan.skipWhitespace()
            if (scan.consume(NORMAL)) return null
            while (true) {
                if (scan.empty()) break
                val entry = nextFeatureEntry(scan) ?: return null
                result.settings[entry.name] = entry.`val`
                scan.skipCommaWhitespace()
            }
            return result
        }

        private fun nextFeatureEntry(scan: TextScanner): FontVariationEntry? {
            scan.skipWhitespace()
            val name = scan.nextQuotedString()
            if (name == null || name.length != 4) return null
            scan.skipWhitespace()
            if (scan.empty()) return null
            val num = scan.nextFloat()
            return FontVariationEntry(name, num)
        }
    }
}
