package com.cwf.wa.reader.integration.service.extention

import com.cwf.wa.reader.integration.service.data.GameServiceRequestsTestData
import com.cwf.wa.reader.integration.service.model.inbound.WaMessageRequest
import com.cwf.wa.reader.integration.service.readFileAsObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class ToWaWriterServiceRequestTest {

  lateinit var waMessageRequest: WaMessageRequest

  @BeforeEach
  fun setup() {
    waMessageRequest = readFileAsObject("json/request/text-message.json")
  }

  @Test
  fun `should extract messageId and map it to request`() {
    assertEquals(
      GameServiceRequestsTestData.buildMarkMessageAsReadRequest(),
      waMessageRequest.toMarkMessageAsReadRequest()
    )
  }

}