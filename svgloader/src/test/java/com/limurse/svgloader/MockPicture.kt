/*
 * Copyright 2013-2018 Paul LeBeau, Cave Rock Software Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.limurse.svgloader

import android.graphics.Canvas
import android.graphics.Picture
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowPicture

/**
 * Mock version of Android Picture class for testing.
 */
@Implements(Picture::class)
class MockPicture : ShadowPicture() {
    private var width = 0
    private var height = 0
    private var canvas: Canvas? = null
    private var recording = false
    @Implementation
    override fun __constructor__() {
    }

    val operations: List<String>
        get() = (Shadow.extract<Any>(canvas) as MockCanvas).operations

    fun clearOperations() {
        (Shadow.extract<Any>(canvas) as MockCanvas).clearOperations()
    }

    @Implementation
    override fun beginRecording(width: Int, height: Int): Canvas {
        this.width = width
        this.height = height
        canvas = Canvas()
        recording = true
        return canvas!!
    }

    @Implementation
    fun endRecording() {
        recording = false
    }

    override fun getWidth(): Int {
        return this.width
    }

    override fun getHeight(): Int {
        return this.height
    } //public static Picture createFromStream(InputStream stream ) { return null; }
    //public void draw(Canvas canvas) { /* do nothing */ }
    //public boolean requiresHardwareAcceleration() { return true; }
    //public void writeToStream(OutputStream stream ) { /* do nothing */ }
}
