package ru.chernyshoff.server.webflux.dao.client.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import ru.chernyshoff.server.webflux.dao.client.IoClient
import ru.chernyshoff.server.webflux.dao.client.mapper.toRequest
import ru.chernyshoff.server.webflux.dao.client.mapper.toTrace
import ru.chernyshoff.server.webflux.dao.client.model.TraceResponse
import ru.chernyshoff.server.webflux.domain.Trace

@Component
@ConditionalOnProperty(value = ["client.mode"], havingValue = "reactive", matchIfMissing = true)
class IoClientReactive(
    private val client: WebClient,
    @Value($$"${app.io.host}") private val ioHost: String
) : IoClient {

    override fun trace(trace: Trace): Mono<Trace> =
        client
            .post()
            .uri("$ioHost/api/io/trace")
            .bodyValue(trace.toRequest())
            .retrieve()
            .bodyToMono(TraceResponse::class.java)
            .map { it.toTrace() }
}