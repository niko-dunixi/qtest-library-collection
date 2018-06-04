package io.paulbaker.qtest

import io.kotlintest.provided.getTestProject
import io.kotlintest.provided.randomUUID
import io.kotlintest.provided.testableQTestClient
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

class TestCycleTests {

    private val testableQTestClient = testableQTestClient()

    @Test
    fun testCreate() {
        val project = getTestProject()
        val testCycleClient = testableQTestClient.testCycleClient(project.id)
        val createdTestCycle = testCycleClient.create(randomUUID())
        assertThat(createdTestCycle, notNullValue())
        assert(testCycleClient.delete(createdTestCycle.id), { "Couldn't delete the test-cycle: ${createdTestCycle.name} - ${createdTestCycle.id}" })
        assert(!testCycleClient.delete(createdTestCycle.id), { "We shouldn't be able to delete the test-cycle twice: ${createdTestCycle.name} - ${createdTestCycle.id}" })
    }
}