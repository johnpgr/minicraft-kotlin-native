@file:OptIn(ExperimentalForeignApi::class)

package gfx

import kotlinx.cinterop.*
import sdl.*
import sdl_image.IMG_Load

class Spritesheet(path: String) {
    companion object {
        const val RESOURCES_PATH = "src/nativeMain/resources/"
    }

    val pixels: IntArray
    val width: Int
    val height: Int

    init {
        val originalSurface =
            IMG_Load(RESOURCES_PATH + path) ?: error("Failed to load image: ${RESOURCES_PATH + path}")

        val surface = SDL_ConvertSurface(originalSurface.reinterpret(), SDL_PIXELFORMAT_ARGB8888)
            ?: error("Failed to convert surface format")

        width = surface.pointed.w
        height = surface.pointed.h
        pixels = IntArray(width * height)

        val surfacePixels = surface.pointed.pixels!!.reinterpret<UIntVar>()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelIndex = y * width + x
                val pixel = surfacePixels[pixelIndex]

                val a = (pixel shr 24) and 0xFFu
                val r = (pixel shr 16) and 0xFFu
                val g = (pixel shr 8) and 0xFFu
                val b = pixel and 0xFFu

                if (a < 128u) {
                    pixels[pixelIndex] = 0
                } else {
                    val brightness = (r + g + b) / 3u
                    pixels[pixelIndex] = when {
                        brightness < 64u -> 0
                        brightness < 128u -> 1
                        brightness < 192u -> 2
                        else -> 3
                    }
                }
            }
        }

        SDL_DestroySurface(originalSurface.reinterpret())
        SDL_DestroySurface(surface)
    }
}