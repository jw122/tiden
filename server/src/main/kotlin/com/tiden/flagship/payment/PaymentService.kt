package com.tiden.flagship.payment

import com.tiden.flagship.circle.CircleCreateCardRequest
import com.tiden.flagship.circle.CirclePaymentRequest
import com.tiden.flagship.circle.PaymentResponseData
import com.tiden.flagship.payment.models.PaymentRequest
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

/**
 * payments data access
 */
class PaymentService {

    companion object {
        fun storeSuccessfulPayment(cardRequest: CircleCreateCardRequest, paymentRequest: PaymentRequest, paymentResponse: PaymentResponseData) {
            // TODO: look into JOOQ codegen + DSL

            // update payment table
            transaction {
                addLogger(StdOutSqlLogger)

                val sqlString = "INSERT INTO public.payment (amount, payment_id, description, email, phone_number, " +
                        "verification_method, source_id, source_type, cvv, expiration_month, expiration_year, " +
                        "name, address, city, district, postal_code, country, " +
                        "platform_merchant_id, platform_wallet_id, " +
                        "fee_amount, fee_currency, tracking_ref, " +
                        "risk_decision_reason, risk_decision, " +
                        "create_date, update_date, currency, " +
                        "merchant_id," +
                        "status) " +
                        "VALUES (${paymentRequest.amount}, '${paymentResponse.id}', '${paymentRequest.description}', '${paymentRequest.email}', '${paymentRequest.phoneNumber}', " +
                        "'${paymentRequest.verificationMethod}', '${paymentResponse.source.id}', '${paymentResponse.source.type}', '${cardRequest.encryptedData}', '${cardRequest.expMonth}', '${cardRequest.expYear}', " +
                        "'${cardRequest.billingDetails.name}', '${cardRequest.billingDetails.line1}', '${cardRequest.billingDetails.city}', '${cardRequest.billingDetails.district}', '${cardRequest.billingDetails.postalCode}', '${cardRequest.billingDetails.country}', " +
                        "'${paymentResponse.merchantId}', '${paymentResponse.merchantWalletId}', " +
                        "'${paymentResponse.fees?.amount}', '${paymentResponse.fees?.currency}', '${paymentResponse.trackingRef}', " +
                        "'${paymentResponse.riskEvaluation?.reason}', '${paymentResponse.riskEvaluation?.decision}', " +
                        "'${paymentResponse.createDate}', '${paymentResponse.updateDate}', '${paymentResponse.amount.currency}', " +
                        "${paymentRequest.merchantId}, " +
                        "'CREATED'" + // TODO: if we're reusing this to insert at other parts of the request make this a parameter
                        ");"
                exec(sqlString) {
                    while (it.next()) {
                        println("insert result $it.row")
                    }
                }
            }

            // update merchant_balance table
            transaction {
                val sqlString = "UPDATE public.merchant_balance SET amount = amount + ${ paymentRequest.amount} WHERE merchant_id = ${paymentRequest.merchantId};"

                exec(sqlString) {
                    while (it.next()) {
                        println("insert result $it.row")
                    }
                }
            }
        }

        fun storeFailedPayment(cardRequest: CircleCreateCardRequest, paymentRequest: PaymentRequest) {
            transaction {
                addLogger(StdOutSqlLogger)

                val sqlString = "INSERT INTO public.payment (amount, payment_id, description, email, phone_number, " +
                        "verification_method, source_id, source_type, cvv, expiration_month, expiration_year, " +
                        "name, address, city, district, postal_code, country, " +
                        "platform_merchant_id, platform_wallet_id, " +
                        "fee_amount, fee_currency, tracking_ref, " +
                        "risk_decision_reason, risk_decision, " +
                        "create_date, update_date, currency, " +
                        "merchant_id," +
                        "status) " +
                        "VALUES (${paymentRequest.amount}, NULL, '${paymentRequest.description}', '${paymentRequest.email}', '${paymentRequest.phoneNumber}', " +
                        "'${paymentRequest.cvv}', NULL, NULL, '${cardRequest.encryptedData}', '${cardRequest.expMonth}', '${cardRequest.expYear}', " +
                        "'${cardRequest.billingDetails.name}', '${cardRequest.billingDetails.line1}', '${cardRequest.billingDetails.city}', '${cardRequest.billingDetails.district}', '${cardRequest.billingDetails.postalCode}', '${cardRequest.billingDetails.country}', " +
                        "NULL, NULL, " +
                        "NULL, NULL, NULL, " +
                        "NULL, NULL, " +
                        "NULL, NULL, NULL, " +
                        "${paymentRequest.merchantId}, " +
                        "'FAILED'" + // TODO: if we're reusing this to insert at other parts of the request make this a parameter
                        ");"
                exec(sqlString) {
                    while (it.next()) {
                        println("insert result $it.row")
                    }
                }
            }
        }

        fun getPaymentWithSourceId(sourceId: String): PaymentRequest? {
            var result: PaymentRequest? = null

            transaction {
                addLogger(StdOutSqlLogger)


                exec(
                    "SELECT amount, description, email, phone_number, verification_method, " +
                            "cvv, source_id, source_type FROM public.payment WHERE source_id = '${sourceId}'"
                ) {
                    while (it.next()) {
                        result = getPaymentFromResultSet(it)
                    }
                }
            }
            return result
        }

        fun getAnyPayment(): PaymentRequest? {
            var result: PaymentRequest? = null

            transaction {
                addLogger(StdOutSqlLogger)

                exec(
                    "SELECT amount, description, email, phone_number, verification_method, " +
                            "cvv, source_id, source_type FROM public.payment limit 1;"
                ) {
                    while (it.next()) {
                        result = getPaymentFromResultSet(it)
                    }
                }
            }
            return result
        }

        private fun getPaymentFromResultSet(rs: ResultSet): PaymentRequest {
            return PaymentRequest(
                rs.getString("payment_id"),
                rs.getString("amount"),
                rs.getString("description"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("verification_method"),
                rs.getString("source_type"),
                rs.getString("cvv"),
                rs.getInt("expiration_month"),
                rs.getInt("expiration_year"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("city"),
                rs.getString("district"),
                rs.getString("postalCode"),
                rs.getString("country"),
                rs.getString("key_id"),
                rs.getString("merchant_id")
            )
        }
    }
}