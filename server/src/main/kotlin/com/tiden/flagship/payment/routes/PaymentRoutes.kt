package com.tiden.flagship.payment.routes

import com.tiden.flagship.payment.PaymentService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import com.tiden.flagship.payment.models.Payment
import com.tiden.flagship.payment.models.paymentStorage

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

            val payment = PaymentService.getAnyPayment()
            if (payment != null) {
                call.respond(payment)
            } else {
                call.respondText("No payments found", status = HttpStatusCode.NotFound)
            }
        }
        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

            // TODO: remove this comment
            // paymentStorage.find { it.sourceId == id }
            val payment = PaymentService.getPaymentWithSourceId(id) ?: return@get call.respondText(
                "No payment with this id: $id",
                status = HttpStatusCode.NotFound
            )

            call.respond(payment)
        }

        post {
            val payment = call.receive<Payment>()
            val ipAddress = call.request.local.remoteHost

            println("payment request body: " + payment)
            // Prepare a payment request
            // TODO: extract IP address from request, user session ID, encrypt the CVV
            val request = com.tiden.flagship.circle.buildPaymentRequest(
                payment.sourceId,
                payment.sourceType,
                "172.33.222.1",
                payment.amount,
                payment.verificationMethod,
                payment.cvv, // TODO: send encrypted
                "key1", // Unique identifier of the public key used in encryption
                payment.description,
                payment.email,
                payment.phoneNumber,
                "xxx")
            val paymentResponse = com.tiden.flagship.circle.makePayment(request)

            // TODO: update this to no longer use in-memory array for storage once we have DB
            paymentStorage.add(payment)

            // inserting a payment to the database
            PaymentService.addPayment(payment)

            call.respondText("Payment stored correctly: " + paymentResponse, status = HttpStatusCode.Created)
        }
    }

    route("/stablecoins") {
        // calls the com.tiden.flagship.circle API for available stablecoins
        get {
            val response = com.tiden.flagship.circle.getStablecoins()
            call.respondText(response)
        }
    }
}