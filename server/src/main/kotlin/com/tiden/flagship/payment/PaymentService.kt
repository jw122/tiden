package com.tiden.flagship.payment

import com.tiden.flagship.payment.models.Payment
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.ResultSet

/**
 * payments DAO
 */
class PaymentService {

    companion object {
        fun addPayment(payment: Payment) {
            // TODO: update this to include all fields in newly defined Payment object
            transaction {
                addLogger(StdOutSqlLogger)

                val sqlString = "INSERT INTO public.payment (amount, description, email, phone_number, verification_method, " +
                        "cvv, source_id, source_type)\n" +
                        "VALUES (${payment.amount}, '${payment.description}', '${payment.email}', '${payment.phoneNumber}', " +
                        "'${payment.verificationMethod}', '${payment.cvv}', '${payment.paymentId}', '${payment.sourceType}');"
                exec(sqlString) {
                    while (it.next()) {
                        println("insert result $it.row")
                    }
                }
            }
        }

        fun getPaymentWithSourceId(sourceId: String): Payment? {
            var result: Payment? = null

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

        fun getAnyPayment(): Payment? {
            var result: Payment? = null

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

        private fun getPaymentFromResultSet(rs: ResultSet): Payment {
            return Payment(
                rs.getString("amount"),
                rs.getString("description"),
                rs.getString("email"),
                rs.getString("phone_number"),
                rs.getString("verification_method"),
                rs.getString("cvv"),
                rs.getString("source_id"),
                rs.getString("source_type"),
                rs.getInt("expirationMonth"),
                rs.getInt("expirationYear"),
                rs.getString("address"),
                rs.getString("district"),
                rs.getString("city"),
                rs.getString("country"),
                rs.getString("name"),
                rs.getString("postalCode"),
            )
        }
    }
}