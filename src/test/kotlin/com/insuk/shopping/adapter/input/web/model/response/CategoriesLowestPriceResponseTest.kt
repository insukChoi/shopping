package com.insuk.shopping.adapter.input.web.model.response

import com.insuk.shopping.adapter.input.web.model.response.CategoriesLowestPriceResponse.CategoriesLowestPriceContent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale

internal class CategoriesLowestPriceResponseTest : StringSpec({
    "totalPrice 가 잘 계산된다" {
        val items =
            listOf(
                CategoriesLowestPriceContent("바지", "D", 3000.toBigDecimal()),
                CategoriesLowestPriceContent("아우터", "E", 5000.toBigDecimal()),
                CategoriesLowestPriceContent("양말", "I", 1700.toBigDecimal()),
            )

        val response = CategoriesLowestPriceResponse(contents = items)

        response.totalPrice shouldBeEqualIgnoringScale 9700.toBigDecimal()
    }

    "totalPrice 는 합산할 값이 없으면 0이 된다" {
        val response = CategoriesLowestPriceResponse(contents = emptyList())

        response.totalPrice shouldBeEqualIgnoringScale 0.toBigDecimal()
    }
})
