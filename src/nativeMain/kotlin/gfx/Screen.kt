@file:OptIn(ExperimentalForeignApi::class)

package gfx

import cnames.structs.SDL_Window
import kotlinx.cinterop.*

class Screen(val w: Int, val h: Int, val sheet: Spritesheet) {
    companion object {
        const val BIT_MIRROR_X: Int = 0x01
        const val BIT_MIRROR_Y: Int = 0x02
    }

    val pixels: IntArray = IntArray(w * h)

    var xOffset: Int = 0
    var yOffset: Int = 0

    val dither =
        intArrayOf(0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5)
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

fun Screen.clear(r: Int, g: Int, b: Int, a: Int = 255) {
    clear((a shl 24) or (r shl 16) or (g shl 8) or b)
}

/**
 * Renderiza um bloco (tile) de 8×8 pixels na superfície da janela.
 *
 * @param yp Posição vertical de desenho (em pixels, antes de aplicar `yOffset`).
 * @param xp Posição horizontal de desenho (em pixels, antes de aplicar `xOffset`).
 * @param tile Índice do tile na spritesheet (0-based, leitura em linhas de 32 tiles).
 * @param colors Máscara de cores aplicada ao valor do pixel (cada byte representa uma paleta).
 * @param bits Flags de espelhamento (usar BIT_MIRROR_X e/ou BIT_MIRROR_Y).
 *
 * O cálculo interno considera:
 * - `xp` e `yp`: posições ajustadas pelos offsets da tela.
 * - `mirrorX` e `mirrorY`: definem se cada linha ou coluna do tile será invertida.
 * - `toffs`: deslocamento inicial no array de pixels da spritesheet.
 *
 * Pixels com valor de cor >= 255 são ignorados (transparência).
 */
fun Screen.render(yp: Int, xp: Int, tile: Int, colors: Int, bits: Int) {
    var xp = xp
    var yp = yp
    xp -= xOffset
    yp -= yOffset
    val mirrorX = (bits and Screen.BIT_MIRROR_X) > 0
    val mirrorY = (bits and Screen.BIT_MIRROR_Y) > 0

    val xTile: Int = tile % 32
    val yTile: Int = tile / 32
    val toffs = xTile * 8 + yTile * 8 * sheet.width

    for (y in 0..7) {
        var ys = y
        if (mirrorY) ys = 7 - y
        if (y + yp < 0 || y + yp >= h) continue
        for (x in 0..7) {
            if (x + xp < 0 || x + xp >= w) continue

            var xs = x
            if (mirrorX) xs = 7 - x
            val col: Int =
                (colors shr (sheet.pixels[xs + ys * sheet.width + toffs] * 8)) and 255
            if (col < 255) pixels[(x + xp) + (y + yp) * w] = col
        }
    }
}

/**
 * Aplica um efeito de overlay na tela atual usando dithering.
 *
 * @param screen2 Tela de origem do overlay.
 * @param xa Deslocamento horizontal aplicado ao padrão de dithering 4×4.
 * @param ya Deslocamento vertical aplicado ao padrão de dithering 4×4.
 *
 * Para cada pixel de `screen2`, obtém-se o valor de cor dividido por 10.
 * Se esse valor for menor ou igual ao elemento correspondente na matriz
 * `dither` (índice: `(x + xa) and 3` + `(y + ya) and 3 * 4`),
 * o pixel na tela atual (`this`) é definido para 0 (preto).
 */
fun Screen.overlay(screen2: Screen, xa: Int, ya: Int) {
    val oPixels = screen2.pixels
    var i = 0
    for (y in 0..<h) {
        for (x in 0..<w) {
            if (oPixels[i] / 10 <= dither[((x + xa) and 3) + ((y + ya) and 3) * 4]) pixels[i] =
                0
            i++
        }
    }
}

/**
 * Aplica um efeito de iluminação radial na tela atual.
 *
 * @param x Coordenada X do centro da fonte de luz (em pixels, antes de aplicar `xOffset`).
 * @param y Coordenada Y do centro da fonte de luz (em pixels, antes de aplicar `yOffset`).
 * @param r Raio de alcance da luz (em pixels).
 *
 * O processamento:
 * - Ajusta as coordenadas pelo deslocamento da tela (`xOffset`, `yOffset`).
 * - Define um retângulo de varredura limitado aos limites da tela, de `(x - r)` a `(x + r)` e
 *   `(y - r)` a `(y + r)`.
 * - Para cada pixel dentro desse retângulo, calcula a distância ao centro da luz.
 * - Se a distância for <= r, calcula o brilho como `br = 255 - (dist² * 255) / (r²)`.
 * - Atualiza o pixel se o valor calculado for maior que o valor atual (`pixels[xx + yy * w]`).
 */
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