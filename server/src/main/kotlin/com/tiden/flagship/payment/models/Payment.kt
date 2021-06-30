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
    // card details
    val sourceType: String,
    val cvv: String,
    val expirationMonth: Int,
    val expirationYear: Int,
    // billing details
    // name must be first + last
    val name: String,
    val address: String,
    val city: String,
    val district: String,
    val postalCode: String,
    // must be valid ISO 31660-2 country code
    val country: String
    )

// TODO: all data access objects should have method to transform data from
// result set to a POJO
