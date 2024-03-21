package com.limurse.svgloader

import android.graphics.Matrix
import android.graphics.Path
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale


@Implements(Path::class)
class MockPath {
    private var path = ArrayList<String>()
    private var transforms: ArrayList<Matrix>? = null
    @Implementation
    fun __constructor__() {
        path.clear()
    }

    @Implementation
    fun moveTo(x: Float, y: Float) {
        path.add(String.format(Locale.US, "M %s %s", num(x), num(y)))
    }

    @Implementation
    fun lineTo(x: Float, y: Float) {
        path.add(String.format(Locale.US, "L %s %s", num(x), num(y)))
    }

    @Implementation
    fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        path.add(String.format(Locale.US, "Q %s %s %s %s", num(x1), num(y1), num(x2), num(y2)))
    }

    @Implementation
    fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        path.add(
            String.format(
                Locale.US,
                "C %s %s %s %s %s %s",
                num(x1),
                num(y1),
                num(x2),
                num(y2),
                num(x3),
                num(y3)
            )
        )
    }

    @Implementation
    fun close() {
        path.add("Z")
    }

    @Implementation
    fun op(otherPath: Path?, op: Path.Op?): Boolean {
        val mockOtherPath = Shadow.extract<Any>(otherPath) as MockPath
        if (path.isEmpty()) {
            path = ArrayList(mockOtherPath.path)
            return true
        }

        // Update the path to represent the Op() operation
        path.add(0, "(")
        when (op) {
            Path.Op.UNION -> path.add("\u222a")
            Path.Op.INTERSECT -> path.add("\u2229")
            else -> {}
        }
        path.addAll(mockOtherPath.path)
        path.add(")")
        return true
    }

    @Implementation
    fun transform(matrix: Matrix) {
        if (transforms == null) transforms = ArrayList()
        transforms!!.add(matrix)
    }

    val pathDescription: String
        get() {
            val sb = StringBuilder()
            for (pathSeg in path) {
                if (sb.length > 0) sb.append(' ')
                sb.append(pathSeg)
            }
            if (transforms != null && !transforms!!.isEmpty()) {
                for (matrix in transforms!!) {
                    if (matrix.isIdentity) continue
                    sb.append(" \u00d7 [")
                    formatMatrix(sb, matrix)
                    sb.append(']')
                }
            }
            return sb.toString()
        }

    private fun formatMatrix(sb: StringBuilder, matrix: Matrix) {
        val values = FloatArray(9)
        matrix.getValues(values)
        sb.append(num(values[0]))
        sb.append(", ")
        sb.append(num(values[3]))
        sb.append(", ")
        sb.append(num(values[1]))
        sb.append(", ")
        sb.append(num(values[4]))
        sb.append(", ")
        sb.append(num(values[2]))
        sb.append(", ")
        sb.append(num(values[5]))
    }

    companion object {
        private fun num(f: Float): String {
            return if (f == f.toLong().toFloat()) String.format(
                "%d",
                f.toLong()
            ) else String.format("%s", round(f, 5))
        }

        private fun round(value: Float, places: Int): Float {
            var bd = BigDecimal(value.toString())
            bd = bd.setScale(places, RoundingMode.HALF_UP)
            return bd.toFloat()
        }
    }
}
