package org.rsmod.plugins.net.service

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.timeout.IdleStateHandler
import org.rsmod.game.protocol.Protocol
import org.rsmod.game.protocol.ProtocolDecoder
import org.rsmod.game.protocol.ProtocolEncoder
import org.rsmod.plugins.net.service.downstream.ServiceDownstream
import org.rsmod.plugins.net.service.upstream.ServiceUpstream
import java.util.concurrent.TimeUnit
import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton

private const val TIMEOUT_SECS = 30L

@Singleton
public class ServiceChannelInitializer @Inject constructor(
    private val handlerProvider: Provider<ServiceChannelHandler>,
    @ServiceUpstream private val serviceUpstream: Protocol,
    @ServiceDownstream private val serviceDownstream: Protocol
) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(
            IdleStateHandler(true, TIMEOUT_SECS, TIMEOUT_SECS, TIMEOUT_SECS, TimeUnit.SECONDS),
            ProtocolDecoder(serviceUpstream),
            ProtocolEncoder(serviceDownstream),
            handlerProvider.get()
        )
    }
}
