@file:OptIn(ExperimentalForeignApi::class)

package gfx

import kotlinx.cinterop.*

class Screen(val w: Int, val h: Int, val sheet: Spritesheet) {
    companion object {
        const val BIT_MIRROR_X: Int = 0x01
        const val BIT_MIRROR_Y: Int = 0x02
    }

    val pixels: IntArray = IntArray(w * h)

    var xOffset: Int = 0
    var yOffset: Int = 0

    val dither = intArrayOf(0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5)
}

operator fun Screen.get(x: Int, y: Int): Int {
    if (x < 0 || x >= w || y < 0 || y >= h) {
        throw IndexOutOfBoundsException("Coordinates ($x, $y) are out of bounds for screen size ($w, $h)")
    }
    return pixels[y * w + x]
}

operator fun Screen.set(x: Int, y: Int, value: Int) {
    if (x < 0 || x >= w || y < 0 || y >= h) {
        throw IndexOutOfBoundsException("Coordinates ($x, $y) are out of bounds for screen size ($w, $h)")
    }
    pixels[y * w + x] = value
}

fun Screen.clear(color: Int) {
    for (y in 0 until h) {
        for (x in 0 until w) {
            this[x, y] = color
        }
    }
}

fun Screen.render(xp: Int, yp: Int, tile: Int, colors: Int, bits: Int) {
    var xp = xp
    var yp = yp
    xp -= xOffset
    yp -= yOffset
    val mirrorX = (bits and Screen.BIT_MIRROR_X) > 0
    val mirrorY = (bits and Screen.BIT_MIRROR_Y) > 0

    val xTile: Int = tile % 32
    val yTile: Int = tile / 32
    val toffs: Int = xTile * 8 + yTile * 8 * sheet.width

    for (y in 0..7) {
        var ys = y
        if (mirrorY) ys = 7 - y
        if (y + yp < 0 || y + yp >= h) continue
        for (x in 0..7) {
            if (x + xp < 0 || x + xp >= w) continue

            var xs = x
            if (mirrorX) xs = 7 - x
            val col: Int = (colors shr (sheet.pixels[xs + ys * sheet.width + toffs] * 8)) and 255
            if (col < 255) pixels[(x + xp) + (y + yp) * w] = col
        }
    }
}

fun Screen.overlay(screen2: Screen, xa: Int, ya: Int) {
    val oPixels = screen2.pixels
    var i = 0
    for (y in 0 until h) {
        for (x in 0 until w) {
            if (oPixels[i] / 10 <= dither[((x + xa) and 3) + ((y + ya) and 3) * 4]) pixels[i] = 0
            i++
        }
    }
}

fun Screen.renderLight(x: Int, y: Int, r: Int) {
    var x = x
    var y = y
    x -= xOffset
    y -= yOffset
    var x0 = x - r
    var x1 = x + r
    var y0 = y - r
    var y1 = y + r

    if (x0 < 0) x0 = 0
    if (y0 < 0) y0 = 0
    if (x1 > w) x1 = w
    if (y1 > h) y1 = h
    for (yy in y0..<y1) {
        var yd = yy - y
        yd = yd * yd
        for (xx in x0..<x1) {
            val xd = xx - x
            val dist = xd * xd + yd
            if (dist <= r * r) {
                val br = 255 - dist * 255 / (r * r)
                if (pixels[xx + yy * w] < br) pixels[xx + yy * w] = br
            }
        }
    }
}