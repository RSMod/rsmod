package org.rsmod.plugins.api.cache.type.param

import org.rsmod.plugins.api.cache.type.ConfigType
import org.rsmod.plugins.api.cache.type.literal.CacheTypeIdentifier

public data class ParamType(
    override val id: Int,
    val name: String?,
    val transmit: Boolean,
    val type: CacheTypeIdentifier?,
    val autoDisable: Boolean,
    val default: Any?
) : ConfigType
