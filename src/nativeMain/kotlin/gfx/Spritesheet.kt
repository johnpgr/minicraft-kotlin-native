@file:OptIn(ExperimentalForeignApi::class)

package gfx

import kotlinx.cinterop.*
import sdl.*
import sdl_image.IMG_Load

class Spritesheet(path: String) {
    companion object {
        private const val RESOURCES_PATH = "src/nativeMain/resources/"
    }

    val pixels: IntArray
    val width: Int
    val height: Int

    init {
        val originalSurface = IMG_Load(RESOURCES_PATH + path) ?: error("Failed to load image: ${RESOURCES_PATH + path}")
        val surface = SDL_ConvertSurface(originalSurface.reinterpret(), SDL_PIXELFORMAT_ARGB8888)
            ?: error("Failed to convert surface format")

        width = surface.pointed.w
        height = surface.pointed.h

        val surfacePixels = surface.pointed.pixels!!.reinterpret<UIntVar>()
        pixels = IntArray(width * height) { i -> convertPixel(surfacePixels[i]) }

        SDL_DestroySurface(originalSurface.reinterpret())
        SDL_DestroySurface(surface)
    }
}

/**
 * Converts a pixel value from ARGB format to an integer representation based on its transparency
 * and average brightness.
 *
 * The conversion process involves:
 * - Extracting the alpha (transparency), red, green, and blue components from the pixel.
 * - Checking the alpha value to determine if the pixel is transparent.
 * - Calculating the average brightness of the RGB components and mapping it to a specific range:
 *   - 0 for very dark pixels.
 *   - 1 for dark pixels.
 *   - 2 for bright pixels.
 *   - 3 for very bright pixels.
 *
 * @param pixel The pixel value in ARGB format as an unsigned integer.
 * @return An integer representation of the pixel based on its transparency and brightness.
 */
private fun convertPixel(pixel: UInt): Int {
    val a = (pixel shr 24) and 0xFFu
    val r = (pixel shr 16) and 0xFFu
    val g = (pixel shr 8) and 0xFFu
    val b = pixel and 0xFFu

    return if (a < 128u) 0 else when ((r + g + b) / 3u) {
        in 0u..63u -> 0
        in 64u..127u -> 1
        in 128u..191u -> 2
        else -> 3
    }
}
