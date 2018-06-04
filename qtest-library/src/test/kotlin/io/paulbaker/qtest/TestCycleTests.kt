package io.paulbaker.qtest

import io.kotlintest.matchers.gt
import io.kotlintest.provided.randomUUID
import io.kotlintest.provided.testableQTestClient
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec

class TestCycleTests : BehaviorSpec({
    Given("I am in a project") {
        val testableQTestClient = testableQTestClient()
        val projects = testableQTestClient.projectClient().projects()
        val project = projects.first()
        val testCycleClient = testableQTestClient.testCycleClient(project.id)
        When("I create a root level test cycle") {
            val testCycle = testCycleClient.create(randomUUID())
            Then("The creation succeeds") {
                testCycle shouldNotBe null
                testCycle.id shouldBe gt(0L)
            }
            Then("I can delete the test cycle") {
                testCycleClient.delete(testCycle.id) shouldBe true
            }
            Then("I can't delete the test cycle again") {
                testCycleClient.delete(testCycle.id) shouldBe false
            }
        }
    }
})