package com.insuk.shopping.config

import org.jetbrains.exposed.spring.SpringTransactionManager
import org.jetbrains.exposed.sql.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

internal const val SHOPPING_TRANSACTION_MANAGER = "shoppingTransactionManager"

@Configuration
class ExposedConfiguration {
    @Bean
    fun h2Database(dataSource: DataSource) = Database.connect(dataSource)

    @Bean(SHOPPING_TRANSACTION_MANAGER)
    fun transactionManager(dataSource: DataSource) = SpringTransactionManager(dataSource)
}
