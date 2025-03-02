package com.insuk.shopping.adapter.output.persistence.entity

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime.now

object CategoryEntity : LongIdTable("category", "category_id") {
    val name = varchar("category_name", 100)
    val createdAt = datetime("created_at").clientDefault { now() }
    val updatedAt = datetime("updated_at").clientDefault { now() }
}
