package com.insuk.shopping.application.domain.usecase

import com.insuk.shopping.application.domain.model.Product
import com.insuk.shopping.application.domain.model.toProduct
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
        val (brandId, brand) = shoppingCommandOutputPort.addBrand(command.brandName)
        val (categoryId, category) = shoppingCommandOutputPort.addCategory(command.categoryName)

        val (_, productOnly) = shoppingCommandOutputPort.addProduct(
            price = command.price,
            brandId = brandId,
            categoryId = categoryId,
        )

        productOnly.toProduct(
            brand = brand,
            category = category,
        )
    }.getOrElse {
        throw UseCaseException(ADD_PRODUCT_EXCEPTION.errorMessage, it)
    }

    override fun updateProduct(command: ProductCommand): Product? {
        val (brandId, brand) = shoppingQueryOutputPort.getBrandByBrandName(command.brandName)
            .requireNotNullOrThrow("${CAN_NOT_FIND_BRAND_EXCEPTION.errorMessage} -> brandName: ${command.brandName}")

        val (categoryId, category) = shoppingQueryOutputPort.getCategoryByCategoryName(command.categoryName)
            .requireNotNullOrThrow("${CAN_NOT_FIND_CATEGORY_EXCEPTION.errorMessage} -> categoryName: ${command.categoryName}")

        return runCatching {
            val (_, productOnly) = shoppingCommandOutputPort.modifyProduct(
                price = command.price,
                brandId = brandId,
                categoryId = categoryId,
            )

            productOnly?.toProduct(
                brand = brand,
                category = category,
            )
        }.getOrElse {
            throw UseCaseException(UPDATE_PRODUCT_EXCEPTION.errorMessage, it)
        }
    }

    override fun deleteProduct(command: ProductDeleteCommand) {
        val (brandId, _) = shoppingQueryOutputPort.getBrandByBrandName(command.brandName)
            .requireNotNullOrThrow("${CAN_NOT_FIND_BRAND_EXCEPTION.errorMessage} -> brandName: ${command.brandName}")

        val (categoryId, _) = shoppingQueryOutputPort.getCategoryByCategoryName(command.categoryName)
            .requireNotNullOrThrow("${CAN_NOT_FIND_CATEGORY_EXCEPTION.errorMessage} -> categoryName: ${command.categoryName}")

        runCatching {
            shoppingCommandOutputPort.removeProduct(
                brandId = brandId,
                categoryId = categoryId,
            )
        }.getOrElse {
            throw UseCaseException(DELETE_PRODUCT_EXCEPTION.errorMessage, it)
        }
    }

    private fun <T> Pair<Long?, T?>.requireNotNullOrThrow(exceptionMessage: String): Pair<Long, T> {
        val (id, entity) = this
        requireNotNull(id) { exceptionMessage }
        requireNotNull(entity) { exceptionMessage }
        return id to entity
    }
}
