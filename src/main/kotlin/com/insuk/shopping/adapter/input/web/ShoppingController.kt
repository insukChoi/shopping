package com.insuk.shopping.adapter.input.web

import com.insuk.shopping.adapter.input.web.model.request.ProductDeleteRequest
import com.insuk.shopping.adapter.input.web.model.request.ProductRequest
import com.insuk.shopping.adapter.input.web.model.response.BrandsLowestPriceResponse
import com.insuk.shopping.adapter.input.web.model.response.BrandsLowestPriceResponse.BrandsLowestPriceContent
import com.insuk.shopping.adapter.input.web.model.response.BrandsLowestPriceResponse.BrandsLowestPriceContent.BrandsLowestPriceCategory
import com.insuk.shopping.adapter.input.web.model.response.CategoriesLowestPriceResponse
import com.insuk.shopping.adapter.input.web.model.response.CategoriesLowestPriceResponse.CategoriesLowestPriceContent
import com.insuk.shopping.adapter.input.web.model.response.LowestAndHighestBrandWithPriceResponse
import com.insuk.shopping.adapter.input.web.model.response.LowestAndHighestBrandWithPriceResponse.BrandWithPriceContent
import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.domain.model.ProductOfBrandLowest
import com.insuk.shopping.application.port.input.ProductCommand
import com.insuk.shopping.application.port.input.ProductDeleteCommand
import com.insuk.shopping.application.port.input.ShoppingCommandInputPort
import com.insuk.shopping.application.port.input.ShoppingQueryInputPort
import com.insuk.shopping.common.Transformer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shopping/v1")
class ShoppingController(
    private val shoppingQueryInputPort: ShoppingQueryInputPort,
    private val shoppingCommandInputPort: ShoppingCommandInputPort,
) {
    /**
     * 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
     */
    @GetMapping("/categories/lowest-price")
    fun categoriesLowestPrice(): ResponseEntity<CategoriesLowestPriceResponse> {
        val query: List<Product> = shoppingQueryInputPort.loadCategoriesLowestPrice()
        return ResponseEntity.ok(
            CategoriesLowestPriceResponse(
                contents =
                query.map { product ->
                    CategoriesLowestPriceContent(
                        categoryName = product.category.name,
                        brandName = product.brand.name,
                        price = product.price,
                    )
                },
            ),
        )
    }

    /**
     * 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API
     */
    @GetMapping("/brand/lowest-price")
    fun brandLowestPrice(): ResponseEntity<BrandsLowestPriceResponse> {
        val query: ProductOfBrandLowest = shoppingQueryInputPort.loadBrandLowestPriceContent()
        return ResponseEntity.ok(
            BrandsLowestPriceResponse(
                lowestPrice = BrandsLowestPriceContent(
                    brandName = query.brandName,
                    categories = query.categories.map { category ->
                        BrandsLowestPriceCategory(
                            categoryName = category.categoryName,
                            price = category.price,
                        )
                    },
                ),
            ),
        )
    }

    /**
     * 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
     */
    @GetMapping("/categories/prices/min-max")
    fun categoryMinMaxPrices(
        @RequestParam("category_name") categoryName: String,
    ): ResponseEntity<LowestAndHighestBrandWithPriceResponse> {
        val query = shoppingQueryInputPort.loadLowestAndHighestPriceBrands(categoryName)
        return ResponseEntity.ok(
            LowestAndHighestBrandWithPriceResponse(
                categoryName = categoryName,
                lowestPrice = query.lowestBrandWithPrice?.let {
                    BrandWithPriceContent(
                        brandName = query.lowestBrandWithPrice.brandName,
                        price = query.lowestBrandWithPrice.price,
                    )
                },
                highestPrice = query.highestBrandWithPrice?.let {
                    BrandWithPriceContent(
                        brandName = query.highestBrandWithPrice.brandName,
                        price = query.highestBrandWithPrice.price,
                    )
                },
            ),
        )
    }

    /**
     * 브랜드 및 상품 추가 API
     */
    @PostMapping("/product")
    fun addProduct(
        @RequestBody productRequest: ProductRequest,
    ): ResponseEntity<Product> {
        val command = productRequest.toCommand()
        return ResponseEntity.status(HttpStatus.CREATED).body(
            shoppingCommandInputPort.addProduct(command),
        )
    }

    /**
     * 브랜드 및 상품 업데이트 API
     */
    @PatchMapping("/product")
    fun updateProduct(
        @RequestBody productRequest: ProductRequest,
    ): ResponseEntity<Product> {
        val command = productRequest.toCommand()

        return when (val updatedProduct = shoppingCommandInputPort.updateProduct(command)) {
            null -> ResponseEntity.noContent().build()
            else -> ResponseEntity.ok(updatedProduct)
        }
    }

    /**
     * 브랜드 및 상품 삭제 API
     */
    @DeleteMapping("/product")
    fun deleteProduct(
        @RequestBody productDeleteRequest: ProductDeleteRequest,
    ): ResponseEntity<Void> {
        val command = productDeleteRequest.toCommand()
        shoppingCommandInputPort.deleteProduct(command)
        return ResponseEntity.noContent().build()
    }

    private fun ProductRequest.toCommand() =
        Transformer(
            inClass = ProductRequest::class,
            outClass = ProductCommand::class,
        ).transform(this)

    private fun ProductDeleteRequest.toCommand() =
        Transformer(
            inClass = ProductDeleteRequest::class,
            outClass = ProductDeleteCommand::class,
        ).transform(this)
}
