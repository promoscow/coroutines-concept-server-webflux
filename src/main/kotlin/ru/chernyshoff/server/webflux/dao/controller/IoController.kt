package ru.chernyshoff.server.webflux.dao.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import ru.chernyshoff.server.webflux.dao.controller.mapper.toResponse
import ru.chernyshoff.server.webflux.dao.controller.mapper.toTrace
import ru.chernyshoff.server.webflux.dao.controller.model.TraceRequest
import ru.chernyshoff.server.webflux.dao.controller.model.TraceResponse
import ru.chernyshoff.server.webflux.service.IoService

@RestController
@RequestMapping("/server/io")
class IoController(
    private val service: IoService
) {

    @PostMapping("/trace")
    fun trace(@RequestBody request: TraceRequest): Mono<TraceResponse> =
        request
            .toTrace()
            .let { service.trace(it) }
            .map { it.toResponse() }
}