package com.insuk.shopping.application.domain.usecase

import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.domain.model.ProductOfBrandLowest
import com.insuk.shopping.application.domain.model.ProductOfLowestAndHighestPriceBrands
import com.insuk.shopping.application.port.input.ShoppingQueryInputPort
import com.insuk.shopping.application.port.output.ShoppingQueryOutputPort
import com.insuk.shopping.exception.UseCaseErrorMessage.*
import com.insuk.shopping.exception.UseCaseException

@UseCase
class ShoppingQueryUseCase(
    private val shoppingQueryOutputPort: ShoppingQueryOutputPort,
) : ShoppingQueryInputPort {
    override fun loadCategoriesLowestPrice(): List<Product> =
        runCatching {
            val categoryLowestPriceList = shoppingQueryOutputPort.getCategoryMinPrices()

            return categoryLowestPriceList.map { (categoryId, lowestPrice) ->
                shoppingQueryOutputPort.getProductByCategoryAndLowestPrice(categoryId, lowestPrice)
            }
        }.getOrElse { throw UseCaseException(LOAD_CATEGORIES_LOWEST_PRICE_EXCEPTION.errorMessage, it) }

    override fun loadBrandLowestPriceContent(): ProductOfBrandLowest {
        runCatching {
            val lowestPriceBrandId = shoppingQueryOutputPort.getLowestBrandIdByCategories()

            return shoppingQueryOutputPort.getProductOfLowestBrandByBrandId(lowestPriceBrandId)
        }.getOrElse { throw UseCaseException(LOAD_BRAND_LOWEST_PRICE_EXCEPTION.errorMessage, it) }
    }

    override fun loadLowestAndHighestPriceBrands(categoryName: String): ProductOfLowestAndHighestPriceBrands {
        val categoryId = requireNotNull(shoppingQueryOutputPort.getCategoryByCategoryName(categoryName).first) {
            "${CAN_NOT_FIND_CATEGORY_EXCEPTION.errorMessage} -> categoryName: $categoryName"
        }
        return runCatching {
            shoppingQueryOutputPort.getLowestAndHighestPriceBrands(categoryId)
        }.getOrElse {
            throw UseCaseException(LOAD_LOWEST_HIGHEST_PRICE_BRANDS_EXCEPTION.errorMessage, it)
        }
    }
}
