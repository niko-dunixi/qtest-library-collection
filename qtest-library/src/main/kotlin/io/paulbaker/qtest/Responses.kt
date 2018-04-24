package io.paulbaker.qtest

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import java.time.LocalDateTime

// Reference this for @JsonFormat patterns https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
const val PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXXXX"

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

@JsonIgnoreProperties(ignoreUnknown = true)
data class Project(
        val id: Long,
        val name: String,
        val description: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
        @JsonProperty("start_date")
        val startDate: LocalDateTime?,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
        @JsonProperty("end_date")
        val endDate: LocalDateTime?,
        @JsonProperty("automation")
        val automationEnabled: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Release(
        val id: Long,
        val name: String,
        val description: String,
        val pid: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
        @JsonProperty("created_date")
        val createdDate: LocalDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
        @JsonProperty("last_modified_date")
        val lastModifiedDate: LocalDateTime,
        @JsonProperty("web_url")
        val url: URL
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class Requirement(
        val id: Long,
        @JsonProperty("parent_id")
        val parentId: Long,
        val name: String,
        val pid: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
        @JsonProperty("created_date")
        val createdDate: LocalDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
        @JsonProperty("last_modified_date")
        val lastModifiedDate: LocalDateTime
)