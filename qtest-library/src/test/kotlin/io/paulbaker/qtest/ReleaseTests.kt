package io.paulbaker.qtest

import io.kotlintest.provided.getTestProject
import io.kotlintest.provided.randomUUID
import io.kotlintest.provided.testableQTestClient
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsEmptyCollection.empty
import org.hamcrest.text.IsEmptyString.emptyOrNullString
import org.junit.jupiter.api.Test

class ReleaseTests {

    @Test
    fun testCreateRelease() {
        val testProject = getTestProject()
        val releaseClient = testableQTestClient().releaseClient(testProject.id)
        val releaseName = randomUUID()
        val release = releaseClient.create(releaseName)
        assertThat(release.id, greaterThan(0L))
        assertThat(release.name, `is`(releaseName))
    }

    @Test
    fun testDeleteRelease() {
        val testProject = getTestProject()
        val releaseClient = testableQTestClient().releaseClient(testProject.id)
        val releaseName = randomUUID()
        val release = releaseClient.create(releaseName)
        assert(releaseClient.delete(release.id), { "Couldn't delete release." })
        assert(!releaseClient.delete(release.id), { "Shouldn't be able to delete twice." })
    }

    @Test
    fun testGetAllRequirements() {
        val testProject = getTestProject()
        val releaseClient = testableQTestClient().releaseClient(testProject.id)
        val releases = releaseClient.releases()
        assertThat(releases, not(empty()))
        releases.forEach { release ->
            assertThat(release.id, greaterThan(0L))
            assertThat(release.name, not(emptyOrNullString()))
        }
    }

    @Test
    fun testGetSingleRequirement() {
        val testProject = getTestProject()
        val releaseClient = testableQTestClient().releaseClient(testProject.id)
        val releases = releaseClient.releases()
        assertThat(releases, not(empty()))
        releases.forEach { release ->
            assertThat(release.id, greaterThan(0L))
            assertThat(release.name, not(emptyOrNullString()))
            val releaseFromId = releaseClient.fromId(release.id)
            assertThat(release, `is`(releaseFromId))
        }
    }
}