package com.tiden.flagship.merchant

import com.tiden.flagship.circle.AmountCurrency
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

/**
 *
 */
class MerchantService {
    companion object {

        fun getMerchantBalanceForId(merchantId: String): AmountCurrency? {
            var result: AmountCurrency? = null

            transaction {
                addLogger(StdOutSqlLogger)

                exec(
                    "SELECT amount FROM public.merchant_balance " +
                            "WHERE merchant_id = '${merchantId}'"
                ) {
                    while (it.next()) {
                        // TODO: hard coding to USDC for now until we update merchant balances to hold currency info
                        result = AmountCurrency(it.getBigDecimal("amount").toString(), "USDC")
                    }
                }
            }
            return result
        }
    }
}