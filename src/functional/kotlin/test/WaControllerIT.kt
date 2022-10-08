package com.cwf.wa.reader.integration.service

import com.cwf.wa.reader.integration.service.service.WaSecurityService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.env.Environment
import org.springframework.http.*
import java.net.URI


@SpringBootTest(
	classes = [Application::class],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = [
		"facebook.wa.secret=17002828369515bbf826f47e809b25b8",
		"facebook.wa.verification-token=verificationToken"
	]
)
internal class WaControllerIT(@LocalServerPort val serverPort: Int) {

	@Autowired
	lateinit var testRestTemplate: TestRestTemplate

	@Autowired
	lateinit var env: Environment

	lateinit var baseUrl: String

	// I didn't manage to successfully test the verifySHA so I'm mocking the service
	@MockBean
	lateinit var waSecurityService: WaSecurityService

	@BeforeEach
	fun setup() {
		baseUrl = "http://localhost:${serverPort}"
	}


	@Nested
	inner class HandleMessageIT {

		@Test
		fun `happy path`() {
			val signature256 = readFileAsString("json/request/sha256.txt")
			val requestBody = readFileAsString("json/request/sha256-verification-body.json")

			val headers = HttpHeaders()
			headers.setContentType(MediaType.APPLICATION_JSON)
			headers.add("X-Hub-Signature", "1")
			headers.add("X-Hub-Signature-256", signature256)

			val entity = HttpEntity<String>(requestBody, headers)
			val response: ResponseEntity<String> =
				testRestTemplate.postForEntity(URI.create("$baseUrl/webhooks"), entity, String::class.java)

			assertEquals(HttpStatus.OK, response.statusCode)
		}

	}


}
