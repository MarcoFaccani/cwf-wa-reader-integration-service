package com.cwf.wa.reader.integration.service.service

import com.cwf.wa.reader.integration.service.model.exception.TokenNotValidException
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class WaSecurityService(
  @Value("\${facebook.wa.verification-token}") val verificationToken: String,
  @Value("\${facebook.wa.secret}") val secret: String
) {

  companion object {
    val log: Logger = LogManager.getLogger()
  }

  fun verifySHA(signature256: String, payload: String) {
    val sha256hex = HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(payload)
    val expectedSignature = signature256.removePrefix("sha256=")

    //log.info("Generated sha256hex: $sha256hex")
    //log.info("Expected signature: $expectedSignature")

    require(sha256hex == expectedSignature) { "Invalid signature" }
    log.info("Signature verified")
  }

  fun verifyToken(inputToken: String) {
    if (inputToken != verificationToken) {
      log.info("Invalid verification token - message will be discarded")
      throw TokenNotValidException()
    }
  }


}