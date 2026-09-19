package ru.chernyshoff.server.webflux.dao.client.filter

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Component
class PostAwaitingThreadsFilter(meterRegistry: MeterRegistry) : ExchangeFilterFunction {

    private val awaitingThreads = ConcurrentHashMap<Long, Int>()

    init {
        Gauge
            .builder(METRIC_NAME, awaitingThreads) { it.size.toDouble() }
            .description("Threads that sent a request to the io service and are awaiting a response")
            .register(meterRegistry)
    }

    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> {
        if (request.method() != HttpMethod.POST) {
            return next.exchange(request)
        }
        return Mono.defer {
            val threadId = Thread.currentThread().threadId()
            next
                .exchange(request)
                .doOnSubscribe { registerAwaiting(threadId) }
                .doFinally { unregisterAwaiting(threadId) }
        }
    }

    private fun registerAwaiting(threadId: Long) {
        awaitingThreads.merge(threadId, 1) { current, _ -> current + 1 }
    }

    private fun unregisterAwaiting(threadId: Long) {
        awaitingThreads.computeIfPresent(threadId) { _, current -> (current - 1).takeIf { it > 0 } }
    }

    private companion object {
        const val METRIC_NAME = "app.io.client.http.requests.active"
    }
}
