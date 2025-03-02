package com.insuk.shopping.application.port.input

import com.insuk.shopping.fixtureMonkey
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.setExp
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

internal class ProductCommandTest : StringSpec({
    val productCommand = fixtureMonkey.giveMeBuilder<ProductCommand>()
        .setExp(ProductCommand::price, 1000.toBigDecimal())
        .sample()

    "가격이 0원이면 예외를 던진다" {
        shouldThrow<IllegalArgumentException> {
            productCommand.copy(price = 0.toBigDecimal())
        }
    }

    "가격이 0보다 작으면 예외를 던진다" {
        shouldThrow<IllegalArgumentException> {
            productCommand.copy(price = (-100).toBigDecimal())
        }
    }

    "가격이 0보다 크면 예외를 던지 않는다" {
        shouldNotThrow<IllegalArgumentException> {
            productCommand.copy(price = 2000.toBigDecimal())
        }
    }
})
