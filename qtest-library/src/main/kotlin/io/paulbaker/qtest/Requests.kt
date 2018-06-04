package io.paulbaker.qtest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter
import java.time.LocalDateTime
import java.util.*

enum class TestCycleParent(val value: String) {
    ROOT("root"), RELEASE("release"), TEST_CYCLE("test-cycle")
}

enum class TestRunParent(val value: String) {
    ROOT("root"), RELEASE("release"), TEST_CYCLE("test-cycle"), TEST_SUITE("test-suite")
}

data class TestResult(
        val status: String,
        @JsonProperty("exe_start_date")
        val startTime: LocalDateTime,
        @JsonProperty("exe_end_date")
        val endTime: LocalDateTime,
        @JsonIgnore
        val note: String = "",
        @JsonIgnore
        val noteIsHtml: Boolean = true,
        val attachments: List<TestResultAttachment> = emptyList()
)

/**
 * Abstract parent class for attachments that qTest can consume
 */
abstract class TestResultAttachment(
        val name: String,
        @JsonProperty("data")
        @JsonSerialize(converter = RawBytesToBase64StringConverter::class)
        val rawByteData: ByteArray
) {

    @JsonProperty("content_type")
    abstract fun contentType(): String
}

class TextAttachment(name: String, text: String) : TestResultAttachment(name, text.toByteArray()) {
    override fun contentType(): String = "text/plain"
}

class WordDocumentAttachment(name: String, rawByteData: ByteArray) : TestResultAttachment(name, rawByteData) {
    override fun contentType(): String = "application/msword"
}

class WordDocxAttachment(name: String, rawByteData: ByteArray) : TestResultAttachment(name, rawByteData) {
    override fun contentType(): String = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
}

class PNGAttachment(name: String, rawByteData: ByteArray) : TestResultAttachment(name, rawByteData) {
    override fun contentType(): String = "image/png"
}

class JPGAttachment(name: String, rawByteData: ByteArray) : TestResultAttachment(name, rawByteData) {
    override fun contentType(): String = "image/jpeg"
}

class GIFAttachment(name: String, rawByteData: ByteArray) : TestResultAttachment(name, rawByteData) {
    override fun contentType(): String = "image/gif"
}

class PDFAttachment(name: String, rawByteData: ByteArray) : TestResultAttachment(name, rawByteData) {
    override fun contentType(): String = "application/pdf"
}

class HTMLAttachment(name: String, htmlString: String) : TestResultAttachment(name, htmlString.toByteArray()) {
    override fun contentType(): String = "text/html"
}

class ZIPAttachment(name: String, rawByteData: ByteArray) : TestResultAttachment(name, rawByteData) {
    override fun contentType(): String = "application/zip"
}


/**
 * Used to convert raw data to a base64 encoded string which is safe for uploading
 */
class RawBytesToBase64StringConverter : Converter<ByteArray, String> {
    override fun getInputType(typeFactory: TypeFactory?): JavaType = typeFactory!!.constructType(ByteArray::class.java)
    override fun getOutputType(typeFactory: TypeFactory?): JavaType = typeFactory!!.constructType(String::class.java)
    override fun convert(value: ByteArray?): String {
        return Base64.getEncoder().encodeToString(value)
    }
}