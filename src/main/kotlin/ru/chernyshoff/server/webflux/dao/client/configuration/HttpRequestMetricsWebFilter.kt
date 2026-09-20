package ru.chernyshoff.server.webflux.dao.client.configuration

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import java.util.concurrent.atomic.AtomicInteger

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class HttpRequestMetricsWebFilter(meterRegistry: MeterRegistry) : WebFilter {

    private val inFlight = AtomicInteger()

    private val accepted: Counter = Counter
        .builder(ACCEPTED_METRIC)
        .description("HTTP requests accepted by the application")
        .register(meterRegistry)

    private val completedCounters: Map<String, Counter> = STATUS_GROUPS.associateWith { group ->
        Counter
            .builder(COMPLETED_METRIC)
            .description("HTTP requests completed by status group")
            .tag(STATUS_GROUP_TAG, group)
            .register(meterRegistry)
    }

    init {
        Gauge
            .builder(ACTIVE_METRIC, inFlight) { it.get().toDouble() }
            .description("HTTP requests currently being processed")
            .register(meterRegistry)
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (isActuator(exchange)) {
            return chain.filter(exchange)
        }
        accepted.increment()
        return chain.filter(exchange)
            .doOnSubscribe { inFlight.incrementAndGet() }
            .doFinally { signal ->
                inFlight.decrementAndGet()
                completedCounters.getValue(calculateStatusGroup(exchange, signal)).increment()
            }
    }

    private fun isActuator(exchange: ServerWebExchange): Boolean =
        exchange.request.path.pathWithinApplication().value().startsWith(ACTUATOR_PREFIX)

    private fun calculateStatusGroup(exchange: ServerWebExchange, signal: SignalType): String {
        if (signal == SignalType.CANCEL) {
            return GROUP_OTHER
        }
        val status = exchange.response.statusCode
        return when {
            status == null && signal == SignalType.ON_ERROR -> GROUP_5XX
            status == null -> GROUP_2XX
            status.is2xxSuccessful -> GROUP_2XX
            status.is4xxClientError -> GROUP_4XX
            status.is5xxServerError -> GROUP_5XX
            else -> GROUP_OTHER
        }
    }

    private companion object {
        const val ACTIVE_METRIC = "app.http.server.requests.active"
        const val ACCEPTED_METRIC = "app.http.server.requests.accepted"
        const val COMPLETED_METRIC = "app.http.server.requests.completed"
        const val STATUS_GROUP_TAG = "status_group"
        const val ACTUATOR_PREFIX = "/actuator"
        const val GROUP_2XX = "2xx"
        const val GROUP_4XX = "4xx"
        const val GROUP_5XX = "5xx"
        const val GROUP_OTHER = "other"
        val STATUS_GROUPS = listOf(GROUP_2XX, GROUP_4XX, GROUP_5XX, GROUP_OTHER)
    }
}
