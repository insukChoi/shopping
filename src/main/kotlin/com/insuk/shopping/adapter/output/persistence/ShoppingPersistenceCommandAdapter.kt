package com.insuk.shopping.adapter.output.persistence

import com.insuk.shopping.adapter.output.persistence.entity.BrandEntity
import com.insuk.shopping.adapter.output.persistence.entity.CategoryEntity
import com.insuk.shopping.adapter.output.persistence.entity.ProductEntity
import com.insuk.shopping.application.domain.model.Brand
import com.insuk.shopping.application.domain.model.Category
import com.insuk.shopping.application.domain.model.ProductOnly
import com.insuk.shopping.application.port.output.BrandId
import com.insuk.shopping.application.port.output.CategoryId
import com.insuk.shopping.application.port.output.ProductId
import com.insuk.shopping.application.port.output.ShoppingCommandOutputPort
import com.insuk.shopping.config.SHOPPING_TRANSACTION_MANAGER
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@PersistenceAdapter
@Transactional(transactionManager = SHOPPING_TRANSACTION_MANAGER)
class ShoppingPersistenceCommandAdapter : ShoppingCommandOutputPort {
    override fun addBrand(brandName: String): Pair<BrandId, Brand> {
        val brandId = BrandEntity.insertAndGetId {
            it[name] = brandName
        }.value

        val brand = BrandEntity
            .selectAll()
            .where {
                BrandEntity.id eq brandId
            }
            .map {
                Brand(
                    name = it[BrandEntity.name],
                    createdAt = it[BrandEntity.createdAt],
                    updatedAt = it[BrandEntity.updatedAt],
                )
            }
            .first()

        return brandId to brand
    }

    override fun addCategory(categoryName: String): Pair<CategoryId, Category> {
        val categoryId = CategoryEntity.insertAndGetId {
            it[name] = categoryName
        }.value

        val category = CategoryEntity
            .selectAll()
            .where {
                CategoryEntity.id eq categoryId
            }
            .map {
                Category(
                    name = it[CategoryEntity.name],
                    createdAt = it[CategoryEntity.createdAt],
                    updatedAt = it[CategoryEntity.updatedAt],
                )
            }
            .first()

        return categoryId to category
    }

    override fun addProduct(
        price: BigDecimal,
        brandId: BrandId,
        categoryId: CategoryId,
    ): Pair<ProductId, ProductOnly> {
        val productId = ProductEntity.insertAndGetId {
            it[ProductEntity.price] = price
            it[ProductEntity.brandId] = brandId
            it[ProductEntity.categoryId] = categoryId
        }.value

        val productOnly = ProductEntity
            .selectAll()
            .where {
                ProductEntity.id eq productId
            }
            .map {
                ProductOnly(
                    price = it[ProductEntity.price],
                    createdAt = it[ProductEntity.createdAt],
                    updatedAt = it[ProductEntity.updatedAt],
                )
            }
            .first()

        return productId to productOnly
    }

    override fun modifyProduct(
        price: BigDecimal,
        brandId: BrandId,
        categoryId: CategoryId,
    ): Pair<ProductId?, ProductOnly?> {
        val productId = ProductEntity
            .selectAll()
            .where {
                (ProductEntity.brandId eq brandId) and
                    (ProductEntity.categoryId eq categoryId)
            }
            .map { it[ProductEntity.id].value }
            .firstOrNull()

        productId?.let {
            ProductEntity.update({
                ProductEntity.id eq productId
            }) {
                it[ProductEntity.price] = price
            }
        }

        val updatedProduct = ProductEntity
            .selectAll()
            .where { ProductEntity.id eq productId }
            .map {
                ProductOnly(
                    price = it[ProductEntity.price],
                    createdAt = it[ProductEntity.createdAt],
                    updatedAt = it[ProductEntity.updatedAt],
                )
            }
            .firstOrNull()

        return productId to updatedProduct
    }

    override fun removeProduct(brandId: BrandId, categoryId: CategoryId): Boolean {
        val deleteCount = ProductEntity.deleteWhere {
            (ProductEntity.brandId eq brandId) and
                (ProductEntity.categoryId eq categoryId)
        }
        return deleteCount > 0
    }
}
