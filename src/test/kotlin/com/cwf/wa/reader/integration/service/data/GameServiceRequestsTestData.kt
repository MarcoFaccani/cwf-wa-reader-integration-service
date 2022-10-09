package com.cwf.wa.reader.integration.service.data

import com.cwf.commonlibrary.model.GameServiceRequest
import com.cwf.commonlibrary.model.InboundMessage
import com.cwf.commonlibrary.model.MessageType
import com.cwf.commonlibrary.model.wa.MarkMessageAsReadRequest

class GameServiceRequestsTestData {

  companion object {

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

    fun buildMarkMessageAsReadRequest() = MarkMessageAsReadRequest(
      messageId = "wamid.HBgMMzkzNDYxMjg1NjIzFQIAEhgUM0VCMDE3NzNGRUI2RkE0NEYwODcA" // taken from text-message
    )

  }
}