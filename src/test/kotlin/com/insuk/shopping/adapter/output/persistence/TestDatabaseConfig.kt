package com.insuk.shopping.adapter.output.persistence

import com.insuk.shopping.adapter.output.persistence.entity.BrandEntity
import com.insuk.shopping.adapter.output.persistence.entity.CategoryEntity
import com.insuk.shopping.adapter.output.persistence.entity.ProductEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object TestDatabaseConfig {
    lateinit var database: Database
    private val tables = arrayOf(BrandEntity, CategoryEntity, ProductEntity)

    fun initDatabase() {
        if (!this::database.isInitialized) {
            database =
                Database.connect(
                    url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;",
                    driver = "org.h2.Driver",
                    user = "sa",
                    password = "",
                )

            transaction(database) {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(*tables)
            }
        }
    }

    fun clearDatabase() {
        if (this::database.isInitialized) {
            transaction(database) {
                addLogger(StdOutSqlLogger)
                SchemaUtils.drop(*tables)
                SchemaUtils.create(*tables)
            }
        }
    }
}
