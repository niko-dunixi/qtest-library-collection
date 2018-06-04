package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.paulbaker.qtest.QTestClient

val QTestSubDomain: String = System.getenv("QTEST_SUBDOMAIN")
val QTestCredentials: Pair<String, String> = Pair(System.getenv("QTEST_USER"), System.getenv("QTEST_PASS"))

fun testableQTestClient(): QTestClient {
    return QTestClient(QTestSubDomain, QTestCredentials)
}

fun randomUUID(): String = java.util.UUID.randomUUID().toString()

@Suppress("unused")
object ProjectConfig : AbstractProjectConfig() {

    private var started: Long = 0

//    override fun parallelism(): Int = 4

    override fun beforeAll() {
        started = System.currentTimeMillis()
    }

    override fun afterAll() {
        val time = System.currentTimeMillis() - started
        println("overall time [ms]: $time")
    }
}