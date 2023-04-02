package org.rsmod.plugins.cache.config.npc

import org.rsmod.plugins.types.NamedNpc

public class NpcTypeList(private val elements: Map<Int, NpcType>) : Map<Int, NpcType> by elements {

    public operator fun get(named: NamedNpc): NpcType {
        return elements.getValue(named.id)
    }
}
