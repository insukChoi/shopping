package com.insuk.shopping.common

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

open class Transformer<in T : Any, out R : Any>(
    inClass: KClass<T>,
    outClass: KClass<R>,
    private val param: Map<String, Any?>? = null,
) {
    private val outConstructor = outClass.primaryConstructor!!
    private val inPropertiesByName by lazy {
        inClass.memberProperties.associateBy { it.name }
    }

    fun transform(data: T): R =
        with(outConstructor) {
            callBy(parameters.associateWith { param -> argFor(param, data) })
        }

    open fun argFor(
        parameter: KParameter,
        data: T,
    ): Any? {
        param?.let {
            if (it.contains(parameter.name)) {
                return it[parameter.name]
            }
        }
        return inPropertiesByName[parameter.name]?.get(data)
    }
}
