package org.rsmod.plugins.net.login.downstream

import org.rsmod.game.protocol.packet.ZeroLengthPacketCodec
import com.google.inject.Singleton

@Singleton
public class ErrorConnectingCodec : ZeroLengthPacketCodec<LoginResponse.ErrorConnecting>(
    packet = LoginResponse.ErrorConnecting,
    opcode = -2
)
