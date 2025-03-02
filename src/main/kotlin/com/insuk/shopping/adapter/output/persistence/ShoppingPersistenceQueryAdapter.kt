package com.insuk.shopping.adapter.output.persistence

import com.insuk.shopping.adapter.output.persistence.entity.BrandEntity
import com.insuk.shopping.adapter.output.persistence.entity.CategoryEntity
import com.insuk.shopping.adapter.output.persistence.entity.ProductEntity
import com.insuk.shopping.application.domain.model.*
import com.insuk.shopping.application.domain.model.ProductOfBrandLowest.CategoryOfBrandLowest
import com.insuk.shopping.application.port.output.*
import com.insuk.shopping.config.SHOPPING_TRANSACTION_MANAGER
import org.jetbrains.exposed.sql.*
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@PersistenceAdapter
@Transactional(transactionManager = SHOPPING_TRANSACTION_MANAGER, readOnly = true)
class ShoppingPersistenceQueryAdapter : ShoppingQueryOutputPort {
    // 카테고리별 최저 가격인 카테코리 ID 와 최저가격 조회
    override fun getCategoryMinPrices(): List<Pair<Long, BigDecimal>> {
        return ProductEntity
            .select(ProductEntity.categoryId, ProductEntity.price.min())
            .groupBy(ProductEntity.categoryId)
            .map { Pair(it[ProductEntity.categoryId], it[ProductEntity.price.min()] ?: BigDecimal.ZERO) }
    }

    // 최저가격인 카테고리와 금액으로 가장 최저가격인 제품 1개 조회
    override fun getProductByCategoryAndLowestPrice(
        categoryId: Long,
        lowestPrice: BigDecimal,
    ): Product {
        return productJoinBrandJoinCategory()
            .selectAll()
            .where {
                (ProductEntity.categoryId eq categoryId) and
                    (ProductEntity.price eq lowestPrice)
            }
            .orderBy(BrandEntity.id, SortOrder.DESC)
            .limit(1)
            .map {
                Product(
                    price = it[ProductEntity.price],
                    createdAt = it[ProductEntity.createdAt],
                    updatedAt = it[ProductEntity.updatedAt],
                    brand = it.toBrand(),
                    category = it.toCategory(),
                )
            }.first()
    }

    // 카테고리별 총 금액이 가장 저렴한 단일 브랜드 ID 조회
    override fun getLowestBrandIdByCategories(): Long {
        return ProductEntity
            .select(ProductEntity.brandId, ProductEntity.price.sum())
            .groupBy(ProductEntity.brandId)
            .orderBy(ProductEntity.price.sum(), SortOrder.ASC)
            .limit(1)
            .map {
                it[ProductEntity.brandId]
            }
            .first()
    }

    // 가장 저렴한 브랜드 ID로 모든 카테고리와 금액 조회
    override fun getProductOfLowestBrandByBrandId(brandId: Long): ProductOfBrandLowest {
        val pairs: List<Pair<String, CategoryOfBrandLowest>> = productJoinBrandJoinCategory()
            .selectAll()
            .where {
                ProductEntity.brandId eq brandId
            }
            .orderBy(CategoryEntity.id, SortOrder.ASC)
            .map {
                it[BrandEntity.name] to CategoryOfBrandLowest(
                    categoryName = it[CategoryEntity.name],
                    price = it[ProductEntity.price],
                )
            }

        return ProductOfBrandLowest(
            brandName = pairs.first().first,
            categories = pairs.map { it.second },
        )
    }

    // 카테고리 명으로 카테고리 ID 조회
    override fun getCategoryByCategoryName(categoryName: String): CategoryWIthId? {
        return CategoryEntity
            .selectAll()
            .where {
                CategoryEntity.name eq categoryName
            }
            .map {
                CategoryWIthId(
                    id = it[CategoryEntity.id].value,
                    name = it[CategoryEntity.name],
                    createdAt = it[CategoryEntity.createdAt],
                    updatedAt = it[CategoryEntity.updatedAt],
                )
            }.firstOrNull()
    }

    // 브랜드 명으로 브랜드 ID 조회
    override fun getBrandByBrandName(brandName: String): BrandWithId? {
        return BrandEntity
            .selectAll()
            .where {
                BrandEntity.name eq brandName
            }
            .map {
                BrandWithId(
                    id = it[BrandEntity.id].value,
                    name = it[BrandEntity.name],
                    createdAt = it[BrandEntity.createdAt],
                    updatedAt = it[BrandEntity.updatedAt],
                )
            }
            .firstOrNull()
    }

    // 카테고리 ID로 최저, 최고 가격 브랜드와 상품 가격 조회
    override fun getLowestAndHighestPriceBrands(categoryId: Long): ProductOfLowestAndHighestPriceBrands {
        return ProductOfLowestAndHighestPriceBrands(
            lowestBrandWithPrice = findProductByCategoryAndSortOrder(categoryId, SortOrder.ASC),
            highestBrandWithPrice = findProductByCategoryAndSortOrder(categoryId, SortOrder.DESC),
        )
    }

    private fun findProductByCategoryAndSortOrder(categoryId: Long, order: SortOrder): BrandWithPrice? {
        return productJoinBrandJoinCategory()
            .select(
                BrandEntity.name,
                ProductEntity.price,
            )
            .where {
                ProductEntity.categoryId eq categoryId
            }
            .orderBy(ProductEntity.price, order)
            .limit(1)
            .map {
                BrandWithPrice(
                    brandName = it[BrandEntity.name],
                    price = it[ProductEntity.price],
                )
            }.firstOrNull()
    }

    private fun productJoinBrandJoinCategory() = ProductEntity
        .join(
            otherTable = BrandEntity,
            joinType = JoinType.INNER,
            additionalConstraint = {
                ProductEntity.brandId eq BrandEntity.id
            },
        )
        .join(
            otherTable = CategoryEntity,
            joinType = JoinType.INNER,
            additionalConstraint = {
                ProductEntity.categoryId eq CategoryEntity.id
            },
        )

    private fun ResultRow.toBrand(): Brand {
        return Brand(
            name = this[BrandEntity.name],
            createdAt = this[BrandEntity.createdAt],
            updatedAt = this[BrandEntity.updatedAt],
        )
    }

    private fun ResultRow.toCategory(): Category {
        return Category(
            name = this[CategoryEntity.name],
            createdAt = this[CategoryEntity.createdAt],
            updatedAt = this[CategoryEntity.updatedAt],
        )
    }
}
