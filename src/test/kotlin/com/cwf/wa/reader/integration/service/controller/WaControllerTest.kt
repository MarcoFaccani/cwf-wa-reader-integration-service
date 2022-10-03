package com.cwf.wa.reader.integration.service.controller

import com.cwf.wa.reader.integration.service.model.InboundMessageRequest
import com.cwf.wa.reader.integration.service.readFileAsObject
import com.cwf.wa.reader.integration.service.service.WaReaderService
import com.cwf.wa.reader.integration.service.service.WaSecurityService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
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

    @Test
    fun `happy path - should invoke WaReaderService`() {
      val request = readFileAsObject<InboundMessageRequest>("json/request/text-message.json")
      assertNotNull(request)
    }

  }


}