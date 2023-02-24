package org.rsmod.plugins.api.cache.type.varp

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import org.rsmod.plugins.api.cache.type.ConfigType
import java.io.IOException
import javax.inject.Inject

private const val CONFIG_ARCHIVE = 2
private const val VARP_GROUP = 16

public class VarpTypeLoader @Inject constructor(
    @GameCache private val cache: Cache
) {

    public fun load(): List<VarpType> {
        val types = mutableListOf<VarpType>()
        val files = cache.list(CONFIG_ARCHIVE, VARP_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, VARP_GROUP, file.id)
            types += data.readType(file.id)
        }
        return types
    }

    private fun ByteBuf.readType(id: Int): VarpType {
        val builder = VarpTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.readBuffer(instruction, this)
        }
        return builder.build()
    }

    private fun VarpTypeBuilder.readBuffer(instruction: Int, buf: ByteBuf) {
        when (instruction) {
            5 -> clientCode = buf.readUnsignedShort()
            VarpType.TRANSMISSION_OPCODE -> transmit = true
            ConfigType.ALIAS_OPCODE -> alias = buf.readString()
            else -> throw IOException("Error unrecognised varp config code: $instruction")
        }
    }
}
