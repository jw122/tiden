package com.tiden.flagship.payment.models

import kotlinx.serialization.Serializable

// In-memory storage for Payments (temporary). Will eventually be persisted in DB
val paymentStorage = mutableListOf<Payment>()

// This annotation from kotlinx.serialization can work with Ktor to generate JSON representation for API responses automatically
@Serializable
data class Payment(
    val amount: String,
    val description: String,
    val email: String,
    val phoneNumber: String,
    val verificationMethod: String,
    val cvv: String,
    val sourceId: String,
    val sourceType: String)

// TODO: all data access objects should have method to transform data from
// result set to a POJO

