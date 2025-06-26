@file:OptIn(ExperimentalForeignApi::class)

package gfx

import kotlinx.cinterop.*
import sdl.SDL_DestroySurface
import sdl_image.*

class Spritesheet(path: String) {
    val surface: CPointer<SDL_Surface> =
        IMG_Load(path) ?: error("Failed to load image: $path")
    val width: Int = surface.pointed.w
    val height: Int = surface.pointed.h
    val pixels: CPointer<IntVar> = surface.pointed.pixels!!.reinterpret()
}

fun Spritesheet.cleanup() {
    SDL_DestroySurface(surface.reinterpret())
}