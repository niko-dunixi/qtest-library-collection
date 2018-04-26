package io.paulbaker.qtest

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.*


class ClientProducer(private val host: String, loginToken: LoginToken) {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .authenticator(loginToken)
            .build()

    fun createUserClient(): UserClient = UserClient(okHttpClient, host)

    fun createProjectClient(): ProjectClient = ProjectClient(okHttpClient, host)

    fun createReleaseClient(): ReleaseClient = ReleaseClient(okHttpClient, host)
}

private val objectMapper = jacksonObjectMapper()
        .registerModules(Jdk8Module(), JavaTimeModule())

class ProjectClient(private val okHttpClient: OkHttpClient, private val host: String) {

    /**
     * @see <a href="https://api.qasymphony.com/#/project/getUsers">qTest API</a>
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
    fun fromId(id: Long): Project {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$id")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, Project::class.java)
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

class ReleaseClient(private val okHttpClient: OkHttpClient, private val host: String) {

    /**
     * @see <a href="https://api.qasymphony.com/#/release/getAll">qTest API</a>
     */
    fun releases(projectId: Long): List<Release> {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/releases")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        val listOfReleases = object : TypeReference<List<Release>>() {}
        return responseToObj(response, listOfReleases)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/release/get2">qTest API</a>
     */
    fun release(projectId: Long, releaseId: Long): Release {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId/releases/$releaseId")
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, Release::class.java)
    }

    /**
     * @see <a href="https://api.qasymphony.com/#/release/create2">qTest API</a>
     */
    fun create(projectId: Long, name: String): Release {
        val request = Request.Builder()
                .url("$host/api/v3/projects/$projectId")
                .post(RequestBody.create(MediaType.parse("application/json"), "{name:$name}"))
                .build()
        val response = okHttpClient.newCall(request).execute()
        return responseToObj(response, Release::class.java)
    }
}

private fun <T> responseToObj(response: Response, type: Class<T>): T {
    val string = response.body()!!.string()
    return objectMapper.readValue(string, type)
}

private fun <T> responseToObj(response: Response, typeReference: TypeReference<T>): T {
    val string = response.body()!!.string()
    return objectMapper.readValue(string, typeReference)
}
