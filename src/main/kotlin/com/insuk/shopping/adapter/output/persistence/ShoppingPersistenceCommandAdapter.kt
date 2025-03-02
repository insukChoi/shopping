package com.insuk.shopping.adapter.output.persistence

import com.insuk.shopping.adapter.output.persistence.entity.BrandEntity
import com.insuk.shopping.adapter.output.persistence.entity.CategoryEntity
import com.insuk.shopping.adapter.output.persistence.entity.ProductEntity
import com.insuk.shopping.application.port.output.*
import com.insuk.shopping.config.SHOPPING_TRANSACTION_MANAGER
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@PersistenceAdapter
@Transactional(transactionManager = SHOPPING_TRANSACTION_MANAGER)
class ShoppingPersistenceCommandAdapter : ShoppingCommandOutputPort {
    override fun addBrand(brandName: String): BrandWithId {
        val brandId = BrandEntity.insertAndGetId {
            it[name] = brandName
        }.value

        return BrandEntity
            .selectAll()
            .where {
                BrandEntity.id eq brandId
            }
            .map {
                BrandWithId(
                    id = brandId,
                    name = it[BrandEntity.name],
                    createdAt = it[BrandEntity.createdAt],
                    updatedAt = it[BrandEntity.updatedAt],
                )
            }
            .first()
    }

    override fun addCategory(categoryName: String): CategoryWIthId {
        val categoryId = CategoryEntity.insertAndGetId {
            it[name] = categoryName
        }.value

        return CategoryEntity
            .selectAll()
            .where {
                CategoryEntity.id eq categoryId
            }
            .map {
                CategoryWIthId(
                    id = categoryId,
                    name = it[CategoryEntity.name],
                    createdAt = it[CategoryEntity.createdAt],
                    updatedAt = it[CategoryEntity.updatedAt],
                )
            }
            .first()
    }

    override fun addProduct(
        price: BigDecimal,
        brandId: BrandId,
        categoryId: CategoryId,
    ): ProductWithId {
        val productId = ProductEntity.insertAndGetId {
            it[ProductEntity.price] = price
            it[ProductEntity.brandId] = brandId
            it[ProductEntity.categoryId] = categoryId
        }.value

        return ProductEntity
            .selectAll()
            .where {
                ProductEntity.id eq productId
            }
            .map {
                ProductWithId(
                    id = productId,
                    price = it[ProductEntity.price],
                    createdAt = it[ProductEntity.createdAt],
                    updatedAt = it[ProductEntity.updatedAt],
                )
            }
            .first()
    }

    override fun modifyProduct(
        price: BigDecimal,
        brandId: BrandId,
        categoryId: CategoryId,
    ): ProductWithId? {
        val productId = ProductEntity
            .selectAll()
            .where {
                (ProductEntity.brandId eq brandId) and
                    (ProductEntity.categoryId eq categoryId)
            }
            .map { it[ProductEntity.id].value }
            .firstOrNull()

        return productId?.let {
            ProductEntity.update({
                ProductEntity.id eq productId
            }) {
                it[ProductEntity.price] = price
            }

            ProductEntity
                .selectAll()
                .where { ProductEntity.id eq productId }
                .map {
                    ProductWithId(
                        id = productId,
                        price = it[ProductEntity.price],
                        createdAt = it[ProductEntity.createdAt],
                        updatedAt = it[ProductEntity.updatedAt],
                    )
                }
                .firstOrNull()
        }
    }

    override fun removeProduct(brandId: BrandId, categoryId: CategoryId): Boolean {
        val deleteCount = ProductEntity.deleteWhere {
            (ProductEntity.brandId eq brandId) and
                (ProductEntity.categoryId eq categoryId)
        }
        return deleteCount > 0
    }
}
