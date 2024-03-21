
package com.limurse.svgloader

import android.os.Build
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.JELLY_BEAN])
@RunWith(
    RobolectricTestRunner::class
)
class ViewsTest {
    @get:Throws(SVGParseException::class)
    @get:Test
    val viewList: Unit
        get() {
            val test = """<?xml version="1.0" standalone="no"?>
<svg xmlns="http://www.w3.org/2000/svg">  <view id="normalView" viewBox="0 0 100 100"/>  <g>    <view id="halfView"   viewBox="0 0 200 200"/>    <g>      <view id="doubleView" viewBox="0 0  50  50"/>    </g>  </g></svg>"""
            val svg = SVG.getFromString(test)
            val views = svg.viewList
            Assert.assertEquals(3, views.size.toLong())
            Assert.assertTrue(views.contains("normalView"))
            Assert.assertTrue(views.contains("halfView"))
            Assert.assertTrue(views.contains("doubleView"))
        }
}
