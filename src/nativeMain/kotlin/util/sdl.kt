@file:OptIn(ExperimentalForeignApi::class)

package util

import kotlinx.cinterop.*
import sdl.*

fun sdlError(): String? = SDL_GetError()?.toString()