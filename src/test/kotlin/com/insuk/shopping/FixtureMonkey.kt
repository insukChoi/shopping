package com.insuk.shopping

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.jackson.plugin.JacksonPlugin
import com.navercorp.fixturemonkey.kotest.KotestPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin

val fixtureMonkey: FixtureMonkey by lazy {
    FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .plugin(
            JacksonPlugin(
                ObjectMapper()
                    .registerKotlinModule()
                    .registerModule(JavaTimeModule())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false),
            ),
        )
        .plugin(KotestPlugin())
        .build()
}
