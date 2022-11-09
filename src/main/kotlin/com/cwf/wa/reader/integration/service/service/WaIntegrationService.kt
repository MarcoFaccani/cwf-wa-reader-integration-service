package com.cwf.wa.reader.integration.service.service

import com.cwf.wa.reader.integration.service.channel.outbound.GameClient
import com.cwf.wa.reader.integration.service.channel.outbound.WaWriterClient
import com.cwf.wa.reader.integration.service.extention.toGameServiceRequest
import com.cwf.wa.reader.integration.service.extention.toMarkMessageAsReadRequest
import com.cwf.wa.reader.integration.service.model.inbound.WaMessageRequest
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class WaIntegrationService(
  @Autowired val gameClient: GameClient,
  @Autowired val waWriterClient: WaWriterClient
) {

  companion object {
    val log = LogManager.getLogger()
  }

  fun handleMessage(request: WaMessageRequest) {
    try {
      waWriterClient.markMessageAsRead(request.toMarkMessageAsReadRequest())
    } catch (ex: Exception) {
      log.warn("error marking message as read. Error message: ${ex.message}")
    }

    gameClient.forwardMessage(request.toGameServiceRequest())
  }

}
