package com.tiden.flagship.circle

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import io.github.cdimascio.dotenv.dotenv

@Serializable
data class PaymentMetadata(val ipAddress: String, val email: String, val phoneNumber: String, val sessionId: String)

@Serializable
data class Source(val id: String, val type: String)

@Serializable
data class Amount(val amount: String, val currency: String)

@Serializable
data class PaymentRequest(val idempotencyKey: String, val keyId: String, val description: String, val verification: String, val encryptedData: String,
val metadata: Map<String, String>,
val amount: Map<String, String>,
val source: Map<String, String>)

suspend fun getStablecoins(): String {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://api-sandbox.circle.com/v1/stablecoins") {
        headers {
            append(HttpHeaders.Accept, "application/json")
        }
    }
    println("got response: " + response.readText());
    client.close()
    return response.readText()
}

suspend fun makePayment(paymentRequest: PaymentRequest): String {
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer =  KotlinxSerializer()
        }
    }
    val response: HttpResponse = client.post("https://api-sandbox.circle.com/v1/payments") {
        val dotenv = dotenv()
        val apiKey : String = dotenv["CIRCLE_API_KEY"]
        headers {
            append(HttpHeaders.Accept, "application/json")
            append(HttpHeaders.Authorization, "Bearer " + apiKey)
        }
        contentType(ContentType.Application.Json)
        body = paymentRequest
        print("payment request to Circle: " + body + "\n")
    }
    val responseBody: String = response.receive()
    println("got response from payment: " + responseBody)
    return responseBody
}

fun buildPaymentRequest(sourceId: String, sourceType: String, ipAddress: String, amount: String, verificationMethod: String, encryptedData: String, pubKeyId: String, description: String, email: String, phoneNumber: String, userSessionId: String): PaymentRequest {
    val idempotencyKey = java.util.UUID.randomUUID().toString()
    println("building payment request for id " + idempotencyKey)
    return PaymentRequest(
            idempotencyKey, // for ensuring exactly-once execution of mutating requests
            pubKeyId, // unique identifier of pub key used in encryption
            description,
            verificationMethod,
            encryptedData,
            mapOf("ipAddress" to ipAddress, "email" to email, "phoneNumber" to phoneNumber, "sessionId" to userSessionId),
            mapOf("amount" to amount, "currency" to "USD"),
            mapOf("id" to sourceId, "type" to sourceType)
    )
}