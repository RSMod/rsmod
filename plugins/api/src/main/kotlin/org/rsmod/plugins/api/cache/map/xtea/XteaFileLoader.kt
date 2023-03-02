package org.rsmod.plugins.api.cache.map.xtea

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.logging.InlineLogger
import org.openrs2.crypto.XteaKey
import org.rsmod.game.config.GameConfig
import org.rsmod.game.events.GameEventBus
import org.rsmod.game.events.publish
import org.rsmod.json.Json
import org.rsmod.plugins.api.cachePath
import java.nio.file.Files
import javax.inject.Inject

private val logger = InlineLogger()

private const val FILE_NAME = "xteas.json"

public class XteaFileLoader @Inject constructor(
    @Json private val mapper: ObjectMapper,
    private val config: GameConfig,
    private val repository: XteaRepository,
    private val eventBus: GameEventBus
) {

    public fun load() {
        /* xtea file should be one path level above packed cache */
        val file = config.cachePath.resolve(FILE_NAME)
        Files.newBufferedReader(file).use {
            val fileXtea = mapper.readValue(it, Array<XteaFile>::class.java)
            fileXtea.forEach { xtea ->
                repository[xtea.mapSquare] = XteaKey.fromIntArray(xtea.key)
            }
            logger.info { "Loaded ${fileXtea.size} map XTEA keys." }
            eventBus.publish(XteaEvent.Loaded)
        }
    }
}
