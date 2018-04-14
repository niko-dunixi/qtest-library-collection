package io.paulbaker.qtest

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import java.util.function.Supplier


/**
 * Created by Paul N. Baker on 04/13/2018
 */

class LoginTokenSupplier(private val site: String, private val username: String, private val password: String) : Supplier<LoginToken> {

    override fun get(): LoginToken {
        val builder = Request.Builder()
        builder.url("https://$site.qtestnet.com/oauth/token")
        builder.post(
                FormBody.Builder()
                        .add("grant_type", "password")
                        .add("username", username)
                        .add("password", password)
                        .build()
        )
        val encoder = Base64.getEncoder()
        builder.addHeader("Authorization", "Basic " + encoder.encodeToString(("$site:").toByteArray()))
        val request = builder.build()

        val client = OkHttpClient.Builder().build()
        val response = client.newCall(request).execute()

        return response.body().use {
            val jacksonObjectMapper = jacksonObjectMapper()
            val body = it!!.string()
            val mapTypeReference = object : TypeReference<Map<String, Any>>() {}
            val jsonMap = jacksonObjectMapper.readValue<Map<String, String?>>(body, mapTypeReference)

            val accessToken = jsonMap["access_token"].asNullableString()
            val tokenType = jsonMap["token_type"].asNullableString()
            val refreshToken = jsonMap["refresh_token"].asNullableString()

            val scope = Regex("\\s+").split(jsonMap["scope"] ?: "").toSet()

            val agent = jsonMap["agent"].asNullableString()
            LoginToken(accessToken, tokenType, refreshToken, scope, agent)
        }
    }

    private fun String?.asNullableString(): String? {
        if (this == "null") {
            return null
        }
        return this
    }
}

data class LoginToken(val accessToken: String?, val tokenType: String?, val refreshToken: String?, val scope: Set<String>, var agent: String?)