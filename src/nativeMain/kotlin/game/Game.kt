@file:OptIn(ExperimentalForeignApi::class)

package game

import cnames.structs.SDL_Window
import cnames.structs.SDL_Renderer
import entity.*
import gfx.*
import input.*
import item.*
import kotlinx.cinterop.*
import level.*
import level.tile.Tile
import screen.*
import sdl.*
import util.*
import kotlin.random.Random
import kotlin.time.TimeSource

object Game {
    const val NAME = "Minicraft Kotlin/Native"
    const val HEIGHT = 120
    const val WIDTH = 160
    const val SCALE = 3

    val random: Random = uniqueRandom()
    val window: CPointer<SDL_Window>
    val texture: CPointer<SDL_Texture>
    val renderer: CPointer<SDL_Renderer>
    val pixels: IntArray = IntArray(WIDTH * SCALE * HEIGHT * SCALE)
    val colors: IntArray = IntArray(256)
    val screen: Screen
    val lightScreen: Screen

    var running = false
    var tickCount: Int = 0
    var gameTime: Int = 0
    var levels: Array<Level>? = null
    var level: Level? = null
    var player: Player? = null
    var currentLevel: Int = 3
    var menu: Menu? = TitleMenu()
    var pendingLevelChange: Int = 0
    var playerDeadTime: Int = 0
    var wonTimer: Int = 0
    var hasWon: Boolean = false
    var hasFocus: Boolean = true

    init {
        if (!SDL_Init(SDL_INIT_VIDEO)) error("SDL could not initialize! ${sdlError()}")

        val windowWidth = WIDTH * SCALE
        val windowHeight = HEIGHT * SCALE
        window = SDL_CreateWindow(NAME, windowWidth, windowHeight, SDL_WINDOW_HIDDEN)
            ?: error("Window could not be created! ${sdlError()}")
        renderer = SDL_CreateRenderer(window, null)
            ?: error("Renderer could not be created! ${sdlError()}")
        texture = SDL_CreateTexture(
            renderer, SDL_PIXELFORMAT_ARGB8888, SDL_TextureAccess.SDL_TEXTUREACCESS_STREAMING,
            WIDTH * SCALE, HEIGHT * SCALE
        ) ?: error("Texture could not be created! ${sdlError()}")
        screen = Screen(WIDTH, HEIGHT, Spritesheet("icons.png"))
        lightScreen = Screen(WIDTH, HEIGHT, Spritesheet("icons.png"))

        initColors()
    }
}

fun Game.initColors() {
    var pp = 0
    for (r in 0 until 6) {
        for (g in 0 until 6) {
            for (b in 0 until 6) {
                val rr = (r * 255 / 5)
                val gg = (g * 255 / 5)
                val bb = (b * 255 / 5)
                val mid = (rr * 30 + gg * 59 + bb * 11) / 100

                val r1 = ((rr + mid * 1) / 2) * 230 / 255 + 10
                val g1 = ((gg + mid * 1) / 2) * 230 / 255 + 10
                val b1 = ((bb + mid * 1) / 2) * 230 / 255 + 10
                colors[pp++] = (r1 shl 16) or (g1 shl 8) or b1
            }
        }
    }
}

fun Game.handleEvents() = memScoped {
    val event = alloc<SDL_Event>()
    while (SDL_PollEvent(event.ptr)) {
        InputHandler.handleEvent(event)

        when (event.type) {
            SDL_EVENT_QUIT -> running = false
            SDL_EVENT_WINDOW_RESTORED, SDL_EVENT_WINDOW_FOCUS_GAINED -> hasFocus = true
            SDL_EVENT_WINDOW_MINIMIZED, SDL_EVENT_WINDOW_FOCUS_LOST -> hasFocus = false
        }
    }
}

