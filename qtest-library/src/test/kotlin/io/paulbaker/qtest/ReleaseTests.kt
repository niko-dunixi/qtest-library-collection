package io.paulbaker.qtest

import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.matchers.string.shouldNotBeEmpty
import io.kotlintest.provided.testableQTestClient
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec

class ReleaseTests : BehaviorSpec({
    Given("I have a qtest project") {
        val testableQTestClient = testableQTestClient()
        val projectClient = testableQTestClient.projectClient()
        val project = projectClient.fromId(49099)
        When("I request all requirements") {
            val releaseClient = testableQTestClient.releaseClient(project.id)
            val releases = releaseClient.releases()
            Then("I get all requirements") {
                releases.shouldNotBeEmpty()
                releases.forEach({
                    it shouldNotBe null
                    it.name.shouldNotBeEmpty()
                })
            }
            Then("It should be equivalent to requesting each individually") {
                releases.forEach({
                    val releaseFromId = releaseClient.release(it.id)
                    it shouldBe releaseFromId
                })
            }
        }
    }
})