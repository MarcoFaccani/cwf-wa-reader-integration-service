package com.cwf.wa.reader.integration.service.model

import com.fasterxml.jackson.annotation.JsonProperty


data class InboundMessageRequest(
  val `object`: String,
  val entry: List<Entry>
)

data class Entry(
  val id: String? = null,
  val changes: ArrayList<Change>
)

data class Change(
  val value: Value,
  val field: String? = null
)

data class Value(
  val messaging_product: String,
  var metadata: Metadata? = null,
  val contacts: List<Contact>? = null,
  val messages: List<InboundMessage>
)

data class InboundMessage(
  val from: String,
  val id: String,
  val timestamp: String,
  val text: Text? = null,
  val type: String,
  val context: MessageContext? = null,
  val interactive: InteractiveMessage? = null
)

data class Contact(
  val profile: Profile? = null,
  @JsonProperty("wa_id") val waId: String? = null
)

data class Profile(
  val name: String? = null
)

data class MessageContext(
  val from: String? = null,
  val id: String? = null,
  val forwarded: String? = null,
  @JsonProperty("frequently_forwarded") val frequentlyForwarded: String? = null
)

data class InteractiveMessage(
  val type: String? = null,
  @JsonProperty("list_reply") val listReply: InteractiveListReply? = null,
  @JsonProperty("button_reply") val buttonReply: InteractiveButtonReply? = null
)

data class InteractiveListReply(
  val id: String? = null,
  val title: String? = null,
  val description: String? = null
)

data class InteractiveButtonReply(
  val id: String? = null,
  val title: String? = null,
)

data class Text(
  val body: String?
)

class Metadata {
  @JsonProperty("display_phone_number")
  val displayPhoneNumber: String? = null

  @JsonProperty("phone_number_id")
  val phoneNumberId: String? = null
}