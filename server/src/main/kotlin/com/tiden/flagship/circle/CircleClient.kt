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
data class PaymentMetadata(
    val ipAddress: String?,
    val email: String,
    val phoneNumber: String?,
    val sessionId: String?
)

@Serializable
data class Source(val id: String, val type: String)

@Serializable
data class AmountCurrency(
    val amount: String,
    val currency: String,
)

@Serializable
data class RiskEvaluation(
    val decision: String,
    val reason: String,
)

@Serializable
data class CirclePaymentRequest(
    val idempotencyKey: String,
    val keyId: String,
    val description: String,
    val verification: String,
    val encryptedData: String,
    val metadata: PaymentMetadata,
    val amount: AmountCurrency,
    val source: Source
)

@Serializable
data class PaymentResponseData(
    val id: String,
    val type: String,
    val merchantId: String,
    val merchantWalletId: String,
    val source: Source,
    val description: String,
    val amount: AmountCurrency,
    val status: String,
    val verification: Map<String, String>? = null,
    val cancel: Map<String, String>? = null,
    val refunds: List<Map<String, String>>? = null,
    val createDate: String,
    val updateDate: String,
    val metadata: Map<String, String>,
    val errorCode: String? = null,
    val riskEvaluation: RiskEvaluation? = null,
    val trackingRef: String? = null,
    val fees: AmountCurrency? = null,
)

@Serializable
data class CirclePaymentResponse(
    val data: PaymentResponseData
)

@Serializable
data class BillingDetails(
    val name: String,
    val city: String,
    val country: String,
    val line1: String,
    val district: String,
    val postalCode: String
)

@Serializable
data class CircleCreateCardRequest(
    val idempotencyKey: String,
    val keyId: String,
    val expMonth: Int,
    val expYear: Int,
    val encryptedData: String,
    val billingDetails: BillingDetails,
    val metadata: PaymentMetadata
)

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
data class CircleCreateCardResponse(
    val data: CreateCardResponseData,
)

@Serializable
data class PublicKeyResponseData(
    val keyId: String,
    val publicKey: String
)

@Serializable
data class PublicKeyResponse(
    val data: PublicKeyResponseData
)

fun createClient() : HttpClient{
    return HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
}

// Endpoint to store the information about the card and obtain the sourceId (Circle's internal card id) and encrypted fields
// Encrypted card details (card number and CVV) come from the client
// Some extra fields need to be included in the request to Circle's create card endpoint: billing details for your end user, a unique ID for the active session (sessionId) and the IP address of the end-user (ipAddress).
// Finally, the Circle API will respond with a card containing id value (sourceId) that can be stored on our side to refer to this end-user's card in future payment requests (passed in as sourceId)
suspend fun createCard(circleCreateCardRequest: CircleCreateCardRequest): String {
    val client = createClient()
    val response: HttpResponse = client.post("https://api-sandbox.circle.com/v1/cards") {
        val dotenv = dotenv {
            directory = "../"
            ignoreIfMalformed = true
            ignoreIfMissing = true
        }
        val apiKey: String = dotenv["CIRCLE_API_KEY"]
        headers {
            append(HttpHeaders.Accept, "application/json")
            append(HttpHeaders.Authorization, "Bearer " + apiKey)
        }
        contentType(ContentType.Application.Json)
        body = circleCreateCardRequest
        print("card creation request to Circle: " + body + "\n")
    }
    val responseBodyCircle: CircleCreateCardResponse = response.receive()
    // extract the card ID from the response + return it
    return responseBodyCircle.data.id
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

// queries the Circle /public endpoint to get PGP key for encrypting card credentials
suspend fun getPublicKey(): String {
    val client = createClient();
    val dotenv = dotenv {
        directory = "../"
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }
    val apiKey: String = dotenv["CIRCLE_API_KEY"]
    val response: HttpResponse = client.get("https://api-sandbox.circle.com/v1/encryption/public") {

        headers {
            append(HttpHeaders.Accept, "application/json")
            append(HttpHeaders.Authorization, "Bearer " + apiKey)
        }
    }
    return response.readText()
}

/**
 * more details on the API here
 * https://developers.circle.com/reference#payments-payments-get
 */
suspend fun makePayment(circlePaymentRequest: CirclePaymentRequest): CirclePaymentResponse? {
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }
    val response: HttpResponse = client.post("https://api-sandbox.circle.com/v1/payments") {
        val dotenv = dotenv {
            directory = "../"
            ignoreIfMalformed = true
            ignoreIfMissing = true
        }
        val apiKey: String = dotenv["CIRCLE_API_KEY"]

        headers {
            append(HttpHeaders.Accept, "application/json")
            append(HttpHeaders.Authorization, "Bearer $apiKey")
        }
        contentType(ContentType.Application.Json)
        body = circlePaymentRequest
        print("payment request to Circle: $body\n")
    }

    if (response.status.value >= HttpStatusCode.BadRequest.value /*400*/) {
        print("error with post request to circle payment with response content ${response.content}")
        return null
    }

    return response.receive()
}

fun buildPaymentRequest(
    idempotencyKey: String,
    sourceId: String,
    sourceType: String,
    ipAddress: String,
    amount: String,
    verificationMethod: String,
    encryptedData: String,
    pubKeyId: String,
    description: String,
    email: String,
    phoneNumber: String,
    userSessionId: String
): CirclePaymentRequest {
    println("building payment request for id $idempotencyKey")
    return CirclePaymentRequest(
        idempotencyKey, // for ensuring exactly-once execution of mutating requests
        pubKeyId, // unique identifier of pub key used in encryption
        description,
        verificationMethod,
        encryptedData,
        PaymentMetadata(ipAddress, email, phoneNumber, userSessionId),
        AmountCurrency(amount, "USD"),
        Source(sourceId, sourceType)
    )
}