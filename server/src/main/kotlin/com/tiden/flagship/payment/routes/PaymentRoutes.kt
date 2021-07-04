package com.tiden.flagship.payment.routes

import com.tiden.flagship.circle.*
import com.tiden.flagship.payment.PaymentService
import com.tiden.flagship.payment.models.PaymentRequest
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

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

    route("/api/hello") {
        get {
            call.respond(mapOf("message" to "hello from Kotlin server"))
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

            val payment = PaymentService.getPaymentWithSourceId(id) ?: return@get call.respondText(
                "No payment with this id: $id",
                status = HttpStatusCode.NotFound
            )

            call.respond(payment)
        }
        post {
            val paymentRequest = call.receive<PaymentRequest>()
            // Unique identifier of pub key used in encryption. Not currently persisted
            val keyId = paymentRequest.keyId.toString();
            // TODO: extract actual IP address
            val ipAddress = "172.33.222.1"
            // TODO: set up a sessionId (in client?)
            val sessionId = "xxx"

            call.application.environment.log.info("Preparing a payment request: $paymentRequest")
            val idempotencyKey = java.util.UUID.randomUUID().toString()

            // Step 1: Create a card entity on Circle (or get existing one)
            val cardRequest: CircleCreateCardRequest = CircleCreateCardRequest(
                idempotencyKey,
                keyId,
                paymentRequest.expirationMonth,
                paymentRequest.expirationYear,
                paymentRequest.cvv,
                BillingDetails(
                    paymentRequest.name,
                    paymentRequest.city,
                    paymentRequest.country,
                    paymentRequest.address,
                    paymentRequest.district,
                    paymentRequest.postalCode
                ),
                PaymentMetadata(ipAddress, paymentRequest.email, paymentRequest.phoneNumber, ipAddress)

            )

            val cardId = com.tiden.flagship.circle.createCard(cardRequest)
            call.application.environment.log.info("Card successfully created: $cardId")

            // Step 2: Make the payment using sourceId given from card step
            // The cardId will be passed into the payment request as sourceId
            val circlePaymentRequest: CirclePaymentRequest = buildPaymentRequest(
                idempotencyKey,
                cardId,
                paymentRequest.sourceType,
                ipAddress,
                paymentRequest.amount,
                paymentRequest.verificationMethod,
                paymentRequest.cvv,
                keyId,
                paymentRequest.description,
                paymentRequest.email,
                paymentRequest.phoneNumber,
                sessionId
            )

            val circlePaymentResponse: CirclePaymentResponse? = makePayment(circlePaymentRequest)

            if (circlePaymentResponse != null) {
                PaymentService.storeSuccessfulPayment(cardRequest, paymentRequest, circlePaymentResponse.data)
                call.respondText(
                    "Payment stored correctly: " + circlePaymentResponse.data.id,
                    status = HttpStatusCode.Created
                )
            } else {
                PaymentService.storeFailedPayment(cardRequest, paymentRequest)
                call.respondText(
                    "unable to process payment: " + circlePaymentRequest.metadata,
                    status = HttpStatusCode.InternalServerError
                )
            }

        }
    }

    route("/stablecoins") {
        // calls the com.tiden.flagship.circle API for available stablecoins
        get {
            val response = com.tiden.flagship.circle.getStablecoins()
            call.respondText(response)
        }
    }

    route("/encryption") {
        get {
            val response = com.tiden.flagship.circle.getPublicKey();
            call.respondText(response)
        }
    }
}