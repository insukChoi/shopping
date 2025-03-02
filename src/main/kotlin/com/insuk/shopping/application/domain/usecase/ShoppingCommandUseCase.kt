package com.insuk.shopping.application.domain.usecase

import com.insuk.shopping.application.domain.mapper.toDomain
import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.port.input.ProductCommand
import com.insuk.shopping.application.port.input.ProductDeleteCommand
import com.insuk.shopping.application.port.input.ShoppingCommandInputPort
import com.insuk.shopping.application.port.output.ShoppingCommandOutputPort
import com.insuk.shopping.application.port.output.ShoppingQueryOutputPort
import com.insuk.shopping.config.SHOPPING_TRANSACTION_MANAGER
import com.insuk.shopping.exception.UseCaseErrorMessage.*
import com.insuk.shopping.exception.UseCaseException
import org.springframework.transaction.annotation.Transactional

@UseCase
@Transactional(transactionManager = SHOPPING_TRANSACTION_MANAGER)
class ShoppingCommandUseCase(
    private val shoppingCommandOutputPort: ShoppingCommandOutputPort,
    private val shoppingQueryOutputPort: ShoppingQueryOutputPort,
) : ShoppingCommandInputPort {
    override fun addProduct(command: ProductCommand): Product = runCatching {
        val brandWithId = shoppingCommandOutputPort.addBrand(command.brandName)
        val categoryWithId = shoppingCommandOutputPort.addCategory(command.categoryName)

        val productWithId = shoppingCommandOutputPort.addProduct(
            price = command.price,
            brandId = brandWithId.id,
            categoryId = categoryWithId.id,
        )

        productWithId.toDomain(
            brand = brandWithId.toDomain(),
            category = categoryWithId.toDomain(),
        )
    }.getOrElse {
        throw UseCaseException(ADD_PRODUCT_EXCEPTION.errorMessage, it)
    }

    override fun updateProduct(command: ProductCommand): Product? {
        val brandWithId = requireNotNull(shoppingQueryOutputPort.getBrandByBrandName(command.brandName)) {
            "${CAN_NOT_FIND_BRAND_EXCEPTION.errorMessage} -> brandName: ${command.brandName}"
        }
        val categoryWithId = requireNotNull(shoppingQueryOutputPort.getCategoryByCategoryName(command.categoryName)) {
            "${CAN_NOT_FIND_CATEGORY_EXCEPTION.errorMessage} -> categoryName: ${command.categoryName}"
        }

        return runCatching {
            val productWithId = shoppingCommandOutputPort.modifyProduct(
                price = command.price,
                brandId = brandWithId.id,
                categoryId = categoryWithId.id,
            )

            productWithId?.toDomain(
                brand = brandWithId.toDomain(),
                category = categoryWithId.toDomain(),
            )
        }.getOrElse {
            throw UseCaseException(UPDATE_PRODUCT_EXCEPTION.errorMessage, it)
        }
    }

    override fun deleteProduct(command: ProductDeleteCommand) {
        val brandWithId = requireNotNull(shoppingQueryOutputPort.getBrandByBrandName(command.brandName)) {
            "${CAN_NOT_FIND_BRAND_EXCEPTION.errorMessage} -> brandName: ${command.brandName}"
        }
        val categoryWithId = requireNotNull(shoppingQueryOutputPort.getCategoryByCategoryName(command.categoryName)) {
            "${CAN_NOT_FIND_CATEGORY_EXCEPTION.errorMessage} -> categoryName: ${command.categoryName}"
        }

        runCatching {
            shoppingCommandOutputPort.removeProduct(
                brandId = brandWithId.id,
                categoryId = categoryWithId.id,
            )
        }.getOrElse {
            throw UseCaseException(DELETE_PRODUCT_EXCEPTION.errorMessage, it)
        }
    }
}
