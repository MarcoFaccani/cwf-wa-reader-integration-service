package com.cwf.wa.reader.integration.service.controller

import com.cwf.wa.reader.integration.service.model.InboundMessageRequest
import com.cwf.wa.reader.integration.service.service.WaReaderService
import com.cwf.wa.reader.integration.service.service.WaSecurityService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import lombok.SneakyThrows
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Log4j2
@RestController
class WaController(
  private val waReaderService: WaReaderService,
  private val waSecurityService: WaSecurityService
) {

  companion object {
    private val log = LogManager.getLogger()
  }

  private val objectMapper = jacksonObjectMapper()


  @PostMapping("/webhooks")
  @SneakyThrows
  fun handleMessage(
    @RequestBody request: InboundMessageRequest,
    @RequestHeader("X-Hub-Signature") signature1: String,
    @RequestHeader("X-Hub-Signature-256") signature256: String
  ): ResponseEntity<*> {
    val jsonRequest = objectMapper.writeValueAsString(request)
    log.debug("Message received: {}", jsonRequest)

    try {
      waSecurityService.verifySHA(signature256, jsonRequest)
      waReaderService.handleMessage(request)
    } catch (ex: Exception) {
      log.error("ERROR while handling new message. Error: {} - Message: {}", ex.message, request)
      return ResponseEntity.internalServerError().build<Any>()
    }

    return ResponseEntity.ok().build<Any>()
  }


  @GetMapping("/webhooks")
  fun verify(
    @RequestParam("hub.mode") mode: String,
    @RequestParam("hub.challenge") challenge: Int,
    @RequestParam("hub.verify_token") verifyToken: String
  ): ResponseEntity<Int> {
    log.info("Verify request received with mode: {}, challenge: {}, verifyToken: {}", mode, challenge, verifyToken)
    waSecurityService.verifyToken(verifyToken)
    log.info("Responding with: {}", challenge)
    return ResponseEntity.ok(challenge)
  }

}