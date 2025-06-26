package gfx

object Color {
    fun get(a: Int, r: Int, g: Int, b: Int): Int {
        return (get(b) shl 24) + (get(g) shl 16) + (get(r) shl 8) + (get(a))
    }
    fun get(c: Int): Int {
        if (c < 0) return 255
        val r = c / 100 % 10
        val g = c / 10 % 10
        val b = c % 10
        return r * 36 + g * 6 + b
    }
}