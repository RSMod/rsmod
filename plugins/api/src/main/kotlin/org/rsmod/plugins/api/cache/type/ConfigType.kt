package org.rsmod.plugins.api.cache.type

public interface ConfigType {

    public val id: Int

    public companion object {

        public const val TRANSMISSION_OPCODE: Int = 230
        public const val INTERNAL_NAME_OPCODE: Int = 240
    }
}
