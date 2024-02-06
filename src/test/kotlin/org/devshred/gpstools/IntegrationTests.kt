package org.devshred.gpstools

import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_OCTET_STREAM
import org.springframework.http.MediaType.APPLICATION_XML
import org.springframework.http.RequestEntity.get
import org.springframework.http.RequestEntity.post
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(
    @Autowired val restTemplate: TestRestTemplate,
) {
    @Test
    fun `storedFile lifecycle`() {
        val filename = randomAlphabetic(8) + ".gpx"
        val fileContent = this::class.java.classLoader.getResource("data/test.gpx")!!.readText(Charsets.UTF_8)

        // upload a file
        val createRequest =
            post("/file?filename=$filename")
                .contentType(APPLICATION_XML)
                .body(fileContent)
        val createResponse = restTemplate.exchange<StoredFileDto>(createRequest)

        assertThat(createResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(createResponse.body).isNotNull
        assertThat(createResponse.body!!.filename).isEqualTo(filename)

        val uuid = createResponse.body!!.id

        // download a file
        val downloadRequest = get("/files/$uuid").build()
        val downloadResponse = restTemplate.exchange<String>(downloadRequest)

        assertThat(downloadResponse.headers.contentType).isEqualTo(APPLICATION_OCTET_STREAM)
        assertThat(downloadResponse.body).isNotNull()
        // TODO: compare waypoints

        // delete a file
        restTemplate.delete("/files/$uuid")

        // requesting a deleted file should return 404/not found
        val downloadResponse2 = restTemplate.exchange<String>(downloadRequest)
        assertThat(downloadResponse2.statusCode).isEqualTo(NOT_FOUND)
    }

    data class StoredFileDto(
        val id: UUID,
        val filename: String,
        val mimeType: String,
        val href: String,
        val size: Long,
    )
}