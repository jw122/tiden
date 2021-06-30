package com.tiden.flagship.circle

import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

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

@Serializable
data class CreateCardRequest(
    val idempotencyKey: String,
    val keyId: String,
    val expMonth: Int,
    val expYear: Int,
    val encryptedData: String,
    val billingDetails: Map<String, String>,
    val metadata: Map<String, String>)

@Serializable
data class CreateCardResponseData(
    val id: String,
    val status: String,
    val last4: String,
    val billingDetails: Map<String, String>,
    val expMonth: Int,
    val expYear: Int,
    val network: String,
    val bin: String,
    val issuerCountry: String,
    val fundingType: String,
    val fingerprint: String,
    val verification: Map<String, String>,
    val createDate: String,
    val metadata: Map<String, String>,
    val updateDate: String
)
@Serializable
data class CreateCardResponse(
    val data: CreateCardResponseData,
)

fun createClient() : HttpClient{
    return HttpClient(CIO) {
        install(JsonFeature) {
            serializer =  KotlinxSerializer()
        }
    }
}

// Endpoint to store the information about the card and obtain the sourceId (Circle's internal card id) and encrypted fields
// Encrypted card details (card number and CVV) come from the client
// Some extra fields need to be included in the request to Circle's create card endpoint: billing details for your end user, a unique ID for the active session (sessionId) and the IP address of the end-user (ipAddress).
// Finally, the Circle API will respond with a card containing id value (sourceId) that can be stored on our side to refer to this end-user's card in future payment requests (passed in as sourceId)
suspend fun createCard(createCardRequest: CreateCardRequest): String {
    val client = createClient()
    val response: HttpResponse = client.post("https://api-sandbox.circle.com/v1/cards") {
        val dotenv = dotenv()
        val apiKey : String = dotenv["CIRCLE_API_KEY"]
        headers {
            append(HttpHeaders.Accept, "application/json")
            append(HttpHeaders.Authorization, "Bearer " + apiKey)
        }
        contentType(ContentType.Application.Json)
        body = createCardRequest
        print("card creation request to Circle: " + body + "\n")
    }
    val responseBody: CreateCardResponse = response.receive()
    // extract the card ID from the response + return it
    return responseBody.data.id
}

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
        val dotenv = dotenv {
            directory = "../"
            ignoreIfMalformed = true
            ignoreIfMissing = true
        }
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

fun buildPaymentRequest(idempotencyKey: String, sourceId: String, sourceType: String, ipAddress: String, amount: String, verificationMethod: String, encryptedData: String, pubKeyId: String, description: String, email: String, phoneNumber: String, userSessionId: String): PaymentRequest {
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