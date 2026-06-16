package ru.chernyshoff.server.webflux.dao.controller.mapper

import ru.chernyshoff.server.webflux.dao.controller.model.TraceRequest
import ru.chernyshoff.server.webflux.dao.controller.model.TraceResponse
import ru.chernyshoff.server.webflux.dao.controller.model.type.ServiceTypeDto
import ru.chernyshoff.server.webflux.domain.Trace
import ru.chernyshoff.server.webflux.domain.type.ServiceType
import java.time.OffsetDateTime

fun TraceRequest.toTrace(): Trace = Trace(
    traceId = this.traceId,
    service = ServiceType.valueOf(this.requestService.name)
)

fun Trace.toResponse(): TraceResponse = TraceResponse(
    traceId = this.traceId,
    timestamp = OffsetDateTime.now(),
    responseService = ServiceTypeDto.valueOf(this.service.name)
)