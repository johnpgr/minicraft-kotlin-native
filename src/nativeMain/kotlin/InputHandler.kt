@file:OptIn(ExperimentalForeignApi::class)

import kotlinx.cinterop.*
import sdl.*

object InputHandler {
    class Key {
        var presses = 0
        var absorbs = 0
        var down = false
        var clicked = false

        init {
            keys.add(this)
        }

        fun toggle(pressed: Boolean) {
            if (pressed != down) {
                down = pressed
                if (pressed) presses++
            }
        }

        fun tick() {
            clicked = absorbs < presses
            if (clicked) absorbs++
        }
    }

    val keys = mutableListOf<Key>()
    val up = Key()
    val down = Key()
    val left = Key()
    val right = Key()
    val attack = Key()
    val menu = Key()
}

fun InputHandler.releaseAll() = keys.forEach { it.down = false }

fun InputHandler.tick() = keys.forEach { it.tick() }

fun InputHandler.handleEvent(ev: SDL_Event) {
    when (ev.type) {
        SDL_EVENT_KEY_DOWN -> toggle(ev.key.key, true)
        SDL_EVENT_KEY_UP -> toggle(ev.key.key, false)
    }
}

fun InputHandler.toggle(keycode: SDL_Keycode, pressed: Boolean) {
    when (keycode) {
        // Movimentação
        SDLK_KP_8, SDLK_W, SDLK_UP -> up.toggle(pressed)
        SDLK_KP_2, SDLK_S, SDLK_DOWN -> down.toggle(pressed)
        SDLK_KP_4, SDLK_A, SDLK_LEFT -> left.toggle(pressed)
        SDLK_KP_6, SDLK_D, SDLK_RIGHT -> right.toggle(pressed)

        // Menu
        SDLK_TAB, SDLK_LALT, SDLK_RALT, SDLK_MODE, SDLK_RETURN, SDLK_X -> menu.toggle(
            pressed
        )

        // Ataque
        SDLK_SPACE, SDLK_LCTRL, SDLK_RCTRL, SDLK_KP_0, SDLK_INSERT, SDLK_C -> attack.toggle(
            pressed
        )
    }
}