fun Game.render() {
    if(level != null && player != null) {
        var xScroll: Int = player!!.x - screen.w / 2
        var yScroll: Int = player!!.y - (screen.h - 8) / 2
        if (xScroll < 16) xScroll = 16
        if (yScroll < 16) yScroll = 16
        if (xScroll > level!!.w * 16 - screen.w - 16) xScroll = level!!.w * 16 - screen.w - 16
        if (yScroll > level!!.h * 16 - screen.h - 16) yScroll = level!!.h * 16 - screen.h - 16

        // Sky rendering for upper levels
        if (currentLevel > 3) {
            val col = Color.get(20, 20, 121, 121)
            for (y in 0 until 14) {
                for (x in 0 until 24) {
                    screen.render(
                        x * 8 - ((xScroll / 4) and 7), y * 8 - ((yScroll / 4) and 7), 0, col, 0
                    )
                }
            }
        }

        // Render level
        level!!.renderBackground(screen, xScroll, yScroll)
        level!!.renderSprites(screen, xScroll, yScroll)

        if (currentLevel < 3) {
            lightScreen.clear(0)
            level!!.renderLight(lightScreen, xScroll, yScroll)
            screen.overlay(lightScreen, xScroll, yScroll)
        }
        renderGui()
    }

    menu?.render(screen)
    if (!hasFocus) renderFocusNagger()
    renderPixelsToWindow()
}

fun Game.renderPixelsToWindow() = memScoped {
    for (y in 0 until HEIGHT) {
        for (x in 0 until WIDTH) {
            val cc = screen.pixels[x + y * screen.w]
            if (cc < 255) {
                val color = colors[cc] or (0xFF shl 24) // Alpha channel

                // Preencher bloco escalado
                for (sy in 0 until SCALE) {
                    for (sx in 0 until SCALE) {
                        val targetX = x * SCALE + sx
                        val targetY = y * SCALE + sy
                        pixels[targetX + targetY * (WIDTH * SCALE)] = color
                    }
                }
            }
        }
    }

    pixels.usePinned { pinnedPixels ->
        SDL_UpdateTexture(texture, null, pinnedPixels.addressOf(0), WIDTH * SCALE * 4)
    }

    SDL_RenderTexture(renderer, texture, null, null)
    SDL_RenderPresent(renderer)
}

fun Game.renderGui() {
    val player = this.player ?: return

    // Render GUI background
    for (y in 0 until 2) {
        for (x in 0 until 20) {
            screen.render(x * 8, screen.h - 16 + y * 8, 0 + 12 * 32, Color.get(0, 0, 0, 0), 0)
        }
    }

    // Render health and stamina bars
    for (i in 0 until 10) {
        if (i < player.health) {
            screen.render(i * 8, screen.h - 16, 0 + 12 * 32, Color.get(0, 200, 500, 533), 0)
        } else {
            screen.render(i * 8, screen.h - 16, 0 + 12 * 32, Color.get(0, 100, 0, 0), 0)
        }

        if (player.staminaRechargeDelay > 0) {
            if (player.staminaRechargeDelay / 4 % 2 == 0) screen.render(
                i * 8, screen.h - 8, 1 + 12 * 32, Color.get(0, 555, 0, 0), 0
            )
            else screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(0, 110, 0, 0), 0)
        } else {
            if (i < player.stamina) screen.render(
                i * 8, screen.h - 8, 1 + 12 * 32, Color.get(0, 220, 550, 553), 0
            )
            else screen.render(i * 8, screen.h - 8, 1 + 12 * 32, Color.get(0, 110, 0, 0), 0)
        }
    }

    player.activeItem?.renderInventory(screen, 10 * 8, screen.h - 16)
}

