@file:OptIn(ExperimentalAtomicApi::class)

package util

import kotlin.math.*
import kotlin.random.Random
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private const val multiplier = 0x5DEECE66DL
private val mask = (1L shl 48) - 1
private val seedUniquifier = AtomicLong(8682522807148012)
private fun seedUniquifier(): Long {
    // L'Ecuyer, "Tables of Linear Congruential Generators of
    // Different Sizes and Good Lattice Structure", 1999
    while (true) {
        val current: Long = seedUniquifier.load()
        val next = current * 1181783497276652981L
        if (seedUniquifier.compareAndSet(current, next)) return next
    }
}

private fun initialScramble(seed: Long): Long {
    return (seed xor multiplier) and mask
}

fun uniqueRandom(): Random {
    return Random(seedUniquifier())
}

fun Random.setSeed(seed: Long): Random {
    return Random(initialScramble(seed))
}

fun Random.nextGaussian(): Double {
    var v1: Double
    var v2: Double
    var s: Double
    do {
        v1 = 2 * nextDouble() - 1
        v2 = 2 * nextDouble() - 1
        s = v1 * v1 + v2 * v2
    } while (s >= 1 || s == 0.0)
    val multiplier = sqrt(-2 * ln(s) / s)
    return v1 * multiplier
}