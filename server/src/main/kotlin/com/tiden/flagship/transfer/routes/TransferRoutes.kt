package com.tiden.flagship.transfer

import com.tiden.flagship.circle.*
import com.tiden.flagship.transfer.models.TransferRequest
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import java.util.concurrent.TimeUnit

/**
 * API routing for transfers from existing funded Circle wallet to another wallet or blockchain address
 */
fun Application.registerTransferRoutes() {
    routing {
        transferRouting()
    }
}

fun Route.transferRouting() {
    route("/transfer") {
        post {
            val transferRequest = call.receive<TransferRequest>()
            val idempotencyKey = java.util.UUID.randomUUID().toString()

            val circleTransferRequest: CircleTransferRequest = CircleTransferRequest(
                TransferSource("wallet", transferRequest.walletId),
                TransferDestination(transferRequest.destinationType, transferRequest.destinationAddress, transferRequest.chain),
                AmountCurrency(transferRequest.amount, transferRequest.currency),
                idempotencyKey
            )

            val circleTransferResponse: CircleTransferResponse? = makeTransfer(circleTransferRequest)

            if (circleTransferResponse != null) {
                println("[transfer] response from circle: " + circleTransferResponse)
                
                call.respond(circleTransferResponse)
            } else {
                call.respondText("Unable to process transfer: " + circleTransferRequest.idempotencyKey, status = HttpStatusCode.InternalServerError)
            }
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )

            val transferStatus: String? = getTransferStatus(id)

            if (transferStatus != null) {
                println("transfer status received: " + transferStatus)
                call.respond(transferStatus)
            } else {
                call.respondText("Unable to get status for transfer: " + id, status = HttpStatusCode.InternalServerError)
            }

        }
    }

}