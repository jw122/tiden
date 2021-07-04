package com.tiden.flagship.merchant

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * API routing for merchant related queries
 */
fun Application.registerMerchantRoutes() {
    routing {
        merchantRouting()
    }
}

fun Route.merchantRouting() {
    route("/merchant") {
        get {
            call.respondText("merchant api")
        }

        // TODO: in the long term as a security measure we shouldn't allow
        // calls to merchant balances for any merchant_id, only logged in merchants
        // can view its own balance. this can be done after login features are implemented
        route("/balance") {
            get("{merchant_id}") {
                val id = call.parameters["merchant_id"] ?: return@get call.respondText(
                    "Missing or malformed merchant_id",
                    status = HttpStatusCode.BadRequest
                )

                val amountCurrency = MerchantService.getMerchantBalanceForId(id) ?: return@get call.respondText(
                    "Unable to find balance with this id: $id",
                    status = HttpStatusCode.NotFound
                )

                call.respond(amountCurrency)
            }
        }
    }


}