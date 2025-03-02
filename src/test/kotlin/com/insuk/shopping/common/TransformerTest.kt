package com.insuk.shopping.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual

internal class TransformerTest : FunSpec({
    test("Source transform Target") {
        val source =
            Source(
                name = "name",
                age = 35,
            )

        val target =
            object : Transformer<Source, Target>(
                Source::class,
                Target::class,
                param =
                mapOf(
                    "ageStr" to source.age.toString(),
                ),
            ) {}.transform(source)

        target.name shouldBeEqual "name"
        target.ageStr shouldBeEqual "35"
    }
}) {
    internal data class Source(
        val name: String,
        val age: Int,
    )

    internal data class Target(
        val name: String,
        val ageStr: String,
    )
}
