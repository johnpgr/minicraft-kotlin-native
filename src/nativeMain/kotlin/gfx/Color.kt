package gfx

/**
 * Objeto utilitário para manipulação de cores.
 * Fornece métodos para converter valores numéricos em cores RGBA.
 */
object Color {
    /**
     * Gera uma cor RGBA a partir de quatro componentes inteiros.
     * @param a Componente alfa (transparência)
     * @param b Componente vermelho
     * @param c Componente verde
     * @param d Componente azul
     * @return Int representando a cor no formato RGBA
     */
    fun get(a: Int, b: Int, c: Int, d: Int): Int {
        return (get(d) shl 24) + (get(c) shl 16) + (get(b) shl 8) + (get(a))
    }

    /**
     * Converte um valor inteiro em um componente de cor.
     * @param d Valor inteiro para conversão (formato: RGB onde cada dígito é um valor de 0-9)
     * @return Int representando o componente de cor calculado
     */
    fun get(d: Int): Int {
        if (d < 0) return 255
        val r = d / 100 % 10
        val g = d / 10 % 10
        val b = d % 10
        return r * 36 + g * 6 + b
    }
}