package io.paulbaker.qtest

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter
import java.time.ZonedDateTime
import java.util.*

enum class TestCycleParent(val value: String) {
    ROOT("root"), RELEASE("release"), TEST_CYCLE("test-cycle")
}

enum class TestRunParent(val value: String) {
    ROOT("root"), RELEASE("release"), TEST_CYCLE("test-cycle"), TEST_SUITE("test-suite")
}

enum class FieldParent(val value: String) {
    RELEASE("release"), BUILD("build"), REQUIREMENT("requirement"), TEST_CASE("test-cases"), DEFECT("defect"), TEST_SUITE("test-suite"), TEST_RUN("test-run")
}

enum class SearchTarget(val value: String) {
    REQUIREMENT("requirement"), TEST_CASE("test-cases"), TEST_RUN("test-run"), DEFECT("defect")
}

data class TestResult(
        val status: String,
        @JsonProperty("exe_start_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SENDING_DATE_PATTERN)
        val startTime: ZonedDateTime,
        @JsonProperty("exe_end_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SENDING_DATE_PATTERN)
        val endTime: ZonedDateTime,
        val note: String = "",
        @JsonIgnore
        val noteIsHtml: Boolean = false,
        val attachments: List<TestResultAttachment> = emptyList()
)

/**
 * Abstract parent class for attachments that qTest can consume
 */
abstract class TestResultAttachment(
        @JsonProperty("name")
        val name: String,
        @JsonProperty("data")
        @JsonSerialize(converter = RawBytesToBase64StringConverter::class)
        val data: ByteArray
) {

    @JsonProperty("content_type")
    abstract fun contentType(): String
}

class TextAttachment(filename: String, text: String) : TestResultAttachment(filename, text.toByteArray()) {
    override fun contentType(): String = "text/plain"
}

class WordDocumentAttachment(filename: String, rawByteData: ByteArray) : TestResultAttachment(filename, rawByteData) {
    override fun contentType(): String = "application/msword"
}

class WordDocxAttachment(filename: String, rawByteData: ByteArray) : TestResultAttachment(filename, rawByteData) {
    override fun contentType(): String = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
}

class PNGAttachment(filename: String, rawByteData: ByteArray) : TestResultAttachment(filename, rawByteData) {
    override fun contentType(): String = "image/png"
}

class JPGAttachment(filename: String, rawByteData: ByteArray) : TestResultAttachment(filename, rawByteData) {
    override fun contentType(): String = "image/jpeg"
}

class GIFAttachment(filename: String, rawByteData: ByteArray) : TestResultAttachment(filename, rawByteData) {
    override fun contentType(): String = "image/gif"
}

class PDFAttachment(filename: String, rawByteData: ByteArray) : TestResultAttachment(filename, rawByteData) {
    override fun contentType(): String = "application/pdf"
}

class HTMLAttachment(filename: String, html: String) : TestResultAttachment(filename, html.toByteArray()) {
    override fun contentType(): String = "text/html"
}

class ZIPAttachment(filename: String, rawByteData: ByteArray) : TestResultAttachment(filename, rawByteData) {
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