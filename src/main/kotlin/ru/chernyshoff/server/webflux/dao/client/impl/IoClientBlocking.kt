package ru.chernyshoff.server.webflux.dao.client.impl

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ru.chernyshoff.server.webflux.dao.client.IoClient
import ru.chernyshoff.server.webflux.domain.Trace
import ru.chernyshoff.server.webflux.domain.type.ServiceType
import java.util.concurrent.atomic.AtomicInteger

@Component
@ConditionalOnProperty(value = ["client.mode"], havingValue = "blocking", matchIfMissing = false)
class IoClientBlocking(meterRegistry: MeterRegistry) : IoClient {

    private val servicePrefix = "server-blocking"

    private val threadsInTrace = AtomicInteger()

    init {
        Gauge
            .builder(METRIC_NAME, threadsInTrace) { it.get().toDouble() }
            .description("Threads currently executing IoClientBlocking#trace")
            .register(meterRegistry)
    }

    override fun trace(trace: Trace): Mono<Trace> {
        threadsInTrace.incrementAndGet()
        try {
            return "${servicePrefix}.${RandomStringUtils.secure().nextAlphanumeric(6)}"
                .let {
                    /** Блокируем worker */
                    Thread.sleep(150)
                    Trace(traceId = "${trace.traceId}-$it", service = ServiceType.IO).toMono()
                }
        } finally {
            threadsInTrace.decrementAndGet()
        }
    }

    private companion object {
        const val METRIC_NAME = "io.client.trace"
    }
}
