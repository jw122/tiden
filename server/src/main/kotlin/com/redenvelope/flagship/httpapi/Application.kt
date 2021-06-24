package com.redenvelope.flagship.httpapi

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import routes.registerPaymentRoutes


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
//    allows server to examine Accept header to see if it can serve this specific type of content
    install(ContentNegotiation) {
//        json support is powered by kotlinx.serialization
        json()
    }
    registerPaymentRoutes()
}
