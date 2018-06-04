package io.kotlintest.provided

//import io.kotlintest.AbstractProjectConfig
import io.paulbaker.qtest.Project
import io.paulbaker.qtest.QTestClient

val QTestSubDomain: String = System.getenv("QTEST_SUBDOMAIN")
val QTestCredentials: Pair<String, String> = Pair(System.getenv("QTEST_USER"), System.getenv("QTEST_PASS"))

fun testableQTestClient(): QTestClient {
    return QTestClient(QTestSubDomain, QTestCredentials)
}

fun randomUUID(): String = java.util.UUID.randomUUID().toString()

/**
 * We can't delete projects, thus we cannot use an ephemeral test-project.
 * Let's just re-use the same dedicated mock project every time.
 */
fun getTestProject(): Project = createOrFindProject("mock-automation-project")

/**
 * If the project doesn't exist by name, create it. Otherwise return it.
 */
fun createOrFindProject(name: String): Project {
    val projectClient = testableQTestClient().projectClient()
    val namedProject = projectClient.projects().firstOrNull { it.name == name }
    if (namedProject != null) {
        return namedProject
    }
    return projectClient.create(name)
}

//@Suppress("unused")
//object ProjectConfig : AbstractProjectConfig() {
//
//    private var started: Long = 0
//
//    override fun parallelism(): Int = 4
//
//    override fun beforeAll() {
//        started = System.currentTimeMillis()
//    }
//
//    override fun afterAll() {
//        val time = System.currentTimeMillis() - started
//        println("overall time [ms]: $time")
//    }
//}