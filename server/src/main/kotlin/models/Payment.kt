package models

import kotlinx.serialization.Serializable

// In-memory storage for Payments (temporary). Will eventually be persisted in DB
val paymentStorage = mutableListOf<Payment>()

// This annotation from kotlinx.serialization can work with Ktor to generate JSON representation for API responses automatically
@Serializable
data class Payment(val id: String, val amount: Int, val source: String, val type: String)