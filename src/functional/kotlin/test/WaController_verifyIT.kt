import com.cwf.wa.reader.integration.service.Application
import com.cwf.wa.reader.integration.service.model.outbound.ResponseStatusErrorResponse
import com.fasterxml.jackson.databind.JsonNode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(
  classes = [Application::class],
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = [
    "facebook.wa.secret=17002828369515bbf826f47e809b25b8",
    "facebook.wa.verification-token=verificationToken"
  ]
)
internal class WaController_verifyIT(@LocalServerPort val serverPort: Int) {


  @Autowired
  lateinit var testRestTemplate: TestRestTemplate

  @Autowired
  lateinit var env: Environment

  lateinit var baseUrl: String

  @BeforeEach
  fun setup() {
    baseUrl = "http://localhost:${serverPort}"
  }

  @Nested
  inner class VerifyIT {

    @Test
    fun `happy path - should return challenge param`() {
      val challenge = "2"
      val url = UriComponentsBuilder.fromHttpUrl("$baseUrl/webhooks")
        .queryParam("hub.mode", 1)
        .queryParam("hub.challenge", challenge)
        .queryParam("hub.verify_token", env.getProperty("facebook.wa.verification-token"))
        .build().encode().toUri()

      val requestEntity = RequestEntity<Any>(HttpMethod.GET, url)
      val response: ResponseEntity<JsonNode> = testRestTemplate.exchange(requestEntity, JsonNode::class.java)

      Assertions.assertEquals(HttpStatus.OK, response.statusCode)
      Assertions.assertEquals(challenge, response.body.toString())
    }

    @Test
    fun `should throw TokenNotValidException when token doesn't match`() {
      val url = UriComponentsBuilder.fromHttpUrl("$baseUrl/webhooks")
        .queryParam("hub.mode", 1)
        .queryParam("hub.challenge", 2)
        .queryParam("hub.verify_token", "wrong token")
        .build().encode().toUri()

      val requestEntity = RequestEntity<Any>(HttpMethod.GET, url)
      val response: ResponseEntity<ResponseStatusErrorResponse> =
        testRestTemplate.exchange(requestEntity, ResponseStatusErrorResponse::class.java)

      Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
      Assertions.assertEquals("Invalid verification token - message will be discarded", response.body!!.message)
    }
  }
}