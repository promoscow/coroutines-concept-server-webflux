package ru.chernyshoff.server.webflux.dao.client.model

import ru.chernyshoff.server.webflux.dao.client.model.type.ServiceTypeDto
import java.time.OffsetDateTime

data class TraceRequest(
    val traceId: String,
    val timestamp: OffsetDateTime,
    val requestService: ServiceTypeDto
)