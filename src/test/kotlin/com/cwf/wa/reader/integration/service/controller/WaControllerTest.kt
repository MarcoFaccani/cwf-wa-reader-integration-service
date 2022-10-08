package com.cwf.wa.reader.integration.service.controller

import com.cwf.wa.reader.integration.service.model.exception.TokenNotValidException
import com.cwf.wa.reader.integration.service.model.inbound.WaMessageRequest
import com.cwf.wa.reader.integration.service.readFileAsObject
import com.cwf.wa.reader.integration.service.service.WaReaderService
import com.cwf.wa.reader.integration.service.service.WaSecurityService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.given
import org.mockito.kotlin.then
import org.mockito.kotlin.willThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@ExtendWith(value = [RestDocumentationExtension::class, SpringExtension::class])
@WebMvcTest(WaController::class)
internal class WaControllerTest(@Autowired val context: WebApplicationContext) {


  lateinit var mockMvc: MockMvc

  private val objMapper = jacksonObjectMapper()

  @MockBean
  lateinit var waReaderService: WaReaderService

  @MockBean
  lateinit var waSecurityService: WaSecurityService

  @BeforeEach
  fun setUp(restDocumentation: RestDocumentationContextProvider?) {
    mockMvc = MockMvcBuilders
      .webAppContextSetup(context)
      .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
      .alwaysDo<DefaultMockMvcBuilder>(
        MockMvcRestDocumentation.document(
          "{method-name}",
          Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
          Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
        )
      )
      .build()
  }

  @Nested
  inner class HandleMessageTest {

    lateinit var request: WaMessageRequest
    lateinit var requestAsString: String

    val signature256 = "xHubSignature256DummyValue"

    @BeforeEach
    fun setup() {
      request = readFileAsObject("json/request/text-message.json")
    }

    @Test
    fun `happy path - should invoke WaReaderService`() {
      request.entry[0].changes[0].value.metadata = null
      requestAsString = objMapper.writeValueAsString(request)

      mockMvc.perform(
        post("/webhooks")
          .content(requestAsString)
          .header("X-Hub-Signature", "xHubSignatureDummyValue")
          .header("X-Hub-Signature-256", signature256)
          .contentType(MediaType.APPLICATION_JSON)
      )
        .andExpect(status().isOk)

      then(waSecurityService).should().verifySHA(signature256, requestAsString)
      then(waReaderService).should().handleMessage(request)
    }

    @Test
    fun `should return 500 when exception is caught`() {
      requestAsString = objMapper.writeValueAsString(request)

      given { waSecurityService.verifySHA(signature256, requestAsString) } willThrow { TokenNotValidException() }

      mockMvc.perform(
        post("/webhooks")
          .content(requestAsString)
          .header("X-Hub-Signature", "xHubSignatureDummyValue")
          .header("X-Hub-Signature-256", signature256)
          .contentType(MediaType.APPLICATION_JSON)
      )
        .andExpect(status().isInternalServerError)
    }

  }

  @Nested
  inner class VerifyTest {

    @Test
    fun `happy path - response should be 200 and return the value of the param hub-challenge`() {
      val challengeValue = "100"

      val responseBody = mockMvc.perform(
        get("/webhooks")
          .param("hub.mode", "modeValue")
          .param("hub.challenge", "100")
          .param("hub.verify_token", "verifyTokenValue")
      )
        .andExpect(status().isOk)
        .andReturn()
        .response
        .contentAsString

      assertEquals(responseBody, challengeValue)
    }

  }


}