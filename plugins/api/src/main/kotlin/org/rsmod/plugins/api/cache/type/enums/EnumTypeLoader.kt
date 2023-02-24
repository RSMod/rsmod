package org.rsmod.plugins.api.cache.type.enums

import io.netty.buffer.ByteBuf
import org.openrs2.buffer.readString
import org.openrs2.cache.Cache
import org.rsmod.plugins.api.cache.build.game.GameCache
import java.io.IOException
import javax.inject.Inject

private const val CONFIG_ARCHIVE = 2
private const val ENUM_GROUP = 8

public class EnumTypeLoader @Inject constructor(
    @GameCache private val cache: Cache
) {

    public fun load(): List<EnumType<Any, Any>> {
        val types = mutableListOf<EnumType<Any, Any>>()
        val files = cache.list(CONFIG_ARCHIVE, ENUM_GROUP)
        files.forEach { file ->
            val data = cache.read(CONFIG_ARCHIVE, ENUM_GROUP, file.id)
            types += data.readType(file.id)
        }
        return types
    }

    private fun ByteBuf.readType(id: Int): EnumType<Any, Any> {
        val builder = EnumTypeBuilder().apply { this.id = id }
        while (isReadable) {
            val instruction = readUnsignedByte().toInt()
            if (instruction == 0) {
                break
            }
            builder.decodeType(this, instruction)
        }
        return builder.build()
    }

    private fun EnumTypeBuilder.decodeType(buf: ByteBuf, instruction: Int) {
        when (instruction) {
            1 -> keyType = buf.readByte().toInt().toChar()
            2 -> valType = buf.readByte().toInt().toChar()
            3 -> defaultStr = buf.readString()
            4 -> defaultInt = buf.readInt()
            5 -> {
                check(size == 0)
                size = buf.readUnsignedShort()
                repeat(size) {
                    val key = buf.readInt()
                    val value = buf.readString()
                    strValues[key] = value
                }
            }
            6 -> {
                check(size == 0)
                size = buf.readUnsignedShort()
                repeat(size) {
                    val key = buf.readInt()
                    val value = buf.readInt()
                    intValues[key] = value
                }
            }
            else -> throw IOException("Error unrecognised enum config code: $instruction")
        }
    }
}
