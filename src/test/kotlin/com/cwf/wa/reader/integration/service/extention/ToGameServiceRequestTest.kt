package com.cwf.wa.reader.integration.service.extention

import com.cwf.commonlibrary.model.GameServiceRequest
import com.cwf.commonlibrary.model.InboundMessage
import com.cwf.commonlibrary.model.MessageType
import com.cwf.wa.reader.integration.service.model.exception.UnknownMessageTypeException
import com.cwf.wa.reader.integration.service.model.inbound.WaMessageRequest
import com.cwf.wa.reader.integration.service.readFileAsObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class ToGameServiceRequestTest {

  lateinit var buttonMessage: WaMessageRequest
  lateinit var listMessage: WaMessageRequest
  lateinit var textMessage: WaMessageRequest

  @BeforeEach
  fun setup() {
    textMessage = readFileAsObject("json/request/text-message.json")
    listMessage = readFileAsObject("json/request/interactive-list-message.json")
    buttonMessage = readFileAsObject("json/request/interactive-button-message.json")
  }


  @Nested
  inner class HappyPaths() {

    @Test
    fun `should map WaRequest of type TEXT to GameServiceRequest`() {
      assertEquals(buildExpectedGameServiceRequest_text(), textMessage.toGameServiceRequest())
    }

    @Test
    fun `should map WaRequest of type LIST to GameServiceRequest`() {
      assertEquals(buildExpectedGameServiceRequest_list(), listMessage.toGameServiceRequest())
    }

    @Test
    fun `should map WaRequest of type BUTTON to GameServiceRequest`() {
      assertEquals(buildExpectedGameServiceRequest_button(), buttonMessage.toGameServiceRequest())
    }
  }

  @Nested
  inner class UnhappyPaths {

    @Test
    fun `should throw UnknownMessageTypeException when message type is unknown`() {
      val unknownMessage = readFileAsObject<WaMessageRequest>("json/request/unknown-message-type.json")
      val e = assertThrows<UnknownMessageTypeException> { unknownMessage.toGameServiceRequest() }
      assertEquals("Error extracting message from incoming request - message type UNKNOWN is unknown", e.message)
    }

    @Test
    fun `should throw UnknownMessageTypeException when interactive type is unknown`() {
      val unknownMessage = readFileAsObject<WaMessageRequest>("json/request/unknown-interactive-message-type.json")
      val e = assertThrows<UnknownMessageTypeException> { unknownMessage.toGameServiceRequest() }
      assertEquals(
        "Error extracting message from incoming request - message type UNKNOWN is unknown for parent type 'interactive'",
        e.message
      )
    }
  }

  fun buildExpectedGameServiceRequest_text() = GameServiceRequest(
    fullPhoneNumber = "393461285623",
    playerName = "Marco",
    message = InboundMessage(
      type = MessageType.TEXT,
      content = "Just a text"
    )
  )

  fun buildExpectedGameServiceRequest_list() = GameServiceRequest(
    fullPhoneNumber = "393461285623",
    playerName = "Marco",
    message = InboundMessage(
      type = MessageType.LIST,
      content = "Dummy Title"
    )
  )

  fun buildExpectedGameServiceRequest_button() = GameServiceRequest(
    fullPhoneNumber = "393461285623",
    playerName = "Marco",
    message = InboundMessage(
      type = MessageType.BUTTON,
      content = "Dummy Title"
    )
  )

}