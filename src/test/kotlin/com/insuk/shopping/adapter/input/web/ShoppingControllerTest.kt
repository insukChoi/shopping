package com.insuk.shopping.adapter.input.web

import com.insuk.shopping.adapter.input.web.model.request.ProductDeleteRequest
import com.insuk.shopping.adapter.input.web.model.request.ProductRequest
import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.domain.model.ProductOfBrandLowest
import com.insuk.shopping.application.domain.model.ProductOfLowestAndHighestPriceBrands
import com.insuk.shopping.application.port.input.ShoppingCommandInputPort
import com.insuk.shopping.application.port.input.ShoppingQueryInputPort
import com.insuk.shopping.config.JacksonConfiguration
import com.insuk.shopping.exception.GlobalExceptionHandler
import com.insuk.shopping.exception.UseCaseErrorMessage.*
import com.insuk.shopping.exception.UseCaseException
import com.insuk.shopping.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.*
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
@AutoConfigureMockMvc
class ShoppingControllerTest : BehaviorSpec({
    val shoppingQueryInputPort = mockk<ShoppingQueryInputPort>()
    val shoppingCommandInputPort = mockk<ShoppingCommandInputPort>()
    val controller = ShoppingController(shoppingQueryInputPort, shoppingCommandInputPort)
    val objectMapper = JacksonConfiguration().objectMapper()
    lateinit var mockMvc: MockMvc

    beforeTest {
        mockMvc =
            MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(GlobalExceptionHandler())
                .build()
        clearMocks(shoppingQueryInputPort)
    }

    given("카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API 를 호출할때") {
        `when`("정상적으로 조회되면") {
            then("HTTP 200 응답과 함께 최저 가격 정보를 반환한다") {
                val product1 = fixtureMonkey.giveMeOne<Product>()
                val product2 = fixtureMonkey.giveMeOne<Product>()
                val mockProducts = listOf(product1, product2)

                every { shoppingQueryInputPort.loadCategoriesLowestPrice() } returns mockProducts

                mockMvc.perform(get("$URI/categories/lowest-price"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("contents.[0].categoryName").value(product1.category.name))
                    .andExpect(jsonPath("contents.[0].brandName").value(product1.brand.name))
                    .andExpect(jsonPath("contents.[0].price").value(product1.price))
                    .andExpect(jsonPath("contents.[1].categoryName").value(product2.category.name))
                    .andExpect(jsonPath("contents.[1].brandName").value(product2.brand.name))
                    .andExpect(jsonPath("contents.[1].price").value(product2.price))

                verify(exactly = 1) { shoppingQueryInputPort.loadCategoriesLowestPrice() }
            }
        }

        `when`("예외가 발생하면") {
            then("HTTP 500 응답을 반환한다") {
                every { shoppingQueryInputPort.loadCategoriesLowestPrice() } throws
                    UseCaseException(LOAD_CATEGORIES_LOWEST_PRICE_EXCEPTION.errorMessage)

                mockMvc.perform(get("$URI/categories/lowest-price"))
                    .andExpect(status().isInternalServerError)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(LOAD_CATEGORIES_LOWEST_PRICE_EXCEPTION.errorMessage))

                verify(exactly = 1) { shoppingQueryInputPort.loadCategoriesLowestPrice() }
            }
        }
    }
    given("단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API 를 호출할때") {
        `when`("정상적으로 조회되면") {
            then("HTTP 200 응답과 함께 최저가격에 판매하는 브랜드와 카테고리의 상품가격을 반환한다") {
                val productOfBrandLowest = fixtureMonkey.giveMeBuilder<ProductOfBrandLowest>()
                    .sizeExp(ProductOfBrandLowest::categories, 2)
                    .sample()

                every { shoppingQueryInputPort.loadBrandLowestPriceContent() } returns productOfBrandLowest

                mockMvc.perform(get("$URI/brand/lowest-price"))
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("lowestPrice.brandName").value(productOfBrandLowest.brandName))
                    .andExpect(jsonPath("lowestPrice.categories").exists())

                verify(exactly = 1) { shoppingQueryInputPort.loadBrandLowestPriceContent() }
            }
        }

        `when`("예외가 발생하면") {
            then("HTTP 500 응답을 반환한다") {
                every { shoppingQueryInputPort.loadBrandLowestPriceContent() } throws
                    UseCaseException(LOAD_BRAND_LOWEST_PRICE_EXCEPTION.errorMessage)

                mockMvc.perform(get("$URI/brand/lowest-price"))
                    .andExpect(status().isInternalServerError)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(LOAD_BRAND_LOWEST_PRICE_EXCEPTION.errorMessage))

                verify(exactly = 1) { shoppingQueryInputPort.loadBrandLowestPriceContent() }
            }
        }
    }
    given("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API 를 호출할때") {
        val categoryName = fixtureMonkey.giveMeOne<String>()
        `when`("정상적으로 조회되면") {
            then("HTTP 200 응답과 함께 최저, 최고 가격 브랜드와 상품 가격 정보를 반환한다") {
                val productOfLowestAndHighestPriceBrands =
                    fixtureMonkey.giveMeBuilder<ProductOfLowestAndHighestPriceBrands>()
                        .setNotNullExp(ProductOfLowestAndHighestPriceBrands::lowestBrandWithPrice)
                        .setNotNullExp(ProductOfLowestAndHighestPriceBrands::highestBrandWithPrice)
                        .sample()

                every { shoppingQueryInputPort.loadLowestAndHighestPriceBrands(any()) } returns productOfLowestAndHighestPriceBrands

                mockMvc.perform(
                    get("$URI/categories/prices/min-max")
                        .param("categoryName", categoryName),
                )
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("categoryName").value(categoryName))
                    .andExpect(jsonPath("lowestPrice.brandName").value(productOfLowestAndHighestPriceBrands.lowestBrandWithPrice?.brandName))
                    .andExpect(jsonPath("lowestPrice.price").value(productOfLowestAndHighestPriceBrands.lowestBrandWithPrice?.price))
                    .andExpect(jsonPath("highestPrice.brandName").value(productOfLowestAndHighestPriceBrands.highestBrandWithPrice?.brandName))
                    .andExpect(jsonPath("highestPrice.price").value(productOfLowestAndHighestPriceBrands.highestBrandWithPrice?.price))

                verify(exactly = 1) { shoppingQueryInputPort.loadLowestAndHighestPriceBrands(categoryName) }
            }
        }

        `when`("카테고리 명을 전송하지않으면") {
            then("HTTP 400 응답을 반환한다") {
                mockMvc.perform(
                    get("$URI/categories/prices/min-max"),
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            }
        }

        `when`("예외가 발생하면") {
            then("HTTP 500 응답을 반환한다") {
                every { shoppingQueryInputPort.loadLowestAndHighestPriceBrands(any()) } throws
                    UseCaseException(LOAD_LOWEST_HIGHEST_PRICE_BRANDS_EXCEPTION.errorMessage)

                mockMvc.perform(
                    get("$URI/categories/prices/min-max")
                        .param("categoryName", categoryName),
                )
                    .andExpect(status().isInternalServerError)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value(LOAD_LOWEST_HIGHEST_PRICE_BRANDS_EXCEPTION.errorMessage))

                verify(exactly = 1) { shoppingQueryInputPort.loadLowestAndHighestPriceBrands(categoryName) }
            }
        }
    }
    given("브랜드 및 상품 추가 API 를 호출할 때") {
        val productRequest = fixtureMonkey.giveMeBuilder<ProductRequest>()
            .setExp(ProductRequest::price, 1000.toBigDecimal())
            .sample()
        val product = fixtureMonkey.giveMeOne<Product>()
        `when`("정상적으로 추가되면") {
            then("201 응답과 함께 상품이 추가된다") {
                every { shoppingCommandInputPort.addProduct(any()) } returns product

                mockMvc.perform(
                    post("$URI/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)),
                )
                    .andExpect(status().isCreated)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("price").value(product.price))
                    .andExpect(jsonPath("brand").exists())
                    .andExpect(jsonPath("category").exists())

                verify(exactly = 1) { shoppingCommandInputPort.addProduct(any()) }
            }
        }
        `when`("예외가 발생하면") {
            then("HTTP 500 응답을 반환한다") {
                every { shoppingCommandInputPort.addProduct(any()) } throws
                    UseCaseException(ADD_PRODUCT_EXCEPTION.errorMessage)

                mockMvc.perform(
                    post("$URI/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)),
                )
                    .andExpect(status().isInternalServerError)
                    .andExpect(jsonPath("$.message").value(ADD_PRODUCT_EXCEPTION.errorMessage))
            }
        }
    }
    given("브랜드 및 상품 업데이트 API 를 호출할 때") {
        val productRequest = fixtureMonkey.giveMeBuilder<ProductRequest>()
            .setExp(ProductRequest::price, 1000.toBigDecimal())
            .sample()
        val product = fixtureMonkey.giveMeOne<Product>()
        `when`("정상적으로 변경되면") {
            then("200 응답과 변경된 상품이 조회된다") {
                every { shoppingCommandInputPort.updateProduct(any()) } returns product

                mockMvc.perform(
                    patch("$URI/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)),
                )
                    .andExpect(status().isOk)
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("price").value(product.price))
                    .andExpect(jsonPath("brand").exists())
                    .andExpect(jsonPath("category").exists())

                verify(exactly = 1) { shoppingCommandInputPort.updateProduct(any()) }
            }
        }
        `when`("변경되는 컨텐츠가 없으면") {
            then("204(NO_CONTENT) 응답을 반환한다") {
                every { shoppingCommandInputPort.updateProduct(any()) } returns null

                mockMvc.perform(
                    patch("$URI/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)),
                )
                    .andExpect(status().isNoContent)
            }
        }
        `when`("예외가 발생하면") {
            then("HTTP 500 응답을 반환한다") {
                every { shoppingCommandInputPort.updateProduct(any()) } throws
                    UseCaseException(UPDATE_PRODUCT_EXCEPTION.errorMessage)

                mockMvc.perform(
                    patch("$URI/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)),
                )
                    .andExpect(status().isInternalServerError)
                    .andExpect(jsonPath("$.message").value(UPDATE_PRODUCT_EXCEPTION.errorMessage))
            }
        }
    }
    given("브랜드 및 상품 삭제 API 를 호출할 때") {
        val productDeleteRequest = fixtureMonkey.giveMeOne<ProductDeleteRequest>()
        `when`("정삭적으로 삭제 되면") {
            then("204(NO_CONTENT) 응답을 반환한다") {
                every { shoppingCommandInputPort.deleteProduct(any()) } just runs

                mockMvc.perform(
                    delete("$URI/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDeleteRequest)),
                )
                    .andExpect(status().isNoContent)
            }
        }
        `when`("예외가 발생하면") {
            then("HTTP 500 응답을 반환한다") {
                every { shoppingCommandInputPort.deleteProduct(any()) } throws
                    UseCaseException(DELETE_PRODUCT_EXCEPTION.errorMessage)

                mockMvc.perform(
                    delete("$URI/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDeleteRequest)),
                )
                    .andExpect(status().isInternalServerError)
                    .andExpect(jsonPath("$.message").value(DELETE_PRODUCT_EXCEPTION.errorMessage))
            }
        }
    }
}) {
    companion object {
        private const val URI = "/shopping/v1"
    }
}
