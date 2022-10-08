package com.cwf.wa.reader.integration.service.service

import com.cwf.wa.reader.integration.service.channel.outbound.GameClient
import com.cwf.wa.reader.integration.service.channel.outbound.WaWriterClient
import com.cwf.wa.reader.integration.service.data.GameServiceRequestsTestData.Companion.buildExpectedGameServiceRequest_text
import com.cwf.wa.reader.integration.service.data.GameServiceRequestsTestData.Companion.buildMarkMessageAsReadRequest
import com.cwf.wa.reader.integration.service.model.inbound.WaMessageRequest
import com.cwf.wa.reader.integration.service.readFileAsObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.then
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class WaReaderServiceTest {

  @Mock
  lateinit var gameClient: GameClient

  @Mock
  lateinit var waWriterClient: WaWriterClient

  @InjectMocks
  lateinit var underTest: WaReaderService

  lateinit var waMessageRequest: WaMessageRequest

  @BeforeEach
  fun setup() {
    waMessageRequest = readFileAsObject("json/request/text-message.json")
  }

  @Test
  fun `should forward message to game service`() {
    assertDoesNotThrow { underTest.handleMessage(waMessageRequest) }
    then(waWriterClient).should().markMessageAsRead(buildMarkMessageAsReadRequest())
    then(gameClient).should().forwardMessage(buildExpectedGameServiceRequest_text())
  }
}