package com.tiden.flagship.httpapi

import com.fasterxml.jackson.databind.SerializationFeature
import com.tiden.flagship.merchant.registerMerchantRoutes
import io.github.cdimascio.dotenv.dotenv
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.tiden.flagship.payment.routes.registerPaymentRoutes
import com.tiden.flagship.transfer.registerTransferRoutes
import io.ktor.http.*
import org.jetbrains.exposed.sql.Database

fun main() {
    val port = 8080
    val server = embeddedServer(Netty, port, module = Application::mainModule)

    server.start()
}

fun Application.mainModule() {
//    val dotenv = dotenv()
    // TODO: why doesn't this work even if env variables are set?
    val dotenv = dotenv {
        directory = "../"
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Put)
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
    }

    val host : String = dotenv["POSTGRES_HOST_DEV"]
    val port : String = dotenv["POSTGRES_PORT_DEV"]
    val dbName : String = dotenv["POSTGRES_DB_DEV"]
    val dbUser : String = dotenv["POSTGRES_USER_DEV"]
    val dbPassword : String = dotenv["POSTGRES_PASSWORD_DEV"]

    Database.connect("jdbc:postgresql://$host:$port/$dbName", driver = "org.postgresql.Driver",
        user = dbUser, password = dbPassword)


    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    registerPaymentRoutes()
    registerMerchantRoutes()
    registerTransferRoutes()


}