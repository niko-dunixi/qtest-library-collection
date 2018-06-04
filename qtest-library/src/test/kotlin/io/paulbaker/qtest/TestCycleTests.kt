package io.paulbaker.qtest

import io.kotlintest.provided.getTestProject
import io.kotlintest.provided.randomUUID
import io.kotlintest.provided.testableQTestClient
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsEmptyCollection.empty
import org.junit.jupiter.api.Test

class TestCycleTests {

    private val testableQTestClient = testableQTestClient()

    @Test
    fun testGetAllTestCycles() {
        val testProject = getTestProject()
        val releaseClient = testableQTestClient.releaseClient(testProject.id)
        val releases = releaseClient.releases()
        assertThat(releases, not(empty()))
        releases.forEach { release ->
            assertThat(release.id, greaterThan(0L))
            assertThat(release.name, not(emptyOrNullString()))
        }
    }

    @Test
    fun testGetIndividualTestCycles() {
        val testProject = getTestProject()
        val releaseClient = testableQTestClient.releaseClient(testProject.id)
        val releases = releaseClient.releases()
        assertThat(releases, not(empty()))
        releases.forEach { release ->
            val releaseFromId = releaseClient.fromId(release.id)
            assertThat(releaseFromId, `is`(release))
        }
    }

    @Test
    fun testCreate() {
        val project = getTestProject()
        val testCycleClient = testableQTestClient.testCycleClient(project.id)
        val name = randomUUID()
        val createdTestCycle = testCycleClient.create(name)
        assertThat(createdTestCycle.id, greaterThan(0L))
        assertThat(createdTestCycle.name, `is`(name))
        assert(testCycleClient.delete(createdTestCycle.id), { "Couldn't delete the test-cycle: ${createdTestCycle.name} - ${createdTestCycle.id}" })
        assert(!testCycleClient.delete(createdTestCycle.id), { "We shouldn't be able to delete the test-cycle twice: ${createdTestCycle.name} - ${createdTestCycle.id}" })
    }
}