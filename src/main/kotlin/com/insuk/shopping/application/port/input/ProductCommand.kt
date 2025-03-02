package com.insuk.shopping.application.port.input

import com.insuk.shopping.exception.ValidationErrorMessage.PRICE_NONE_POSITIVE_EXCEPTION
import java.math.BigDecimal

data class ProductCommand(
    val brandName: String,
    val categoryName: String,
    val price: BigDecimal,
) {
    init {
        require(price > BigDecimal.ZERO) {
            PRICE_NONE_POSITIVE_EXCEPTION.errorMessage
        }
    }
}

data class ProductDeleteCommand(
    val brandName: String,
    val categoryName: String,
)
