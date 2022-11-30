package org.rsmod.game

import com.google.common.util.concurrent.Service
import com.google.common.util.concurrent.ServiceManager
import com.google.inject.Inject

public class GameBootstrap @Inject constructor(services: Set<Service>) {

    private val serviceManager = ServiceManager(services)

    public fun startUp() {
        serviceManager.startAsync()
        try {
            serviceManager.awaitHealthy()
        } catch (t: Throwable) {
            serviceManager.stopAsync().awaitStopped()
            throw t
        }
        val shutdown = Thread(::shutdown, "ShutdownHook")
        Runtime.getRuntime().addShutdownHook(shutdown)
        serviceManager.awaitStopped()
        Runtime.getRuntime().removeShutdownHook(shutdown)
    }

    private fun shutdown() {
        serviceManager.stopAsync().awaitStopped()
    }
}
