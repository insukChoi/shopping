package com.insuk.shopping.application.port.input

import com.insuk.shopping.application.domain.model.Product

interface ShoppingCommandInputPort {
    fun addProduct(command: ProductCommand): Product
    fun updateProduct(command: ProductCommand): Product?
    fun deleteProduct(command: ProductDeleteCommand)
}
