package com.insuk.shopping.application.domain.usecase

import com.insuk.shopping.application.domain.model.Brand
import com.insuk.shopping.application.domain.model.Category
import com.insuk.shopping.application.domain.model.ProductOnly
import com.insuk.shopping.application.port.input.ProductCommand
import com.insuk.shopping.application.port.input.ProductDeleteCommand
import com.insuk.shopping.application.port.output.ShoppingCommandOutputPort
import com.insuk.shopping.application.port.output.ShoppingQueryOutputPort
import com.insuk.shopping.exception.UseCaseErrorMessage.CAN_NOT_FIND_BRAND_EXCEPTION
import com.insuk.shopping.exception.UseCaseErrorMessage.CAN_NOT_FIND_CATEGORY_EXCEPTION
import com.insuk.shopping.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

internal class ShoppingCommandUseCaseTest : BehaviorSpec({
    val shoppingCommandOutputPort = mockk<ShoppingCommandOutputPort>()
    val shoppingQueryOutputPort = mockk<ShoppingQueryOutputPort>()
    val shoppingCommandUseCase = ShoppingCommandUseCase(shoppingCommandOutputPort, shoppingQueryOutputPort)

    given("상품이 추가 될 때") {
        val command = fixtureMonkey.giveMeOne<ProductCommand>()
        `when`("브랜드명과 카테고리명 가격이 주어지면") {
            then("브랜드, 카테고리, 상품이 추가된다") {
                every { shoppingCommandOutputPort.addBrand(any()) } returns
                    (1L to fixtureMonkey.giveMeOne<Brand>())
                every { shoppingCommandOutputPort.addCategory(any()) } returns
                    (1L to fixtureMonkey.giveMeOne<Category>())
                every { shoppingCommandOutputPort.addProduct(any(), any(), any()) } returns
                    (1L to fixtureMonkey.giveMeOne<ProductOnly>())

                val result = shoppingCommandUseCase.addProduct(command)

                result.shouldNotBeNull()
                result.brand.shouldNotBeNull()
                result.category.shouldNotBeNull()
            }
        }
    }
    given("상품이 업데이트 될 때") {
        val command = fixtureMonkey.giveMeOne<ProductCommand>()
        `when`("브랜드명과 카테고리명 가격이 주어지면") {
            then("상품 정보가 업데이트 된다") {
                every { shoppingQueryOutputPort.getBrandByBrandName(any()) } returns (1L to fixtureMonkey.giveMeOne<Brand>())
                every { shoppingQueryOutputPort.getCategoryByCategoryName(any()) } returns (1L to fixtureMonkey.giveMeOne<Category>())
                every {
                    shoppingCommandOutputPort.modifyProduct(any(), any(), any())
                } returns (1L to fixtureMonkey.giveMeOne<ProductOnly>())

                val result = shoppingCommandUseCase.updateProduct(command)

                result.shouldNotBeNull()
                result.brand.shouldNotBeNull()
                result.category.shouldNotBeNull()
            }
        }
        `when`("업데이트 하려는 상품이 없으면") {
            then("null 로 return 한다") {
                every { shoppingQueryOutputPort.getBrandByBrandName(any()) } returns (1L to fixtureMonkey.giveMeOne<Brand>())
                every { shoppingQueryOutputPort.getCategoryByCategoryName(any()) } returns (1L to fixtureMonkey.giveMeOne<Category>())
                every {
                    shoppingCommandOutputPort.modifyProduct(any(), any(), any())
                } returns (null to null)

                val result = shoppingCommandUseCase.updateProduct(command)

                result.shouldBeNull()
            }
        }
        `when`("업데이트 하려는 브랜드가 없으면") {
            then("브랜드를 찾을 수 없다는 오류가 발생한다") {
                every { shoppingQueryOutputPort.getBrandByBrandName(any()) } returns (null to null)

                shouldThrow<IllegalArgumentException> {
                    shoppingCommandUseCase.updateProduct(command)
                }.message shouldContain CAN_NOT_FIND_BRAND_EXCEPTION.errorMessage
            }
        }
        `when`("업데이트 하려는 카테고리가 없으면") {
            then("카테고리를 찾을 수 없다는 오류가 발생한다") {
                every { shoppingQueryOutputPort.getBrandByBrandName(any()) } returns (1L to fixtureMonkey.giveMeOne<Brand>())
                every { shoppingQueryOutputPort.getCategoryByCategoryName(any()) } returns (null to null)

                shouldThrow<IllegalArgumentException> {
                    shoppingCommandUseCase.updateProduct(command)
                }.message shouldContain CAN_NOT_FIND_CATEGORY_EXCEPTION.errorMessage
            }
        }
    }
    given("상품을 삭제 할 때") {
        val command = fixtureMonkey.giveMeOne<ProductDeleteCommand>()
        `when`("브랜드명과 카테고리명 가격이 주어지면") {
            then("상품이 삭제 된다") {
                every { shoppingQueryOutputPort.getBrandByBrandName(any()) } returns (1L to fixtureMonkey.giveMeOne<Brand>())
                every { shoppingQueryOutputPort.getCategoryByCategoryName(any()) } returns (1L to fixtureMonkey.giveMeOne<Category>())
                every {
                    shoppingCommandOutputPort.removeProduct(any(), any())
                } returns true

                shoppingCommandUseCase.deleteProduct(command)

                verify(exactly = 1) {
                    shoppingCommandOutputPort.removeProduct(1L, 1L)
                }
            }
        }
    }
})
