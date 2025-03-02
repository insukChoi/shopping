package com.insuk.shopping.adapter.input.web.model.response

import com.insuk.shopping.adapter.input.web.model.response.BrandsLowestPriceResponse.BrandsLowestPriceContent
import com.insuk.shopping.adapter.input.web.model.response.BrandsLowestPriceResponse.BrandsLowestPriceContent.BrandsLowestPriceCategory
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual

internal class BrandsLowestPriceResponseTest : StringSpec({
    "totalPrice 가 올바르게 계산된다" {
        val content = BrandsLowestPriceContent(
            brandName = "A",
            categories = listOf(
                BrandsLowestPriceCategory(
                    categoryName = "바지",
                    price = 1000.toBigDecimal(),
                ),
                BrandsLowestPriceCategory(
                    categoryName = "아우터",
                    price = 5000.toBigDecimal(),
                ),
                BrandsLowestPriceCategory(
                    categoryName = "양말",
                    price = 700.toBigDecimal(),
                ),
            ),
        )

        val response = BrandsLowestPriceResponse(lowestPrice = content)

        response.lowestPrice.brandName shouldBeEqual "A"
        response.lowestPrice.totalPrice shouldBeEqualIgnoringScale 6700.toBigDecimal()
        response.lowestPrice.categories shouldHaveSize 3
    }

    "totalPrice 는 합산할 값이 없으면 0이 된다" {
        val content = BrandsLowestPriceContent(
            brandName = "A",
            categories = emptyList(),
        )
        val response = BrandsLowestPriceResponse(lowestPrice = content)

        response.lowestPrice.totalPrice shouldBeEqualIgnoringScale 0.toBigDecimal()
    }
})
