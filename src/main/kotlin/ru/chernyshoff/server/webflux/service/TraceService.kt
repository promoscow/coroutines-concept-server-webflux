package ru.chernyshoff.server.webflux.service

import reactor.core.publisher.Mono
import ru.chernyshoff.server.webflux.domain.Trace

interface IoService {

    fun trace(trace: Trace): Mono<Trace>
}