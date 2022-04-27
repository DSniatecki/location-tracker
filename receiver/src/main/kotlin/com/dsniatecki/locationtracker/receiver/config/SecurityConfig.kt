package com.dsniatecki.locationtracker.receiver.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http.csrf()
            .disable()
            .authorizeExchange()
            .pathMatchers("/actuator/**", "/api/internal/**")
            .permitAll()
            .anyExchange()
            .authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt()
            .and()
            .and()
            .build()
}