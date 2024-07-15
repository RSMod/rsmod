package org.rsmod.plugins.net.login.downstream

import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec
import jakarta.inject.Singleton

@Singleton
public class ClientProtocolOutOfDateCodec : ZeroLengthPacketCodec<LoginResponse.ClientProtocolOutOfDate>(
    packet = LoginResponse.ClientProtocolOutOfDate,
    opcode = 68
)
