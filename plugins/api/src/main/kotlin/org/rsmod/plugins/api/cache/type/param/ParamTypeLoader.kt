package org.rsmod.plugins.api.cache.type.param

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.type.ConfigType
import java.io.IOException
import javax.inject.Inject

public class ParamTypeLoader @Inject constructor(
    @GameCache private val cache: Cache
) {

    public fun load(): List<ParamType> {
        val types = mutableListOf<ParamType>()
        val files = cache.list(CONFIG_ARCHIVE, PARAM_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, PARAM_GROUP, file.id)
            types += data.readType(file.id)
        }
        return types
    }

    private fun ByteBuf.readType(id: Int): ParamType {
        val builder = ParamTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
    }

    private fun ParamTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
        when (instruction) {
            1 -> typeChar = buf.readByte().toInt().toChar()
            2 -> defaultInt = buf.readInt()
            4 -> autoDisable = false
            5 -> defaultStr = buf.readString()
            ConfigType.TRANSMISSION_OPCODE -> transmit = true
            ConfigType.INTERNAL_NAME_OPCODE -> name = buf.readString()
            else -> throw IOException("Error unrecognised param config code: $instruction")
        }
    }

    private companion object {

        private const val CONFIG_ARCHIVE = 2
        private const val PARAM_GROUP = 11
    }
}
