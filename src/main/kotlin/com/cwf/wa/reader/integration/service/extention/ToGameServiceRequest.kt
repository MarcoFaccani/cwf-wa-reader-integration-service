package com.cwf.wa.reader.integration.service.extention

import com.cwf.commonlibrary.model.MessageType
import com.cwf.commonlibrary.model.wa.MarkMessageAsReadRequest
import com.cwf.commonlibrary.request.GameServiceRequest
import com.cwf.commonlibrary.request.InboundMessage
import com.cwf.wa.reader.integration.service.model.exception.UnknownMessageTypeException
import com.cwf.wa.reader.integration.service.model.inbound.WaMessageRequest


fun WaMessageRequest.toGameServiceRequest(): GameServiceRequest {
  val (messageType, content) = getMessageTypeAndContent(this)

  return GameServiceRequest(
    fullPhoneNumber = getFullPhoneNumber(this),
    playerName = getPlayerName(this),
    message = InboundMessage(
      type = messageType,
      content = content
    )
  )
}

fun WaMessageRequest.toMarkMessageAsReadRequest() = MarkMessageAsReadRequest(
  messageId = getMessageId(this)
)


// Related utility methods

fun getFullPhoneNumber(request: WaMessageRequest): String {
  return request.entry[0].changes[0].value.contacts!![0].waId // waId is the phone number
}

fun getPlayerName(request: WaMessageRequest): String {
  return request.entry[0].changes[0].value.contacts!![0].profile.name
}

fun getMessageTypeAndContent(request: WaMessageRequest): Pair<MessageType, String> {
  val message = request.entry[0].changes[0].value.messages[0]

  return when (message.type) {
    "text" -> Pair(MessageType.TEXT, message.text!!.body!!)
    "interactive" -> {
      when (message.interactive!!.type) {
        "button_reply" -> Pair(MessageType.BUTTON, message.interactive.buttonReply!!.title!!)
        "list_reply" -> Pair(MessageType.LIST, message.interactive.listReply!!.title!!)
        else -> {
          throw UnknownMessageTypeException("Error extracting message from incoming request - message type ${message.interactive.type} is unknown for parent type 'interactive'")
        }
      }
    }

    else -> {
      throw UnknownMessageTypeException("Error extracting message from incoming request - message type ${message.type} is unknown")
    }
  }
}

fun getMessageId(request: WaMessageRequest): String {
  return request.entry[0].changes[0].value.messages[0].id
}

