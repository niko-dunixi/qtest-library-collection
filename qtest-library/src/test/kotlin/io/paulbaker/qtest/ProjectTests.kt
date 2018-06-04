package io.paulbaker.qtest

import io.kotlintest.inspectors.forAll
import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.matchers.gt
import io.kotlintest.matchers.string.shouldNotBeBlank
import io.kotlintest.provided.testableQTestClient
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec

class ProjectTests : BehaviorSpec({
    Given("Valid user credentials") {
        val qTestClient = testableQTestClient()
        val projectClient = qTestClient.projectClient()
        val projects = projectClient.projects()
        When("I request the projects") {
            Then("I get a list of all projects") {
                projects.shouldNotBeEmpty()
                projects.forAll {
                    it shouldNotBe null
                    it.id shouldBe gt(0L)
                    it.name.shouldNotBeBlank()
                }
            }
            Then("It should be equivalent to requesting each individually") {
                projects.forAll {
                    val projectFromId = projectClient.fromId(it.id)
                    it shouldBe projectFromId
                }
            }
        }
    }
})
