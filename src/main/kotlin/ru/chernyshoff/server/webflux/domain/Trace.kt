package ru.chernyshoff.server.webflux.domain

import ru.chernyshoff.server.webflux.domain.type.ServiceType

data class Trace(
    val traceId: String,
    val service: ServiceType
)
