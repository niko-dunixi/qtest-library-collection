package io.paulbaker.qtest

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request


class ClientProducer(private val host: String, loginToken: LoginToken) {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .authenticator(loginToken)
            .build()

    fun createUserClient(): UserClient = UserClient(okHttpClient, host)

    fun createProjectClient(): ProjectClient = ProjectClient(okHttpClient, host)
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
        val string = response.body()!!.string()
        return objectMapper.readValue<List<User>>(string, listOfUserType)
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
        val string = response.body()!!.string()
        return objectMapper.readValue<List<Project>>(string, listOfProjects)
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
        val string = response.body()!!.string()
        return objectMapper.readValue<Project>(string, Project::class.java)
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
        val string = response.body()!!.string()
        return objectMapper.readValue(string, User::class.java)
    }
}

