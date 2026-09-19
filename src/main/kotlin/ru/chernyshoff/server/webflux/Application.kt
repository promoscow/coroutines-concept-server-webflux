package ru.chernyshoff.server.webflux

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener
import org.springframework.http.client.ReactorResourceFactory
import reactor.netty.resources.LoopResources

@SpringBootApplication
class Application(
    private val reactorResourceFactory: ReactorResourceFactory
) {

    private val logger = KotlinLogging.logger { this::class.java }

    @PostConstruct
    fun init() {
        val runtime = Runtime.getRuntime()
        logger.info { "Max memory (bytes): ${runtime.maxMemory()}" }
        logger.info { "Processor cores: ${runtime.availableProcessors()}" }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun logEventLoopWorkers() {
        val workers = reactorResourceFactory.loopResources
            .onServer(LoopResources.hasNativeSupport())
            .count()
        logger.info { "Event loop workers accepting requests: $workers" }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
