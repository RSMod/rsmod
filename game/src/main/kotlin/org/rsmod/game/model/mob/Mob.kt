package org.rsmod.game.model.mob

import org.rsmod.game.model.client.Entity
import org.rsmod.game.model.map.Coordinates

public sealed class Mob {

    public abstract val entity: Entity

    public var coords: Coordinates
        get() = entity.coords
        set(value) { entity.coords = value }
}
