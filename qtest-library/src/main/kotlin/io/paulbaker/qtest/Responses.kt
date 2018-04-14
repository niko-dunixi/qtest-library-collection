package io.paulbaker.qtest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
        val id: Long,
        val username: String,
        val email: String,
        @JsonProperty("first_name")
        val firstName: String,
        @JsonProperty("last_name")
        val lastName: String,
        val status: Long,
        val avatar: String,
        @JsonProperty("ldap_username")
        val ldapUsername: String?
)