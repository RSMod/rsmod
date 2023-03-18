package org.rsmod.plugins.api.prot.desktop

import org.openrs2.buffer.BitBuf
import org.openrs2.buffer.writeByteC
import org.openrs2.buffer.writeIntAlt3
import org.openrs2.buffer.writeShortA
import org.openrs2.buffer.writeShortLEA
import org.openrs2.buffer.writeShortSmart
import org.openrs2.buffer.writeString
import org.rsmod.plugins.api.net.downstream.IfOpenSub
import org.rsmod.plugins.api.net.downstream.IfOpenTop
import org.rsmod.plugins.api.net.downstream.MessageGame
import org.rsmod.plugins.api.net.downstream.MinimapFlagSet
import org.rsmod.plugins.api.net.downstream.PlayerInfoPacket
import org.rsmod.plugins.api.net.downstream.RebuildNormal
import org.rsmod.plugins.api.net.downstream.RunClientScript
import org.rsmod.plugins.api.net.downstream.VarpLarge
import org.rsmod.plugins.api.net.downstream.VarpSmall
import org.rsmod.plugins.api.net.platform.GamePlatformPacketMaps

private val platforms: GamePlatformPacketMaps by inject()
private val packets = platforms.desktopDownstream

packets.register<MinimapFlagSet> {
    opcode = 80
    length = 2
    encode { packet, buf ->
        buf.writeByte(packet.lx)
        buf.writeByte(packet.lz)
    }
}

packets.register<MessageGame> {
    opcode = 102
    length = variableByteLength
    encode { packet, buf ->
        buf.writeShortSmart(packet.type)
        buf.writeBoolean(packet.username != null)
        packet.username?.let { buf.writeString(it) }
        buf.writeString(packet.text)
    }
}

packets.register<RunClientScript> {
    opcode = 94
    length = variableShortLength
    encode { packet, buf ->
        val typeChars = String(packet.args.map { if (it is String) 's' else 'i' }.toCharArray())
        buf.writeString(typeChars)
        packet.args.reversed().forEach {
            if (it is String) {
                buf.writeString(it)
            } else if (it is Int) {
                buf.writeInt(it)
            }
        }
        buf.writeInt(packet.id)
    }
}

packets.register<IfOpenTop> {
    opcode = 46
    length = 2
    encode { packet, buf ->
        buf.writeShort(packet.interfaceId)
    }
}

packets.register<IfOpenSub> {
    opcode = 61
    length = 7
    encode { packet, buf ->
        buf.writeByteC(packet.clickMode)
        buf.writeInt(packet.targetComponent)
        buf.writeShortLE(packet.interfaceId)
    }
}

packets.register<RebuildNormal> {
    opcode = 0
    length = variableShortLength
    encode { packet, buf ->
        /* log-in sends gpi initialization data along with REBUILD_NORMAL data */
        packet.gpiInitialization?.let { gpi ->
            BitBuf(buf).use { bitBuf -> gpi.encode(bitBuf) }
        }
        buf.writeShortLEA(packet.zone.x)
        buf.writeShortA(packet.zone.z)
        buf.writeShort(packet.xteaList.size / 4)
        packet.xteaList.forEach { key ->
            buf.writeInt(key)
        }
    }
}

packets.register<PlayerInfoPacket> {
    opcode = 36
    length = variableShortLength
    encode { packet, buf ->
        buf.writeBytes(packet.data, 0, packet.length)
    }
}

packets.register<VarpSmall> {
    opcode = 95
    length = 3
    encode { packet, buf ->
        buf.writeShortA(packet.varp)
        buf.writeByte(packet.packed)
    }
}

packets.register<VarpLarge> {
    opcode = 67
    length = 6
    encode { packet, buf ->
        buf.writeShortLEA(packet.varp)
        buf.writeIntAlt3(packet.packed)
    }
}
