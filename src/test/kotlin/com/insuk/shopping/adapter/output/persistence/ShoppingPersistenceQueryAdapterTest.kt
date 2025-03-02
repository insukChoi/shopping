package com.insuk.shopping.adapter.output.persistence

import com.insuk.shopping.SpringTestContextSupport
import com.insuk.shopping.adapter.output.persistence.entity.BrandEntity
import com.insuk.shopping.adapter.output.persistence.entity.CategoryEntity
import com.insuk.shopping.adapter.output.persistence.entity.ProductEntity
import com.insuk.shopping.application.port.output.ShoppingQueryOutputPort
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

@SpringTestContextSupport
class ShoppingPersistenceQueryAdapterTest(
    private val shoppingQueryOutputPort: ShoppingQueryOutputPort,
) : BehaviorSpec({
    given("쇼핑 테이블들이 존재할 때") {
        `when`("카테고리별 최저 가격을 조회하면") {
            then("각 카테고리의 최저 가격이 올바르게 조회된다") {
                transaction(TestDatabaseConfig.database) {
                    val result = shoppingQueryOutputPort.getCategoryMinPrices()

                    result shouldHaveSize 2

                    val category1 = result.find { it.first == 1L }
                    val category2 = result.find { it.first == 2L }

                    category1 shouldNotBe null
                    category2 shouldNotBe null

                    category1!!.second shouldBeEqualIgnoringScale 5000.toBigDecimal()
                    category2!!.second shouldBeEqualIgnoringScale 7000.toBigDecimal()
                }
            }
        }
        `when`("최저 가격인 카테고리 ID와 최저가격으로") {
            then("최저 가격인 제품을 조회한다") {
                transaction(TestDatabaseConfig.database) {
                    val lowestProduct = shoppingQueryOutputPort.getProductByCategoryAndLowestPrice(1L, 5000.toBigDecimal())

                    lowestProduct.brand.name shouldBeEqual "B"
                    lowestProduct.category.name shouldBeEqual "상의"
                }
            }
        }
        `when`("카테고리별 총 금액이 가장 저렴한 단일 브랜드ID를 조회하면") {
            then("가장 저렴한 브랜드 ID가 조회된다") {
                transaction(TestDatabaseConfig.database) {
                    val brandIdByCategories = shoppingQueryOutputPort.getLowestBrandIdByCategories()

                    brandIdByCategories shouldBeEqual 2
                }
            }
        }
        `when`("가장 저렴한 브랜드 ID로 모든 카테고리와 금액 조회하면") {
            then("해당 브랜드ID의 카테고리별 제품이 조회된다") {
                transaction(TestDatabaseConfig.database) {
                    val productOfBrandLowestByBrandId = shoppingQueryOutputPort.getProductOfLowestBrandByBrandId(2)

                    assertSoftly {
                        productOfBrandLowestByBrandId.brandName shouldBeEqual "B"
                        productOfBrandLowestByBrandId.categories shouldHaveSize 2
                        productOfBrandLowestByBrandId.categories[0].categoryName shouldBeEqual "상의"
                        productOfBrandLowestByBrandId.categories[0].price shouldBeEqualIgnoringScale 5000.toBigDecimal()
                        productOfBrandLowestByBrandId.categories[1].categoryName shouldBeEqual "바지"
                        productOfBrandLowestByBrandId.categories[1].price shouldBeEqualIgnoringScale 8000.toBigDecimal()
                    }
                }
            }
        }
        `when`("카테고리 명으로 카테고리 ID 조회하면") {
            then("카테고리 ID가 조회된다") {
                transaction(TestDatabaseConfig.database) {
                    val (categoryId, category) = shoppingQueryOutputPort.getCategoryByCategoryName("상의")

                    categoryId shouldBe 1L
                    category.shouldNotBeNull()
                }
            }
        }
        `when`("카테고리 ID의 최저, 최고 가격 브랜드와 가격을 조회하면") {
            then("최저, 최고 가격의 브랜드와 상품이 조회된다") {
                transaction(TestDatabaseConfig.database) {
                    val lowestAndHighestPriceBrands = shoppingQueryOutputPort.getLowestAndHighestPriceBrands(1L)

                    assertSoftly {
                        lowestAndHighestPriceBrands.lowestBrandWithPrice?.brandName?.shouldBeEqual("B")
                        lowestAndHighestPriceBrands.lowestBrandWithPrice?.price?.shouldBeEqualIgnoringScale(5000.toBigDecimal())
                        lowestAndHighestPriceBrands.highestBrandWithPrice?.brandName?.shouldBeEqual("A")
                        lowestAndHighestPriceBrands.highestBrandWithPrice?.price?.shouldBeEqualIgnoringScale(10000.toBigDecimal())
                    }
                }
            }
        }
        `when`("카테고리 ID에 해당하는 상품이 없으면") {
            then("최저, 최고 가격의 브랜드와 상품이 null로 조회된다") {
                transaction(TestDatabaseConfig.database) {
                    val lowestAndHighestPriceBrands = shoppingQueryOutputPort.getLowestAndHighestPriceBrands(5L)

                    lowestAndHighestPriceBrands.lowestBrandWithPrice.shouldBeNull()
                    lowestAndHighestPriceBrands.highestBrandWithPrice.shouldBeNull()
                }
            }
        }
    }
    given("상품 업데이트할 때") {
        `when`("브랜드 명으로 브랜드 ID 조회하면") {
            then("브랜드 ID가 조회된다") {
                transaction(TestDatabaseConfig.database) {
                    val (brandId, brand) = shoppingQueryOutputPort.getBrandByBrandName("A")

                    brandId shouldBe 1L
                    brand.shouldNotBeNull()
                }
            }
        }
    }

    beforeSpec {
        TestDatabaseConfig.initDatabase()

        transaction(TestDatabaseConfig.database) {
            // 브랜드 데이터 삽입
            BrandEntity.insert {
                it[id] = 1
                it[name] = "A"
            }
            BrandEntity.insert {
                it[id] = 2
                it[name] = "B"
            }

            // 카테고리 데이터 삽입
            CategoryEntity.insert {
                it[id] = 1
                it[name] = "상의"
            }
            CategoryEntity.insert {
                it[id] = 2
                it[name] = "바지"
            }

            // 상품 데이터 삽입
            ProductEntity.insert {
                it[id] = 1
                it[price] = 10000.toBigDecimal()
                it[brandId] = 1
                it[categoryId] = 1
            }
            ProductEntity.insert {
                it[id] = 2
                it[price] = 7000.toBigDecimal()
                it[brandId] = 1
                it[categoryId] = 2
            }
            ProductEntity.insert {
                it[id] = 3
                it[price] = 5000.toBigDecimal()
                it[brandId] = 2
                it[categoryId] = 1
            }
            ProductEntity.insert {
                it[id] = 4
                it[price] = 8000.toBigDecimal()
                it[brandId] = 2
                it[categoryId] = 2
            }
        }
    }

    afterSpec { TestDatabaseConfig.clearDatabase() }
})
