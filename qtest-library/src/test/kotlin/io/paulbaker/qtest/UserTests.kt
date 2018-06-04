package io.paulbaker.qtest

//import io.kotlintest.inspectors.forAll
//import io.kotlintest.matchers.collections.shouldNotBeEmpty
//import io.kotlintest.matchers.gt
//import io.kotlintest.provided.testableQTestClient
//import io.kotlintest.shouldBe
//import io.kotlintest.shouldNotBe
//import io.kotlintest.specs.BehaviorSpec
//
//class UserTests : BehaviorSpec({
//    Given("I have a project") {
//        val testableQTestClient = testableQTestClient()
//        val projectClient = testableQTestClient.projectClient()
//        val project = projectClient.projects().first()
//        val userClient = testableQTestClient.userClient()
//        When("I request its list of users") {
//            val usersFromProject = projectClient.users(project.id)
//            Then("I get a full list of users") {
//                usersFromProject.shouldNotBeEmpty()
//                usersFromProject.forAll {
//                    it shouldNotBe null
//                    it.id shouldBe gt(0L)
//                }
//            }
//            Then("It should be equivalent to requesting them individually") {
//                usersFromProject.forEach({
//                    val userFromId = userClient.fromId(it.id)
//                    it shouldBe userFromId
//                })
//            }
//        }
//    }
//})