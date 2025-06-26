@file:OptIn(ExperimentalForeignApi::class)

import cnames.structs.SDL_Window
import entity.Player
import gfx.*
import kotlinx.cinterop.*
import level.Level
import level.add
import level.remove
import screen.Menu
import sdl.*
import util.sdlError
import util.uniqueRandom
import kotlin.random.Random

object Game {
    const val NAME = "Kt SDL3 Game"
    const val HEIGHT = 120
    const val WIDTH = 160
    const val SCALE = 3

    val window: CPointer<SDL_Window>
    val screen: Screen
    val lightScreen: Screen

    val random: Random = uniqueRandom()
    val colors: IntArray = IntArray(256).apply {
        var pp = 0
        for (r in 0..5) {
            for (g in 0..5) {
                for (b in 0..5) {
                    val rr = (r * 255 / 5)
                    val gg = (g * 255 / 5)
                    val bb = (b * 255 / 5)
                    val mid = (rr * 30 + gg * 59 + bb * 11) / 100

                    val r1 = ((rr + mid * 1) / 2) * 230 / 255 + 10
                    val g1 = ((gg + mid * 1) / 2) * 230 / 255 + 10
                    val b1 = ((bb + mid * 1) / 2) * 230 / 255 + 10
                    this[pp++] = r1 shl 16 or (g1 shl 8) or b1
                }
            }
        }
    }

    var running = false
    var tickCount: Int = 0
    var gameTime: Int = 0
    var level: Level? = null
    val levels: MutableList<Level> = ArrayList()
    var currentLevel: Int = 3
    var menu: Menu? = null
    var player: Player? = null
    var pendingLevelChange: Int = 0
    var playerDeadTime: Int = 0
    var wonTimer: Int = 0
    var hasWon: Boolean = false

    init {
        if (!SDL_Init(SDL_INIT_VIDEO)) {
            error("SDL could not initialize! ${sdlError()}")
        }

        window = SDL_CreateWindow(
            NAME, WIDTH * SCALE, HEIGHT * SCALE, SDL_WINDOW_BORDERLESS
        ) ?: error("Window could not be created! ${sdlError()}")
        screen = Screen(
            WIDTH, HEIGHT, Spritesheet("src/nativeMain/resources/icons.png")
        )
        lightScreen = Screen(
            WIDTH, HEIGHT, Spritesheet("src/nativeMain/resources/icons.png")
        )
        running = true
    }
}

fun Game.handleEvents() = memScoped {
    val event = alloc<SDL_Event>()
    while (SDL_PollEvent(event.ptr)) {
        InputHandler.handleEvent(event)

        when (event.type) {
            SDL_EVENT_QUIT -> running = false
        }
    }
}

fun Game.update() {
    TODO()
}

fun Game.render() {
    screen.clear(255, 0, 0)
    screen.render(0, 0, 0, 0, 0)
}

fun Game.cleanup() {
    screen.sheet.cleanup()
    SDL_DestroyWindow(window)
    SDL_Quit()
}

fun Game.run() {
    while (running) {
        handleEvents()
        update()
        render()
    }

    cleanup()
}

fun Game.resetGame() {
    TODO()
}

fun Game.scheduleLevelChange(dir: Int) {
    pendingLevelChange = dir
}

fun Game.changeLevel(dir: Int) {
    var level = level ?: error("Level is not initialized!")
    val player = player ?: error("Player is not initialized!")

    level.remove(player)
    currentLevel += dir
    level = levels[currentLevel]
    player.x = (player.x shr 4) * 16 + 8
    player.y = (player.y shr 4) * 16 + 8
    level.add(player)
    this.level = level
}

fun Game.won() {
    wonTimer = 60 * 3
    hasWon = true
}

fun main() {
    Game.run()
}