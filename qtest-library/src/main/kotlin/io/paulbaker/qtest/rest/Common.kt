package io.paulbaker.qtest.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

/**
 * Registers the Kotlin module, but we also want the Jdk8 and JavaTime module.
 */
val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(
        Jdk8Module(),
        JavaTimeModule()
)

typealias Item = Pair<String, Any>

fun jsonOf(vararg items: Item): String {
    val mapOfItems = HashMap<String, Any>()
    mapOfItems.putAll(items)
    return jsonOf(mapOfItems)
}

fun jsonOf(items: Map<String, Any>): String = objectMapper.writeValueAsString(items)

fun jsonOf(item: Any): String = objectMapper.writeValueAsString(item)