package level.levelgen

import level.tile.Tile
import util.uniqueRandom
import kotlin.experimental.and
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.random.Random

class LevelGen(
    val w: Int,
    val h: Int,
    val featureSize: Int,
    val values: DoubleArray = DoubleArray(w * h),
) {
    init {
        var y = 0
        while (y < w) {
            var x = 0
            while (x < w) {
                setSample(x, y, random.nextDouble() * 2 - 1)
                x += featureSize
            }
            y += featureSize
        }

        var stepSize = featureSize
        var scale = 1.0 / w
        var scaleMod = 1.0
        do {
            val halfStep = stepSize / 2
            run {
                var y = 0
                while (y < w) {
                    var x = 0
                    while (x < w) {
                        val a = sample(x, y)
                        val b = sample(x + stepSize, y)
                        val c = sample(x, y + stepSize)
                        val d = sample(x + stepSize, y + stepSize)

                        val e: Double =
                            (a + b + c + d) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale
                        setSample(x + halfStep, y + halfStep, e)
                        x += stepSize
                    }
                    y += stepSize
                }
            }
            var y = 0
            while (y < w) {
                var x = 0
                while (x < w) {
                    val a = sample(x, y)
                    val b = sample(x + stepSize, y)
                    val c = sample(x, y + stepSize)
                    val d = sample(x + halfStep, y + halfStep)
                    val e = sample(x + halfStep, y - halfStep)
                    val f = sample(x - halfStep, y + halfStep)

                    val H: Double =
                        (a + b + d + e) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5
                    val g: Double =
                        (a + c + d + f) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5
                    setSample(x + halfStep, y, H)
                    setSample(x, y + halfStep, g)
                    x += stepSize
                }
                y += stepSize
            }
            stepSize /= 2
            scale *= (scaleMod + 0.8)
            scaleMod *= 0.3
        } while (stepSize > 1)
    }

    companion object {
        private val random: Random = uniqueRandom()

        fun createAndValidateTopMap(w: Int, h: Int): Array<ByteArray> {
            val attempt = 0
            do {
                val result = createTopMap(w, h)

                val count = IntArray(256)

                for (i in 0..<w * h) {
                    count[result[0][i].toInt() and 0xff]++
                }
                if (count[(Tile.rock.id and 0xff.toByte()).toInt()] < 100) continue
                if (count[(Tile.sand.id and 0xff.toByte()).toInt()] < 100) continue
                if (count[(Tile.grass.id and 0xff.toByte()).toInt()] < 100) continue
                if (count[(Tile.tree.id and 0xff.toByte()).toInt()] < 100) continue
                if (count[(Tile.stairsDown.id and 0xff.toByte()).toInt()] < 2) continue

                return result
            } while (true)
        }

        fun createAndValidateUndergroundMap(
            w: Int, h: Int, depth: Int
        ): Array<ByteArray> {
            val attempt = 0
            do {
                val result = createUndergroundMap(w, h, depth)

                val count = IntArray(256)

                for (i in 0..<w * h) {
                    count[result[0]!![i].toInt() and 0xff]++
                }
                if (count[(Tile.rock.id and 0xff.toByte()).toInt()] < 100) continue
                if (count[(Tile.dirt.id and 0xff.toByte()).toInt()] < 100) continue
                if (count[(Tile.ironOre.id and 0xff.toByte()) + depth - 1] < 20) continue
                if (depth < 3) if (count[(Tile.stairsDown.id and 0xff.toByte()).toInt()] < 2) continue

                return result
            } while (true)
        }

        fun createAndValidateSkyMap(w: Int, h: Int): Array<ByteArray> {
            val attempt = 0
            do {
                val result = createSkyMap(w, h)

                val count = IntArray(256)

                for (i in 0..<w * h) {
                    count[result[0]!![i].toInt() and 0xff]++
                }
                if (count[(Tile.cloud.id and 0xff.toByte()).toInt()] < 2000) continue
                if (count[(Tile.stairsDown.id and 0xff.toByte()).toInt()] < 2) continue

                return result
            } while (true)
        }

        private fun createTopMap(w: Int, h: Int): Array<ByteArray> {
            val mnoise1 = LevelGen(w, h, 16)
            val mnoise2 = LevelGen(w, h, 16)
            val mnoise3 = LevelGen(w, h, 16)

            val noise1 = LevelGen(w, h, 32)
            val noise2 = LevelGen(w, h, 32)

            val map = ByteArray(w * h)
            val data = ByteArray(w * h)
            for (y in 0..<h) {
                for (x in 0..<w) {
                    val i = x + y * w

                    var `val` = abs(noise1.values[i] - noise2.values[i]) * 3 - 2
                    var mval = abs(mnoise1.values[i] - mnoise2.values[i])
                    mval = abs(mval - mnoise3.values[i]) * 3 - 2

                    var xd = x / (w - 1.0) * 2 - 1
                    var yd = y / (h - 1.0) * 2 - 1
                    if (xd < 0) xd = -xd
                    if (yd < 0) yd = -yd
                    var dist = if (xd >= yd) xd else yd
                    dist = dist * dist * dist * dist
                    dist = dist * dist * dist * dist
                    `val` = `val` + 1 - dist * 20

                    if (`val` < -0.5) {
                        map[i] = Tile.water.id
                    } else if (`val` > 0.5 && mval < -1.5) {
                        map[i] = Tile.rock.id
                    } else {
                        map[i] = Tile.grass.id
                    }
                }
            }

            for (i in 0..<w * h / 2800) {
                val xs: Int = random.nextInt(w)
                val ys: Int = random.nextInt(h)
                for (k in 0..9) {
                    val x: Int = xs + random.nextInt(21) - 10
                    val y: Int = ys + random.nextInt(21) - 10
                    for (j in 0..99) {
                        val xo: Int = x + random.nextInt(5) - random.nextInt(5)
                        val yo: Int = y + random.nextInt(5) - random.nextInt(5)
                        for (yy in yo - 1..yo + 1) for (xx in xo - 1..xo + 1) if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                            if (map[xx + yy * w] == Tile.grass.id) {
                                map[xx + yy * w] = Tile.sand.id
                            }
                        }
                    }
                }
            }

            /*
		 * for (int i = 0; i < w * h / 2800; i++) { int xs = random.nextInt(w); int ys = random.nextInt(h); for (int k = 0; k < 10; k++) { int x = xs + random.nextInt(21) - 10; int y = ys + random.nextInt(21) - 10; for (int j = 0; j < 100; j++) { int xo = x + random.nextInt(5) - random.nextInt(5); int yo = y + random.nextInt(5) - random.nextInt(5); for (int yy = yo - 1; yy <= yo + 1; yy++) for (int xx = xo - 1; xx <= xo + 1; xx++) if (xx >= 0 && yy >= 0 && xx < w && yy < h) { if (map[xx + yy * w] == Tile.grass.id) { map[xx + yy * w] = Tile.dirt.id; } } } } }
		 */
            for (i in 0..<w * h / 400) {
                val x: Int = random.nextInt(w)
                val y: Int = random.nextInt(h)
                for (j in 0..199) {
                    val xx: Int = x + random.nextInt(15) - random.nextInt(15)
                    val yy: Int = y + random.nextInt(15) - random.nextInt(15)
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tile.grass.id) {
                            map[xx + yy * w] = Tile.tree.id
                        }
                    }
                }
            }

            for (i in 0..<w * h / 400) {
                val x: Int = random.nextInt(w)
                val y: Int = random.nextInt(h)
                val col: Int = random.nextInt(4)
                for (j in 0..29) {
                    val xx: Int = x + random.nextInt(5) - random.nextInt(5)
                    val yy: Int = y + random.nextInt(5) - random.nextInt(5)
                    if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                        if (map[xx + yy * w] == Tile.grass.id) {
                            map[xx + yy * w] = Tile.flower.id
                            data[xx + yy * w] =
                                (col + random.nextInt(4) * 16) as Byte
                        }
                    }
                }
            }

            for (i in 0..<w * h / 100) {
                val xx: Int = random.nextInt(w)
                val yy: Int = random.nextInt(h)
                if (xx >= 0 && yy >= 0 && xx < w && yy < h) {
                    if (map[xx + yy * w] == Tile.sand.id) {
                        map[xx + yy * w] = Tile.cactus.id
                    }
                }
            }

            var count = 0
            stairsLoop@ for (i in 0..<w * h / 100) {
                val x: Int = random.nextInt(w - 2) + 1
                val y: Int = random.nextInt(h - 2) + 1

                for (yy in y - 1..y + 1) for (xx in x - 1..x + 1) {
                    if (map[xx + yy * w] != Tile.rock.id) continue@stairsLoop
                }

                map[x + y * w] = Tile.stairsDown.id
                count++
                if (count == 4) break
            }

            return arrayOf<ByteArray>(map, data)
        }

        private fun createUndergroundMap(
            w: Int, h: Int, depth: Int
        ): Array<ByteArray> {
            val mnoise1 = LevelGen(w, h, 16)
            val mnoise2 = LevelGen(w, h, 16)
            val mnoise3 = LevelGen(w, h, 16)

            val nnoise1 = LevelGen(w, h, 16)
            val nnoise2 = LevelGen(w, h, 16)
            val nnoise3 = LevelGen(w, h, 16)

            val wnoise1 = LevelGen(w, h, 16)
            val wnoise2 = LevelGen(w, h, 16)
            val wnoise3 = LevelGen(w, h, 16)

            val noise1 = LevelGen(w, h, 32)
            val noise2 = LevelGen(w, h, 32)

            val map = ByteArray(w * h)
            val data = ByteArray(w * h)
            for (y in 0..<h) {
                for (x in 0..<w) {
                    val i = x + y * w

                    var `val` = abs(noise1.values[i] - noise2.values[i]) * 3 - 2

                    var mval = abs(mnoise1.values[i] - mnoise2.values[i])
                    mval = abs(mval - mnoise3.values[i]) * 3 - 2

                    var nval = abs(nnoise1.values[i] - nnoise2.values[i])
                    nval = abs(nval - nnoise3.values[i]) * 3 - 2

                    var wval = abs(wnoise1.values[i] - wnoise2.values[i])
                    wval = abs(nval - wnoise3.values[i]) * 3 - 2

                    var xd = x / (w - 1.0) * 2 - 1
                    var yd = y / (h - 1.0) * 2 - 1
                    if (xd < 0) xd = -xd
                    if (yd < 0) yd = -yd
                    var dist = if (xd >= yd) xd else yd
                    dist = dist * dist * dist * dist
                    dist = dist * dist * dist * dist
                    `val` = `val` + 1 - dist * 20

                    if (`val` > -2 && wval < -2.0 + (depth) / 2 * 3) {
                        if (depth > 2) map[i] = Tile.lava.id
                        else map[i] = Tile.water.id
                    } else if (`val` > -2 && (mval < -1.7 || nval < -1.4)) {
                        map[i] = Tile.dirt.id
                    } else {
                        map[i] = Tile.rock.id
                    }
                }
            }

            run {
                val r = 2
                for (i in 0..<w * h / 400) {
                    val x: Int = Companion.random.nextInt(w)
                    val y: Int = Companion.random.nextInt(h)
                    for (j in 0..29) {
                        val xx: Int =
                            x + Companion.random.nextInt(5) - Companion.random.nextInt(
                                5
                            )
                        val yy: Int =
                            y + Companion.random.nextInt(5) - Companion.random.nextInt(
                                5
                            )
                        if (xx >= r && yy >= r && xx < w - r && yy < h - r) {
                            if (map[xx + yy * w] == Tile.rock.id) {
                                map[xx + yy * w] =
                                    ((Tile.ironOre.id and 0xff.toByte()) + depth - 1) as Byte
                            }
                        }
                    }
                }
            }

            if (depth < 3) {
                var count = 0
                stairsLoop@ for (i in 0..<w * h / 100) {
                    val x: Int = random.nextInt(w - 20) + 10
                    val y: Int = random.nextInt(h - 20) + 10

                    for (yy in y - 1..y + 1) for (xx in x - 1..x + 1) {
                        if (map[xx + yy * w] != Tile.rock.id) continue@stairsLoop
                    }

                    map[x + y * w] = Tile.stairsDown.id
                    count++
                    if (count == 4) break
                }
            }

            return arrayOf<ByteArray>(map, data)
        }

        private fun createSkyMap(w: Int, h: Int): Array<ByteArray> {
            val noise1 = LevelGen(w, h, 8)
            val noise2 = LevelGen(w, h, 8)

            val map = ByteArray(w * h)
            val data = ByteArray(w * h)
            for (y in 0..<h) {
                for (x in 0..<w) {
                    val i = x + y * w

                    var `val` = abs(noise1.values[i] - noise2.values[i]) * 3 - 2

                    var xd = x / (w - 1.0) * 2 - 1
                    var yd = y / (h - 1.0) * 2 - 1
                    if (xd < 0) xd = -xd
                    if (yd < 0) yd = -yd
                    var dist = if (xd >= yd) xd else yd
                    dist = dist * dist * dist * dist
                    dist = dist * dist * dist * dist
                    `val` = -`val` * 1 - 2.2
                    `val` = `val` + 1 - dist * 20

                    if (`val` < -0.25) {
                        map[i] = Tile.infiniteFall.id
                    } else {
                        map[i] = Tile.cloud.id
                    }
                }
            }

            stairsLoop@ for (i in 0..<w * h / 50) {
                val x: Int = random.nextInt(w - 2) + 1
                val y: Int = random.nextInt(h - 2) + 1

                for (yy in y - 1..y + 1) for (xx in x - 1..x + 1) {
                    if (map[xx + yy * w] != Tile.cloud.id) continue@stairsLoop
                }

                map[x + y * w] = Tile.cloudCactus.id
            }

            var count = 0
            stairsLoop@ for (i in 0..<w * h) {
                val x: Int = random.nextInt(w - 2) + 1
                val y: Int = random.nextInt(h - 2) + 1

                for (yy in y - 1..y + 1) for (xx in x - 1..x + 1) {
                    if (map[xx + yy * w] != Tile.cloud.id) continue@stairsLoop
                }

                map[x + y * w] = Tile.stairsDown.id
                count++
                if (count == 2) break
            }

            return arrayOf<ByteArray>(map, data)
        }
    }

    private fun sample(x: Int, y: Int): Double {
        return values[(x and (w - 1)) + (y and (h - 1)) * w]
    }

    private fun setSample(x: Int, y: Int, value: Double) {
        values[(x and (w - 1)) + (y and (h - 1)) * w] = value
    }
}
