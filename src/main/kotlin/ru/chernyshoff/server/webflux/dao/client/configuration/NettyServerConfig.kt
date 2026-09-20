package ru.chernyshoff.server.webflux.dao.client.configuration

import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.reactor.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NettyServerConfig(
    @Value($$"${app.tcp.backlog}") private val backlog: Int
) {

    @Bean
    fun backlogCustomizer() = WebServerFactoryCustomizer<NettyReactiveWebServerFactory> { factory ->
        factory.addServerCustomizers(
            { server -> server.option(ChannelOption.SO_BACKLOG, backlog) }
        )
    }
}
