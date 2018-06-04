package io.paulbaker.qtest

import io.kotlintest.provided.getTestProject
import io.kotlintest.provided.testableQTestClient
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsEmptyCollection.empty
import org.hamcrest.text.IsEmptyString.emptyOrNullString
import org.junit.jupiter.api.Test

class UserTests {

    @Test
    fun testGetUsers() {
        val testableQTestClient = testableQTestClient()
        val projectId = getTestProject().id
        val projectClient = testableQTestClient.projectClient()
        val users = projectClient.users(projectId)
        assertThat(users, not(empty()))

        val userClient = testableQTestClient.userClient()
        users.forEach { user ->
            assertThat(user.id, greaterThan(0L))
            assertThat(user.username, not(emptyOrNullString()))
            val userFromId = userClient.fromId(user.id)
            assertThat(userFromId, `is`(user))
        }
    }
}