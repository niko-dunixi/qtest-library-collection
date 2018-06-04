package io.paulbaker.qtest

import io.kotlintest.matchers.collections.shouldNotBeEmpty
import io.kotlintest.matchers.string.shouldNotBeEmpty
import io.kotlintest.provided.randomUUID
import io.kotlintest.provided.testableQTestClient
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec

class ReleaseTests : BehaviorSpec({
    Given("I have a qtest project") {
        val testableQTestClient = testableQTestClient()
        val projectClient = testableQTestClient.projectClient()
        val project = projectClient.fromId(49099)
        val releaseClient = testableQTestClient.releaseClient(project.id)
        When("I request all requirements") {
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
                    val releaseFromId = releaseClient.fromId(it.id)
                    it shouldBe releaseFromId
                })
            }
        }
        When("I create a new release") {
            val releaseName = randomUUID()
//            val releaseName = "${System.currentTimeMillis()}"
            val createdRelease = releaseClient.create(releaseName)
            Then("I can delete the release I created") {
                releaseClient.delete(createdRelease.id) shouldBe true
            }
            Then("I can't delete the release I created twice") {
                releaseClient.delete(createdRelease.id) shouldNotBe true
            }
        }
    }
})