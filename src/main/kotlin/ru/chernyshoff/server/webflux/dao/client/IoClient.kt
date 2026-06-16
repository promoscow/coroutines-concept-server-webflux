package ru.chernyshoff.server.webflux.dao.client

import reactor.core.publisher.Mono
import ru.chernyshoff.server.webflux.domain.Trace

interface IoClient {

    fun trace(trace: Trace): Mono<Trace>
}