fun Game.renderFocusNagger() {
    val msg = "Click to focus!"
    val xx: Int = (WIDTH - msg.length * 8) / 2
    val yy: Int = (HEIGHT - 8) / 2
    val w = msg.length
    val h = 1

    screen.render(xx - 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 0)
    screen.render(xx + w * 8, yy - 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 1)
    screen.render(xx - 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 2)
    screen.render(xx + w * 8, yy + 8, 0 + 13 * 32, Color.get(-1, 1, 5, 445), 3)
    for (x in 0..<w) {
        screen.render(xx + x * 8, yy - 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 0)
        screen.render(xx + x * 8, yy + 8, 1 + 13 * 32, Color.get(-1, 1, 5, 445), 2)
    }
    for (y in 0..<h) {
        screen.render(xx - 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 0)
        screen.render(xx + w * 8, yy + y * 8, 2 + 13 * 32, Color.get(-1, 1, 5, 445), 1)
    }

    if ((tickCount / 20) % 2 == 0) {
        Font.draw(msg, screen, xx, yy, Color.get(5, 333, 333, 333))
    } else {
        Font.draw(msg, screen, xx, yy, Color.get(5, 555, 555, 555))
    }
}


fun Game.cleanup() {
    SDL_DestroyWindow(window)
    SDL_Quit()
}

fun Game.run() {
    var lastTime = SDL_GetPerformanceCounter()
    val frequency = SDL_GetPerformanceFrequency()
    var unprocessed = 0.0
    val nsPerTick = frequency.toDouble() / 60.0
    var frames = 0
    var ticks = 0
    var lastTimer1 = SDL_GetTicks()

    SDL_ShowWindow(window)

    running = true
    while (running) {
        handleEvents()

        val now = SDL_GetPerformanceCounter()
        unprocessed += (now - lastTime).toDouble() / nsPerTick
        lastTime = now

        var shouldRender = false
        while (unprocessed >= 1.0) {
            ticks++
            tick()
            unprocessed -= 1.0
            shouldRender = true
        }

        SDL_Delay(2u)

        if (shouldRender) {
            frames++
            render()
        }

        val currentTime = SDL_GetTicks()
        if (currentTime - lastTimer1 > 1000u) {
            lastTimer1 += 1000u
            println("$ticks ticks, $frames fps")
            frames = 0
            ticks = 0
        }
    }

    cleanup()
}

fun Game.tick() {
    tickCount++
    if (!hasFocus) {
        InputHandler.releaseAll()
        return
    }
    if (player != null && !player!!.removed && !hasWon) gameTime++

    InputHandler.tick()
    if (menu != null) {
        menu!!.tick()
    } else {
        if (player != null && player!!.removed) {
            playerDeadTime++
            if (playerDeadTime > 60) {
                menu = DeadMenu()
            }
        } else {
            if (pendingLevelChange != 0) {
                menu = LevelTransitionMenu(pendingLevelChange)
                pendingLevelChange = 0
            }
        }
        if (wonTimer > 0) {
            if (--wonTimer == 0) {
                menu = WonMenu()
            }
        }
        level?.tick()
        Tile.tickCount++
    }
}

fun Game.resetGame() {
    playerDeadTime = 0
    wonTimer = 0
    gameTime = 0
    hasWon = false
    currentLevel = 3

    val level4 = Level(128, 128, 1, null)
    val level3 = Level(128, 128, 0, level4)
    val level2 =  Level(128, 128, -1, level3)
    val level1 =  Level(128, 128, 0, level2)
    val level0 =  Level(128, 128, 0, level1)

    levels = arrayOf(level4, level3, level2, level1, level0)
    level = levels!![currentLevel]

    player = Player()
    player!!.findStartPos(level!!)

    level?.add(player!!)

    val timeStart = TimeSource.Monotonic.markNow()
    for (i in 0 until 5) {
        levels!![i].trySpawn(5000)
    }
    val timeEnd = TimeSource.Monotonic.markNow()
    println ("Level spawn took: ${timeEnd - timeStart}")
}

fun Game.scheduleLevelChange(dir: Int) {
    pendingLevelChange = dir
}

fun Game.changeLevel(dir: Int) {
    if(level == null || player == null) return

    level!!.remove(player!!)
    currentLevel += dir
    level = levels!![currentLevel]
    player!!.x = (player!!.x shr 4) * 16 + 8
    player!!.y = (player!!.y shr 4) * 16 + 8
    level!!.add(player!!)
}

fun Game.won() {
    wonTimer = 60 * 3
    hasWon = true
}
