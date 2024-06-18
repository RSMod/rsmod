package org.rsmod.plugins.net.login.downstream

import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec
import com.google.inject.Singleton

@Singleton
public class WorldIsFullCodec : ZeroLengthPacketCodec<LoginResponse.WorldIsFull>(
    packet = LoginResponse.WorldIsFull,
    opcode = 7
)
