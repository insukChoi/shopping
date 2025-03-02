package com.insuk.shopping.adapter.output.persistence.entity

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime.now

object ProductEntity : LongIdTable("product", "product_id") {
    val price = decimal("price", 19, 4)
    val brandId = long("brand_id")
    val categoryId = long("category_id")
    val createdAt = datetime("created_at").clientDefault { now() }
    val updatedAt = datetime("updated_at").clientDefault { now() }
}
