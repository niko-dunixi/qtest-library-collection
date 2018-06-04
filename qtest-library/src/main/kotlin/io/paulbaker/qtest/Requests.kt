package io.paulbaker.qtest

enum class TestCycleParent(val value: String) {
    ROOT("root"), RELEASE("release"), TEST_CYCLE("test-cycle")
}

enum class TestRunParent(val value: String) {
    ROOT("root"), RELEASE("release"), TEST_CYCLE("test-cycle"), TEST_SUITE("test-suite")
}