package com.insuk.shopping.application.port.input

import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.domain.model.ProductOfBrandLowest
import com.insuk.shopping.application.domain.model.ProductOfLowestAndHighestPriceBrands

interface ShoppingQueryInputPort {
    fun loadCategoriesLowestPrice(): List<Product>
    fun loadBrandLowestPriceContent(): ProductOfBrandLowest
    fun loadLowestAndHighestPriceBrands(categoryName: String): ProductOfLowestAndHighestPriceBrands
}
