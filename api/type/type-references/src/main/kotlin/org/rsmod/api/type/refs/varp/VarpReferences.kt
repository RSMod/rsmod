package org.rsmod.api.type.refs.varp

import org.rsmod.api.type.refs.HashTypeReferences
import org.rsmod.game.type.varp.HashedVarpType
import org.rsmod.game.type.varp.VarpType

public abstract class VarpReferences : HashTypeReferences<VarpType>(VarpType::class.java) {
    override fun find(hash: Long): VarpType {
        val type = HashedVarpType(hash)
        cache += type
        return type
    }
}