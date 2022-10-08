package com.cwf.wa.reader.integration.service.jackson

import com.cwf.wa.reader.integration.service.model.inbound.WaMessageRequest
import com.cwf.wa.reader.integration.service.readFileAsObject
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester

@JsonTest
internal class JsonRequestToModelTest(
  @Autowired val jacksonTester: JacksonTester<WaMessageRequest>
) {

  val objectMapper = jacksonObjectMapper()

  lateinit var request: WaMessageRequest

  @Nested
  inner class TextMessageTest {

    @BeforeEach
    fun setup() {
      request = readFileAsObject("json/request/text-message.json")
    }

    @Test
    fun `should map generic basic data to InboundMessageRequest`() {
      assertNotNull(request)
      assertNotNull(request.entry[0].changes)

      assertEquals("111126944995938", request.entry[0].id)

      val value = request.entry[0].changes[0].value
      assertNotNull(value)
      assertEquals("whatsapp", value.messaging_product)
    }

    @Test
    fun `should map Changes to InboundMessageRequest`() {
      val changes = request.entry[0].changes
      assertNotNull(changes)
      assertThat(changes).hasSize(1)

      val change = changes[0]
      assertEquals("messages", change.field)
    }

    @Test
    fun `should map Metadata to InboundMessageRequest`() {
      val metadata = request.entry[0].changes[0].value.metadata
      assertNotNull(metadata)
      assertEquals("15550798533", metadata!!.displayPhoneNumber)
      assertEquals("108335841945176", metadata.phoneNumberId)
    }

    @Test
    fun `should map Contact to InboundMessageRequest`() {
      val contacts = request.entry[0].changes[0].value.contacts!!
      assertNotNull(contacts)
      assertThat(contacts).hasSize(1)

      assertEquals("393461285623", contacts[0].waId)
      assertEquals("Marco", contacts[0].profile!!.name)
    }

    @Test
    fun `should map Messages to InboundMessageRequest`() {
      val messages = request.entry[0].changes[0].value.messages
      assertNotNull(messages)
      assertThat(messages).hasSize(1)

      val message = messages[0]
      assertEquals("393461285623", message.from)
      assertEquals("wamid.HBgMMzkzNDYxMjg1NjIzFQIAEhgUM0VCMDE3NzNGRUI2RkE0NEYwODcA", message.id)
      assertEquals("1659647929", message.timestamp)
      assertEquals("text", message.type)
    }

    @Test
    fun `should map Text to InboundMessageRequest`() {
      val text = request.entry[0].changes[0].value.messages[0].text
      assertNotNull(text)
      assertEquals("Just a text", text!!.body)
    }
  }

  @Nested
  inner class InteractiveListMessageTest {

    @BeforeEach
    fun setup() {
      request = readFileAsObject("json/request/interactive-list-message.json")
    }

    @Test
    fun `should map Interactive List object to InboundMessageRequest`() {
      val interactive = request.entry[0].changes[0].value.messages[0].interactive
      assertNotNull(interactive)
      assertEquals("list_reply", interactive!!.type)

      val listReply = interactive.listReply
      assertNotNull(listReply)
      assertEquals("2", listReply!!.id)
      assertEquals("Dummy Title", listReply.title)
      assertEquals("Dummy Description", listReply.description)
    }

  }

  @Nested
  inner class InteractiveButtonMessageTest {

    @BeforeEach
    fun setup() {
      request = readFileAsObject("json/request/interactive-button-message.json")
    }

    @Test
    fun `should map Interactive Button object to InboundMessageRequest`() {
      val interactive = request.entry[0].changes[0].value.messages[0].interactive
      assertNotNull(interactive)
      assertEquals("button_reply", interactive!!.type)

      val listReply = interactive.buttonReply
      assertNotNull(listReply)
      assertEquals("1", listReply!!.id)
      assertEquals("Dummy Title", listReply.title)
    }

  }

}