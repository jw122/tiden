package routes

import circle.getStablecoins
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import models.Payment
import models.paymentStorage

fun Application.registerPaymentRoutes() {
    routing {
        paymentRouting()
    }
}

fun Route.paymentRouting() {
    route("/") {
        get {
            call.respondText("Hello, friend!")
        }
    }
    // Group everything that falls under the /payment endpoint
    route("/payment") {
        get {
            if (paymentStorage.isNotEmpty()) {
                call.respond(paymentStorage)
            } else {
                call.respondText("No payments found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

            val payment = paymentStorage.find { it.id == id } ?: return@get call.respondText(
                "No payment with this id: $id",
                status = HttpStatusCode.NotFound
            )

            call.respond(payment)
        }
        post {
            val payment = call.receive<Payment>()
            paymentStorage.add(payment)
            call.respondText("Payment stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (paymentStorage.removeIf { it.id == id }) {
                call.respondText("Payment removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }

    route("/stablecoins") {
        // calls the circle API for available stablecoins
        get {
            val response = circle.getStablecoins()
            call.respondText(response)
        }
    }
}