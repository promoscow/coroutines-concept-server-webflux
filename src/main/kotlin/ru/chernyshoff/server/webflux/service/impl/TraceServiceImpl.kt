package ru.chernyshoff.server.webflux.service.impl

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.chernyshoff.server.webflux.dao.client.IoClient
import ru.chernyshoff.server.webflux.domain.Trace
import ru.chernyshoff.server.webflux.domain.type.ServiceType
import ru.chernyshoff.server.webflux.service.IoService

@Service
class IoServiceImpl(
    private val client: IoClient,
    @Value($$"${app.service-prefix}") private val servicePrefix: String
) : IoService {

    override fun trace(trace: Trace): Mono<Trace> =
        "${servicePrefix}.${RandomStringUtils.secure().nextAlphanumeric(6)}"
            .let { newTraceId ->
                val newTrace = Trace(
                    traceId = "${trace.traceId}-$newTraceId",
                    service = ServiceType.SERVER
                )
                client
                    .trace(newTrace)
                    .map {
                        Trace(
                            traceId = it.traceId,
                            service = ServiceType.SERVER
                        )
                    }

            }
}