package io.paulbaker.qtest

import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.string.shouldMatch
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.BehaviorSpec

class LoginTests : BehaviorSpec({
    Given("Valid credentials") {
        When("I request a token") {
            Then("I should get a valid token") {
                val loginTokenSupplier = LoginTokenSupplier("yourco", "user@yourco.com", "t3st-password")

                val token = loginTokenSupplier.get()
                token shouldNotBe null
                token.accessToken shouldNotBe null
                token.accessToken!!.shouldMatch("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}")
                token.tokenType shouldBe "bearer"
                token.scope shouldNotBe null
                token.scope.shouldContainAll(listOf(
                        "read",
                        "create",
                        "write",
                        "delete"
                ))
            }
        }
    }
})
