@file:Suppress("DEPRECATION")

package org.rsmod.game.type.inv

import org.rsmod.game.type.obj.ObjType

public data class InvStock
@Deprecated("Use the `ObjType` constructor instead for type-safety consistency.")
constructor(public val obj: Int, public val count: Int, public val restockTicks: Int) {
    public constructor(
        type: ObjType,
        count: Int = 1,
        restockTicks: Int = 0,
    ) : this(type.id, count, restockTicks)
}