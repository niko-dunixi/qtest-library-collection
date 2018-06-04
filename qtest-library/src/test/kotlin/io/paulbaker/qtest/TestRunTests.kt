package io.paulbaker.qtest

import io.kotlintest.provided.getTestProject
import io.kotlintest.provided.randomUUID
import io.kotlintest.provided.testableQTestClient
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.Test

class TestRunTests {

    private val testableQTestClient = testableQTestClient()

    @Test
    fun testCreateRootTestRun() {
        val projectId = getTestProject().id
        val testRunClient = testableQTestClient.testRunClient(projectId)
        val name = "${randomUUID()}-testrun-root"
        val testRun = testRunClient.create(name, 23818835)
        assertThat(testRun.id, greaterThan(0L))
        assertThat(testRun.name, `is`(name))
    }

    @Test
    fun testCreateTestRunNestedInTestCycle() {
        val projectId = getTestProject().id
        val testCycleClient = testableQTestClient.testCycleClient(projectId)
        val testCycle = testCycleClient.create("${randomUUID()}-testcycle-root")

        val testRunClient = testableQTestClient.testRunClient(projectId)
        val name = "${randomUUID()}-testrun-nested-in-testcycle"
        val testRun = testRunClient.create(name, 23818835, TestRunParent.TEST_CYCLE, testCycle.id)
        assertThat(testRun.id, greaterThan(0L))
        assertThat(testRun.name, `is`(name))
    }
}