package com.insuk.shopping.application.domain.usecase

import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.domain.model.ProductOfBrandLowest
import com.insuk.shopping.application.domain.model.ProductOfLowestAndHighestPriceBrands
import com.insuk.shopping.application.port.output.CategoryWIthId
import com.insuk.shopping.application.port.output.ShoppingQueryOutputPort
import com.insuk.shopping.exception.UseCaseErrorMessage.*
import com.insuk.shopping.exception.UseCaseException
import com.insuk.shopping.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.setExp
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

internal class ShoppingQueryUseCaseTest : BehaviorSpec({
    val shoppingQueryOutputPort = mockk<ShoppingQueryOutputPort>()
    val shoppingQueryUseCase = ShoppingQueryUseCase(shoppingQueryOutputPort)

    given("카테고리별 최저 가격 상품을 불러올 때") {
        `when`("카테고리별 최저 가격을 반환받으면") {
            then("해당 가격의 상품을 올바르게 조회한다") {
                val categoryMinPrices =
                    listOf(
                        1L to 5000.toBigDecimal(),
                        2L to 10000.toBigDecimal(),
                    )
                val product1 = fixtureMonkey.giveMeBuilder<Product>()
                    .setExp(Product::price, 5000.toBigDecimal())
                    .sample()
                val product2 = fixtureMonkey.giveMeBuilder<Product>()
                    .setExp(Product::price, 10000.toBigDecimal())
                    .sample()

                every { shoppingQueryOutputPort.getCategoryMinPrices() } returns categoryMinPrices
                every {
                    shoppingQueryOutputPort.getProductByCategoryAndLowestPrice(1L, 5000.toBigDecimal())
                } returns product1
                every {
                    shoppingQueryOutputPort.getProductByCategoryAndLowestPrice(2L, 10000.toBigDecimal())
                } returns product2

                val result = shoppingQueryUseCase.loadCategoriesLowestPrice()

                result shouldHaveSize 2
                result[0] shouldBe product1
                result[1] shouldBe product2

                verify(exactly = 1) { shoppingQueryOutputPort.getCategoryMinPrices() }
                verify(exactly = 1) { shoppingQueryOutputPort.getProductByCategoryAndLowestPrice(1L, 5000.toBigDecimal()) }
                verify(exactly = 1) { shoppingQueryOutputPort.getProductByCategoryAndLowestPrice(2L, 10000.toBigDecimal()) }
            }
        }
        `when`("최저가격 조회중 예외가 발생하면") {
            `when`("getCategoryMinPrices()에서 예외 발생") {
                then("UseCaseException으로 변환되어야 한다") {
                    every { shoppingQueryOutputPort.getCategoryMinPrices() } throws RuntimeException("DB 연결 오류")

                    val exception =
                        shouldThrow<UseCaseException> {
                            shoppingQueryUseCase.loadCategoriesLowestPrice()
                        }

                    exception.message shouldBeEqual LOAD_CATEGORIES_LOWEST_PRICE_EXCEPTION.errorMessage
                }
            }

            `when`("getProductByCategoryAndLowestPrice()에서 예외 발생") {
                then("UseCaseException으로 변환되어야 한다") {
                    every { shoppingQueryOutputPort.getCategoryMinPrices() } returns listOf(1L to 5000.toBigDecimal())
                    every {
                        shoppingQueryOutputPort.getProductByCategoryAndLowestPrice(
                            1L,
                            5000.toBigDecimal(),
                        )
                    } throws RuntimeException("상품 조회 실패")

                    val exception =
                        shouldThrow<UseCaseException> {
                            shoppingQueryUseCase.loadCategoriesLowestPrice()
                        }

                    exception.message shouldBeEqual LOAD_CATEGORIES_LOWEST_PRICE_EXCEPTION.errorMessage
                }
            }
        }
    }
    given("최저가격 브랜드의 카테고리 상품을 불러올 때") {
        `when`("최저가격 브랜드ID를 가져오면") {
            then("해당 브랜드ID로 모든 카테고리 상품을 가져온다") {
                val productOfBrandLowest = fixtureMonkey.giveMeOne<ProductOfBrandLowest>()

                every { shoppingQueryOutputPort.getLowestBrandIdByCategories() } returns 1L
                every { shoppingQueryOutputPort.getProductOfLowestBrandByBrandId(1L) } returns productOfBrandLowest

                val result = shoppingQueryUseCase.loadBrandLowestPriceContent()
                result shouldBe productOfBrandLowest

                verify(exactly = 1) { shoppingQueryOutputPort.getLowestBrandIdByCategories() }
                verify(exactly = 1) { shoppingQueryOutputPort.getProductOfLowestBrandByBrandId(1L) }
            }
        }
        `when`("최저가격 조회중 예외가 발생하면") {
            `when`("getLowestBrandIdByCategories()에서 예외 발생") {
                then("UseCaseException으로 변환되어야 한다") {
                    every { shoppingQueryOutputPort.getLowestBrandIdByCategories() } throws RuntimeException("DB 연결 오류")

                    val exception =
                        shouldThrow<UseCaseException> {
                            shoppingQueryUseCase.loadBrandLowestPriceContent()
                        }

                    exception.message shouldBeEqual LOAD_BRAND_LOWEST_PRICE_EXCEPTION.errorMessage
                }
            }
            `when`("getProductOfLowestBrandByBrandId()에서 예외 발생") {
                then("UseCaseException으로 변환되어야 한다") {
                    every { shoppingQueryOutputPort.getLowestBrandIdByCategories() } returns 1L
                    every {
                        shoppingQueryOutputPort.getProductOfLowestBrandByBrandId(1L)
                    } throws RuntimeException()

                    val exception =
                        shouldThrow<UseCaseException> {
                            shoppingQueryUseCase.loadBrandLowestPriceContent()
                        }

                    exception.message shouldBeEqual LOAD_BRAND_LOWEST_PRICE_EXCEPTION.errorMessage
                }
            }
        }
    }
    given("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회할 때") {
        `when`("카테고리 ID를 가져오면") {
            then("해당 카테고리 ID의 최저,초괴 가격의 브랜드와 상품이 조회된다") {
                val productOfLowestAndHighestPriceBrands = fixtureMonkey.giveMeOne<ProductOfLowestAndHighestPriceBrands>()
                every { shoppingQueryOutputPort.getCategoryByCategoryName(any()) } returns fixtureMonkey.giveMeOne<CategoryWIthId>()
                every { shoppingQueryOutputPort.getLowestAndHighestPriceBrands(any()) } returns productOfLowestAndHighestPriceBrands

                val result = shoppingQueryUseCase.loadLowestAndHighestPriceBrands("상의")
                result shouldBe productOfLowestAndHighestPriceBrands

                verify(exactly = 1) { shoppingQueryOutputPort.getCategoryByCategoryName(any()) }
                verify(exactly = 1) { shoppingQueryOutputPort.getLowestAndHighestPriceBrands(any()) }
            }
        }
        `when`("카테고리 명으로 ID를 찾을 수 없으면") {
            then("카테고리 ID를 찾을 수 없다는 예외 발생") {
                every { shoppingQueryOutputPort.getCategoryByCategoryName(any()) } returns null

                val exception =
                    shouldThrow<IllegalArgumentException> {
                        shoppingQueryUseCase.loadLowestAndHighestPriceBrands("상의")
                    }

                exception.message?.shouldContain(CAN_NOT_FIND_CATEGORY_EXCEPTION.errorMessage)
            }
        }
        `when`("getLowestAndHighestPriceBrands()에서 예외 발생") {
            then("UseCaseException으로 변환되어야 한다") {
                every { shoppingQueryOutputPort.getCategoryByCategoryName(any()) } returns fixtureMonkey.giveMeOne<CategoryWIthId>()
                every {
                    shoppingQueryOutputPort.getLowestAndHighestPriceBrands(any())
                } throws RuntimeException()

                val exception =
                    shouldThrow<UseCaseException> {
                        shoppingQueryUseCase.loadLowestAndHighestPriceBrands("상의")
                    }

                exception.message shouldBeEqual LOAD_LOWEST_HIGHEST_PRICE_BRANDS_EXCEPTION.errorMessage
            }
        }
    }
})
