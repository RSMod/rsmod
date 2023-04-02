package org.rsmod.plugins.cache.config.item

import org.rsmod.plugins.types.NamedItem

public class ItemTypeList(private val elements: Map<Int, ItemType>) : Map<Int, ItemType> by elements {

    public operator fun get(named: NamedItem): ItemType {
        return elements.getValue(named.id)
    }
}
