package com.insuk.shopping.adapter.output.persistence

import com.insuk.shopping.SpringTestContextSupport
import com.insuk.shopping.application.port.output.ShoppingCommandOutputPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.bigdecimal.shouldBeEqualIgnoringScale
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.nulls.shouldNotBeNull
import java.time.LocalDateTime.now

@SpringTestContextSupport
internal class ShoppingPersistenceCommandAdapterTest(
    private val shoppingCommandOutputPort: ShoppingCommandOutputPort,
) : BehaviorSpec({
    given("브랜드를 추가할 때") {
        `when`("브랜드 명이 주어지면") {
            then("브랜드가 추가 된다") {
                val (brandId, brand) = shoppingCommandOutputPort.addBrand("H")
                brandId.shouldNotBeNull()
                brand.createdAt shouldBeBefore now()
                brand.updatedAt shouldBeBefore now()
            }
        }
    }
    given("카테고리를 추가할 때") {
        `when`("카테고리 명이 주어지면") {
            then("카테고리가 추가 된다") {
                val (categoryId, category) = shoppingCommandOutputPort.addCategory("시계")
                categoryId.shouldNotBeNull()
                category.createdAt shouldBeBefore now()
                category.updatedAt shouldBeBefore now()
            }
        }
    }
    given("상품을 추가할 때") {
        `when`("상품가격과 브랜드, 카테고리가 주어지면") {
            then("상품이 추가 된다") {
                val (productId, productOnly) = shoppingCommandOutputPort.addProduct(
                    price = 100.toBigDecimal(),
                    brandId = 1L,
                    categoryId = 1L,
                )
                productId.shouldNotBeNull()
                productOnly.price shouldBeEqualIgnoringScale 100.toBigDecimal()
                productOnly.createdAt shouldBeBefore now()
                productOnly.updatedAt shouldBeBefore now()
            }
        }
    }
    given("상품을 업데이트 할 때") {
        `when`("기존 상품이 있는 상태에서, 상품가격과 브랜드, 카테고리가 주어지면") {
            shoppingCommandOutputPort.addProduct(
                price = 100.toBigDecimal(),
                brandId = 1L,
                categoryId = 1L,
            )
            then("상품이 변경 된다") {
                val (_, modifiedProductOnly) = shoppingCommandOutputPort.modifyProduct(
                    price = 200.toBigDecimal(),
                    brandId = 1L,
                    categoryId = 1L,
                )

                modifiedProductOnly?.price?.shouldBeEqualIgnoringScale(200.toBigDecimal())
            }
        }
    }
    given("상품을 삭제할 때") {
        `when`("기존 상품이 있는 상태에서, 브랜드, 카테고리가 주어지면") {
            shoppingCommandOutputPort.addProduct(
                price = 100.toBigDecimal(),
                brandId = 1L,
                categoryId = 1L,
            )
            then("상품이 삭제 된다") {
                val isDeleted = shoppingCommandOutputPort.removeProduct(
                    brandId = 1L,
                    categoryId = 1L,
                )

                isDeleted.shouldBeTrue()
            }
        }
    }

    beforeSpec {
        TestDatabaseConfig.initDatabase()
    }

    afterSpec { TestDatabaseConfig.clearDatabase() }
})
