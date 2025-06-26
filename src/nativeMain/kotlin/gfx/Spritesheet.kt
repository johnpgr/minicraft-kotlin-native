@file:OptIn(ExperimentalForeignApi::class)

package gfx

import kotlinx.cinterop.*
import sdl.SDL_DestroySurface
import sdl_image.IMG_Load
import sdl_image.SDL_Surface

class Spritesheet(path: String) {
    val pixels: IntArray
    val width: Int
    val height: Int

    init {
        val surface: CPointer<SDL_Surface> =
            IMG_Load(path) ?: error("Failed to load image: $path")

        width = surface.pointed.w
        height = surface.pointed.h
        pixels = IntArray(width * height)

        val surfacePixels = surface.pointed.pixels!!.reinterpret<IntVar>()
        for (i in 0 until width * height) {
            pixels[i] = surfacePixels[i]
        }

        SDL_DestroySurface(surface.reinterpret())
    }
}