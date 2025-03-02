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
                val brandWithId = shoppingCommandOutputPort.addBrand("H")
                brandWithId.id.shouldNotBeNull()
                brandWithId.createdAt shouldBeBefore now()
                brandWithId.updatedAt shouldBeBefore now()
            }
        }
    }
    given("카테고리를 추가할 때") {
        `when`("카테고리 명이 주어지면") {
            then("카테고리가 추가 된다") {
                val categoryWithId = shoppingCommandOutputPort.addCategory("시계")
                categoryWithId.id.shouldNotBeNull()
                categoryWithId.createdAt shouldBeBefore now()
                categoryWithId.updatedAt shouldBeBefore now()
            }
        }
    }
    given("상품을 추가할 때") {
        `when`("상품가격과 브랜드, 카테고리가 주어지면") {
            then("상품이 추가 된다") {
                val productWithId = shoppingCommandOutputPort.addProduct(
                    price = 100.toBigDecimal(),
                    brandId = 1L,
                    categoryId = 1L,
                )
                productWithId.id.shouldNotBeNull()
                productWithId.price shouldBeEqualIgnoringScale 100.toBigDecimal()
                productWithId.createdAt shouldBeBefore now()
                productWithId.updatedAt shouldBeBefore now()
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
                val productWithId = shoppingCommandOutputPort.modifyProduct(
                    price = 200.toBigDecimal(),
                    brandId = 1L,
                    categoryId = 1L,
                )

                productWithId?.price?.shouldBeEqualIgnoringScale(200.toBigDecimal())
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
