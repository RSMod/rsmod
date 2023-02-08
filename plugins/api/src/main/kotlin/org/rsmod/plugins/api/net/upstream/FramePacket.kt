package org.rsmod.plugins.api.net.upstream

import org.rsmod.protocol.game.packet.UpstreamDiscardPacket
import org.rsmod.protocol.game.packet.UpstreamPacket

public object EventMouseMove : UpstreamDiscardPacket
public object EventMouseClick : UpstreamDiscardPacket
public object EventMouseIdle : UpstreamDiscardPacket
public object EventAppletFocus : UpstreamDiscardPacket
public object EventKeyboard : UpstreamDiscardPacket
public object EventCameraPosition : UpstreamDiscardPacket
public data class WindowStatus(val mode: Int, val width: Int, val height: Int) : UpstreamPacket
