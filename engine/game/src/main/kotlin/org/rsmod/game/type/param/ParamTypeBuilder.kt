package org.rsmod.game.type.param

import kotlin.reflect.KClass
import org.rsmod.game.type.literal.CacheVarLiteral
import org.rsmod.game.type.util.GenericPropertySelector.select

@DslMarker private annotation class ParamBuilderDsl

@ParamBuilderDsl
public class ParamTypeBuilder<T : Any>(
    public val type: KClass<T>?,
    public var internal: String? = null,
) {
    public var typeCharId: Char? = null
    public var defaultInt: Int? = null
    public var defaultStr: String? = null
    public var autoDisable: Boolean? = null
    public var typedDefault: T? = null

    public fun build(id: Int): UnpackedParamType<T> {
        val internal = checkNotNull(internal) { "`internal` must be set." }
        val typeLiteral = CacheVarLiteral.forCharId(typeCharId)
        val autoDisable = autoDisable ?: DEFAULT_AUTO_DISABLE
        return UnpackedParamType(
            type = type,
            typeLiteral = typeLiteral,
            defaultInt = defaultInt,
            defaultStr = defaultStr,
            autoDisable = autoDisable,
            typedDefault = typedDefault,
            internalId = id,
            internalName = internal,
        )
    }

    public companion object {
        public const val DEFAULT_AUTO_DISABLE: Boolean = true

        public fun merge(
            edit: UnpackedParamType<*>,
            base: UnpackedParamType<*>,
        ): UnpackedParamType<*> {
            val type = select(edit, base, default = null) { type }
            val typeLiteral = select(edit, base, default = null) { typeLiteral }
            val defaultInt = select(edit, base, default = null) { defaultInt }
            val defaultStr = select(edit, base, default = null) { defaultStr }
            val autoDisable = select(edit, base, default = false) { autoDisable }
            val internalId = select(edit, base, default = null) { internalId }
            val internalName = select(edit, base, default = null) { internalName }
            return UnpackedParamType(
                type = type,
                typeLiteral = typeLiteral,
                defaultInt = defaultInt,
                defaultStr = defaultStr,
                autoDisable = autoDisable,
                typedDefault = null,
                internalId = internalId ?: -1,
                internalName = internalName ?: "",
            )
        }
    }
}