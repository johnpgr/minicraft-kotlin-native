package entity

import item.*
import item.ResourceItem
import item.resource.Resource

data class Inventory(val items: MutableList<Item> = ArrayList())

fun Inventory.add(item: Item) {
    add(items.size, item)
}

fun Inventory.add(slot: Int, item: Item) {
    if (item is ResourceItem) {
        val has = findResource(item.resource)
        if (has == null) {
            items.add(slot, item)
        } else {
            has.count += item.count
        }
    } else {
        items.add(slot, item)
    }
}

private fun Inventory.findResource(resource: Resource): ResourceItem? {
    for (i in 0..<items.size) {
        if (items[i] is ResourceItem) {
            val has: ResourceItem = items[i] as ResourceItem
            if (has.resource === resource) return has
        }
    }
    return null
}

fun Inventory.hasResources(r: Resource, count: Int): Boolean {
    val ri: ResourceItem? = findResource(r)
    if (ri == null) return false
    return ri.count >= count
}

fun Inventory.removeResource(r: Resource, count: Int): Boolean {
    val ri = findResource(r)
    if (ri == null) return false
    if (ri.count < count) return false
    ri.count -= count
    if (ri.count <= 0) items.remove(ri)
    return true
}

fun Inventory.count(item: Item): Int {
    return if (item is ResourceItem) {
        val ri = findResource(item.resource)
        ri?.count ?: 0
    } else {
        items.count { item2 -> item.matches(item2) }
    }
}