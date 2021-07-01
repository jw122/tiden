package com.tiden.flagship.payment.routes

import com.tiden.flagship.payment.PaymentService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import com.tiden.flagship.payment.models.Payment
import com.tiden.flagship.payment.models.paymentStorage
import io.ktor.features.*

fun Application.registerPaymentRoutes() {
    install(CallLogging)
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
            // Client makes a call to circle's /public endpoint to get a public key and encrypt card credentials (card num and CVV)
            // The public key also comes with a keyId
            // The encrypted CVV should be received here in the `encryptedData` field as a string
            // TODO: client would pass in this keyId as a param after fetching public key. Once that's done, this code should extract from request params
            val keyId = "key1"
            // TODO: extract actual IP address
            val ipAddress = "172.33.222.1"
            // TODO: set up a sessionId (in client?)
            val sessionId = "xxx"

            call.application.environment.log.info("Preparing a payment request: " + payment)
            val idempotencyKey = java.util.UUID.randomUUID().toString()

            // Step 1: Create a card entity on Circle (or get existing one)
            val cardRequest = com.tiden.flagship.circle.CreateCardRequest(
                idempotencyKey,
                keyId,
                payment.expirationMonth,
                payment.expirationYear,
                payment.cvv,
                mapOf(
                    "line1" to payment.address,
                    "city" to payment.city,
                    "district" to payment.district,
                    "postalCode" to payment.postalCode,
                    "country" to payment.country,
                    "name" to payment.name),
                mapOf(
                    "phoneNumber" to payment.phoneNumber,
                    "email" to payment.email,
                    "sessionId" to sessionId,
                    "ipAddress" to ipAddress
                )
            )

            val cardId = com.tiden.flagship.circle.createCard(cardRequest)
            call.application.environment.log.info("Card successfully created: " + cardId)

            // Step 2: Make the payment using sourceId given from card step
            // The cardId will be passed into the payment request as sourceId
            val request = com.tiden.flagship.circle.buildPaymentRequest(
                idempotencyKey,
                cardId,
                payment.sourceType,
                ipAddress,
                payment.amount,
                payment.verificationMethod,
                payment.cvv,
                keyId,
                payment.description,
                payment.email,
                payment.phoneNumber,
                sessionId)
            val paymentResponse = com.tiden.flagship.circle.makePayment(request)

            // inserting a payment to the database
            // T0DO: if circle responds in an error, still store the payment attempt
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