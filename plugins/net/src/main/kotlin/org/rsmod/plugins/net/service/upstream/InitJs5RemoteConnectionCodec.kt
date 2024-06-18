package org.rsmod.plugins.net.service.upstream

import io.netty.buffer.ByteBuf
import org.openrs2.crypto.StreamCipher
import org.rsmod.game.protocol.packet.FixedLengthPacketCodec
import com.google.inject.Singleton

@Singleton
public class InitJs5RemoteConnectionCodec : FixedLengthPacketCodec<ServiceRequest.InitJs5RemoteConnection>(
    type = ServiceRequest.InitJs5RemoteConnection::class.java,
    opcode = 15,
    length = 4
) {

    override fun decode(buf: ByteBuf, cipher: StreamCipher): ServiceRequest.InitJs5RemoteConnection {
        val build = buf.readInt()
        return ServiceRequest.InitJs5RemoteConnection(build)
    }

    override fun encode(
        packet: ServiceRequest.InitJs5RemoteConnection,
        buf: ByteBuf,
        cipher: StreamCipher
    ) {
        buf.writeInt(packet.build)
    }
}
