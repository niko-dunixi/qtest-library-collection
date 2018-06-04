package io.paulbaker.qtest

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.paulbaker.qtest.rest.Item
import io.paulbaker.qtest.rest.jsonOf
import io.paulbaker.qtest.rest.objectMapper
import okhttp3.*
import java.util.*

class QTestClient(private val qTestSubDomain: String, credentials: Pair<String, String>, okHttpClient: OkHttpClient) {
    constructor(qTestSubDomain: String, credentials: Pair<String, String>) : this(qTestSubDomain, credentials, OkHttpClient().newBuilder().build())

    private val host: String = "https://$qTestSubDomain.qtestnet.com"
    private val okHttpClient: OkHttpClient = authorizeOkHttpClient(host, okHttpClient, credentials)

    fun projectClient(): ProjectClient = ProjectClient(okHttpClient, host)

    fun releaseClient(projectId: Long): ReleaseClient = ReleaseClient(okHttpClient, host, projectId)

    fun userClient(): UserClient = UserClient(okHttpClient, host)

    /**
     * @see <a href="https://api.qasymphony.com/#/login/postAccessToken">qTest API</a>
     */
    private fun authorizeOkHttpClient(host: String, okHttpClient: OkHttpClient, credentials: Pair<String, String>): OkHttpClient {
        val builder = Request.Builder()
        builder.url("$host/oauth/token")
        builder.post(
                FormBody.Builder()
                        .add("grant_type", "password")
                        .add("username", credentials.first)
                        .add("password", credentials.second)
                        .build()
        )
        val encoder = Base64.getEncoder()
        builder.addHeader("Authorization", "Basic " + encoder.encodeToString(("$qTestSubDomain:").toByteArray()))
        val request = builder.build()

        val response = okHttpClient.newCall(request).execute()
        response.body().use {
            val jacksonObjectMapper = jacksonObjectMapper()
            val body = it!!.string()
            val mapTypeReference = object : TypeReference<Map<String, String?>>() {}
            val jsonMap = jacksonObjectMapper.readValue<Map<String, String?>>(body, mapTypeReference)

            val accessToken = jsonMap["access_token"].asNullableString()
            val tokenType = jsonMap["token_type"].asNullableString()
            val refreshToken = jsonMap["refresh_token"].asNullableString()
            val scope = Regex("\\s+").split(jsonMap["scope"].asNullableString() ?: "").toSet()
            val agent = jsonMap["agent"].asNullableString()

            val loginTokenAuthenticator = LoginTokenAuthenticator(accessToken, tokenType, refreshToken, scope, agent)
            return okHttpClient.newBuilder().authenticator(loginTokenAuthenticator).build()
        }
    }

    private fun String?.asNullableString(): String? {
        if (this == "null") {
            return null
        }
        return this
    }
}

class ProjectClient(private val okHttpClient: OkHttpClient, private val host: String) {

    /**
     * @see <a href="https://api.qasymphony.com/#/project/getUsers">qTest API</a>
     */
    fun fromId(id: Long): Project {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$id")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, Project::class.java)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/project/getProjects">qTest API</a>
     */
    fun projects(): List<Project> {
        val request = Request.Builder()
                .url("$host/api/v3/projects")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        val listOfProjects = object : TypeReference<List<Project>>() {}
        return responseToObj(response, listOfProjects)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/project/getProject">qTest API</a>
     */
    fun users(projectId: Long): List<User> {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/users")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        val listOfUserType = object : TypeReference<List<User>>() {}
        return responseToObj(response, listOfUserType)
    }
}


class UserClient(private val okHttpClient: OkHttpClient, private val host: String) {

    /**
     * @see <a href="https://api.qasymphony.com/#/user/getUserById">qTest API</a>
     */
    fun fromId(userId: Long): User {
        val request = Request.Builder()
                .url("$host/api/v3/users/$userId")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, User::class.java)
    }
}

class ReleaseClient(private val okHttpClient: OkHttpClient, private val host: String, private val projectId: Long) {

    /**
     * @see <a href="https://api.qasymphony.com/#/release/get2">qTest API</a>
     */
    fun fromId(releaseId: Long): Release {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/releases/$releaseId")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, Release::class.java)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/release/getAll">qTest API</a>
     */
    fun releases(): List<Release> {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/releases")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        val listOfReleases = object : TypeReference<List<Release>>() {}
        return responseToObj(response, listOfReleases)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/release/create2">qTest API</a>
     */
    fun create(name: String): Release {
        val content = jsonOf(Item("name", name))
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/releases")
                .post(RequestBody.create(MediaType.parse("application/json"), content))
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, Release::class.java)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/release/delete2">qTest API</a>
     */
    fun delete(releaseId: Long): Boolean {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/releases/$releaseId")
                .delete()
                .build()
        val response = okHttpClient.newCall(request).execute()
        return response.isSuccessful
    }
}

class TestCycleClient(private val okHttpClient: OkHttpClient, private val host: String, private val projectId: Long) {

    /**
     * @see <a href="https://api.qasymphony.com/#/test-cycle/getTestCycle">qTest API</a>
     */
    fun fromId(testCycleId: Long): TestCycle {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/test-cycles/$testCycleId")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, TestCycle::class.java)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/test-cycle/getTestCycles">qTest API</a>
     */
    fun testCycles(): List<TestCycle> {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/test-cycles")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        val listOfTestCycles = object : TypeReference<List<TestCycle>>() {}
        return responseToObj(response, listOfTestCycles)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/test-cycle/createCycle">qTest API</a>
     */
    fun create(name: String, parentType: TestCycleParent = TestCycleParent.ROOT, parentId: Long = 0): TestCycle {
        val content = jsonOf(Item("name", name))
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/test-cycles?parentType=${parentType.value}&parentId=$parentId")
                .post(RequestBody.create(MediaType.parse("application/json"), content))
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, TestCycle::class.java)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/test-cycle/deleteCycle">qTest API</a>
     */
    fun delete(testCycleId: Long): Boolean {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/test-cycles/$testCycleId")
                .delete()
                .build()
        val response = okHttpClient.newCall(request).execute()
        return response.isSuccessful
    }
}
//class RequirementClient(private val okHttpClient: OkHttpClient, private val host: String, private val projectId: Long) {
//}


private fun <T> responseToObj(response: Response, type: Class<T>): T {
    val string = response.body()!!.string()
    return objectMapper.readValue(string, type)
}

private fun <T> responseToObj(response: Response, typeReference: TypeReference<T>): T {
    val string = response.body()!!.string()
    return objectMapper.readValue(string, typeReference)
}
