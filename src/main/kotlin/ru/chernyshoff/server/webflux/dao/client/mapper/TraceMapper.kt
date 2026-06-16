package ru.chernyshoff.server.webflux.dao.client.mapper

import ru.chernyshoff.server.webflux.dao.client.model.TraceRequest
import ru.chernyshoff.server.webflux.dao.client.model.TraceResponse
import ru.chernyshoff.server.webflux.dao.client.model.type.ServiceTypeDto
import ru.chernyshoff.server.webflux.domain.Trace
import ru.chernyshoff.server.webflux.domain.type.ServiceType
import java.time.OffsetDateTime

fun Trace.toRequest(): TraceRequest = TraceRequest(
    traceId = this.traceId,
    timestamp = OffsetDateTime.now(),
    requestService = ServiceTypeDto.valueOf(this.service.name)
)

fun TraceResponse.toTrace(): Trace = Trace(
    traceId = this.traceId,
    service = ServiceType.valueOf(this.responseService.name)
)