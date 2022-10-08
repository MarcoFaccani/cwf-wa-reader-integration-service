package com.cwf.wa.reader.integration.service

import com.cwf.wa.reader.integration.service.service.WaSecurityService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.env.Environment
import org.springframework.http.*
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
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

	companion object {

		private lateinit var gameMockWebServer: MockWebServer
		private lateinit var waWriterMockWebServer: MockWebServer

		private const val GAME_REQUEST_PATH = "/game/message"
		private const val WA_WRITER_REQUEST_PATH = "/game/message"

		@JvmStatic
		@BeforeAll
		internal fun beforeAll() {
			gameMockWebServer = MockWebServer()
			gameMockWebServer.start()

			waWriterMockWebServer = MockWebServer()
			waWriterMockWebServer.start()
		}

		@JvmStatic
		@DynamicPropertySource
		internal fun setProperties(registry: DynamicPropertyRegistry) {
			registry.add("http.game-service.url") { gameMockWebServer.url("/").toString() }
			registry.add("http.wa-writer.url") { waWriterMockWebServer.url("/").toString() }
		}

		@JvmStatic
		@AfterAll
		internal fun afterAll() {
			gameMockWebServer.shutdown()
		}
	}


	@Nested
	inner class HandleMessageIT {

		lateinit var headers: HttpHeaders

		@BeforeEach
		fun setup() {
			gameMockWebServer.url(GAME_REQUEST_PATH)
			waWriterMockWebServer.url(WA_WRITER_REQUEST_PATH)

			headers = HttpHeaders()
			headers.setContentType(MediaType.APPLICATION_JSON)
			headers.add("X-Hub-Signature", "fake")
			headers.add("X-Hub-Signature-256", "fake")
		}

		@Test
		fun `happy path - should forward message to game-service`() {
			waWriterMockWebServer.enqueue(MockResponse().setResponseCode(200))
			gameMockWebServer.enqueue(MockResponse().setResponseCode(200))

			val requestBody = readFileAsString("json/request/text-message.json")
			val entity = HttpEntity<String>(requestBody, headers)
			val response: ResponseEntity<String> =
				testRestTemplate.postForEntity(URI.create("$baseUrl/webhooks"), entity, String::class.java)

			assertEquals(HttpStatus.OK, response.statusCode)
		}

		@Test
		@ExtendWith(OutputCaptureExtension::class)
		fun `unhappy path - response from game-service is 500`(logCapturer: CapturedOutput) {
			val errorMessage = "an error message"
			waWriterMockWebServer.enqueue(MockResponse().setResponseCode(200))
			gameMockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("""{ "message": "$errorMessage" }"""))

			val requestBody = readFileAsString("json/request/text-message.json")
			val entity = HttpEntity<String>(requestBody, headers)
			val response: ResponseEntity<String> =
				testRestTemplate.postForEntity(URI.create("$baseUrl/webhooks"), entity, String::class.java)

			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
			assertThat(logCapturer).contains(errorMessage)
		}

		@Test
		@ExtendWith(OutputCaptureExtension::class)
		fun `unhappy path - response from wa-writer-service is 500`(logCapturer: CapturedOutput) {
			val errorMessage = "an error message"
			gameMockWebServer.enqueue(MockResponse().setResponseCode(200))
			waWriterMockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("""{ "message": "$errorMessage" }"""))

			val requestBody = readFileAsString("json/request/text-message.json")
			val entity = HttpEntity<String>(requestBody, headers)
			val response: ResponseEntity<String> =
				testRestTemplate.postForEntity(URI.create("$baseUrl/webhooks"), entity, String::class.java)

			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
			assertThat(logCapturer).contains(errorMessage)
		}

	}


}
