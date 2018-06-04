package io.paulbaker.qtest

import io.kotlintest.provided.testableQTestClient
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsEmptyCollection.empty
import org.hamcrest.text.IsEmptyString.emptyString
import org.junit.jupiter.api.Test

class ProjectTests {

    private val testableQTestClient = testableQTestClient()

    @Test
    fun testGetAll() {
        val projectClient = testableQTestClient.projectClient()
        val projects = projectClient.projects()
        assertThat(projects, not(empty()))
        projects.forEach({ project ->
            assertThat(project.id, greaterThan(0L))
            assertThat(project.name, not(emptyString()))
        })
    }

    @Test
    fun testGetAllIndividually() {
        val projectClient = testableQTestClient.projectClient()
        val projects = projectClient.projects()
        assertThat(projects, not(empty()))
        projects.forEach({ project ->
            val projectFromId = projectClient.fromId(project.id)
            assertThat(project, `is`(projectFromId))
        })
    }

//    @Test
//    fun testCreateSingleProject() {
//        val projectClient = testableQTestClient.projectClient()
//        val project = projectClient.create("paulbaker-unit-test-${System.currentTimeMillis()}")
//        assert(projectClient.delete(project.id), { "Couldn't delete project" })
//    }
}
