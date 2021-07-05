package com.tiden.flagship.transfer.models

import kotlinx.serialization.Serializable

// This annotation from kotlinx.serialization can work with Ktor to generate JSON representation for API responses automatically
@Serializable
data class TransferRequest(
    val walletId: String,
    val destinationType: String,
    val destinationAddress: String,
    val chain: String,
    val amount: String,
    val currency: String)
