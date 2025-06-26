package screen

import gfx.*

sealed interface Menu

fun Menu.tick() {
}

fun Menu.render(screen: Screen) {
}

inline fun <T> Menu.renderItemList(
    screen: Screen,
    xo: Int,
    yo: Int,
    x1: Int,
    y1: Int,
    items: MutableList<T>,
    selected: Int,
    renderItem: T.(Screen, Int, Int) -> Unit
) {
    renderItemList(screen, xo, yo, x1, y1, items.toList(), selected, renderItem)
}

inline fun <T> Menu.renderItemList(
    screen: Screen,
    xo: Int,
    yo: Int,
    x1: Int,
    y1: Int,
    items: List<T>,
    selected: Int,
    renderItem: T.(Screen, Int, Int) -> Unit
) {
    var selected = selected
    var renderCursor = true
    if (selected < 0) {
        selected = -selected - 1
        renderCursor = false
    }
    val w = x1 - xo
    val h = y1 - yo - 1
    val i0 = 0
    var i1: Int = items.size
    if (i1 > h) i1 = h
    var io = selected - h / 2
    if (io > items.size - h) io = items.size - h
    if (io < 0) io = 0

    for (i in i0..<i1) {
        items[i + io].renderItem(screen, (1 + xo) * 8, (i + 1 + yo) * 8)
    }

    if (renderCursor) {
        val yy = selected + 1 - io + yo
        Font.draw(
            ">", screen, (xo + 0) * 8, yy * 8, Color.get(5, 555, 555, 555)
        )
        Font.draw(
            "<", screen, (xo + w) * 8, yy * 8, Color.get(5, 555, 555, 555)
        )
    }
}