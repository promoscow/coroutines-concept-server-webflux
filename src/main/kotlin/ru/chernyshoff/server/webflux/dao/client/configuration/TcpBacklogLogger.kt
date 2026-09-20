package ru.chernyshoff.server.webflux.dao.client.configuration

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.util.NetUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TcpBacklogLogger(
    @Value($$"${app.tcp.backlog}") private val backlog: Int,
    @Value($$"${server.port}") private val port: Int
) {

    private val logger = KotlinLogging.logger {}

    @EventListener(ApplicationReadyEvent::class)
    fun logAcceptCapacity() {
        val soMaxConnections = NetUtil.SOMAXCONN
        logger.info { "TCP accept: port=$port, SO_BACKLOG=$backlog, OS somaxconn=$soMaxConnections" }
        if (soMaxConnections < backlog) {
            logger.warn { "Kernel will truncate the accept queue to $soMaxConnections (< $backlog)" }
        }
    }
}
