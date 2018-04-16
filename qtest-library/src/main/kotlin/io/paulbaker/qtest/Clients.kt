package io.paulbaker.qtest

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request


class ClientProducer(private val host: String, private val loginToken: LoginToken) {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .authenticator(loginToken)
            .build()

    fun createUserClient(): UserClient = UserClient(okHttpClient, host)

    fun createProjectClient(): ProjectClient = ProjectClient(okHttpClient, host)
}

class ProjectClient(private val okHttpClient: OkHttpClient, private val host: String) {

    /**
     * @see <a href="https://api.qasymphony.com/#/project/getUsers">qTest API</a>
     */
    fun users(projectId: Long): List<User> {
        val url = "$host/api/v3/projects/$projectId/users"
        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        val listOfUserType = object : TypeReference<List<User>>() {}
        val string = response.body()!!.string()
        return jacksonObjectMapper().readValue<List<User>>(string, listOfUserType)
    }
}


class UserClient(private val okHttpClient: OkHttpClient, private val host: String) {

    /**
     * @see <a href="https://api.qasymphony.com/#/user/getUserById">qTest API</a>
     */
    fun fromId(userId: Long): User {
        val url = "$host/api/v3/users/$userId"
        val request = Request.Builder()
                .url(url)
                .get()
                .build()
        val response = okHttpClient.newCall(request).execute()
        val string = response.body()!!.string()
        return jacksonObjectMapper().readValue(string, User::class.java)
    }
}

