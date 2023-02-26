package org.rsmod.plugins.api.cache.type.enums.literal

import org.rsmod.game.model.map.Coordinates

public object EnumTypeCoordinate : EnumTypeBaseInt<Coordinates> {

    override fun decode(value: Int): Coordinates {
        return Coordinates(value)
    }

    override fun encode(value: Coordinates): Int {
        return value.packed
    }
}
