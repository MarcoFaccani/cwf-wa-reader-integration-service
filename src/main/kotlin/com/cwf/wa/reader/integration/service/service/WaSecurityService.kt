package com.cwf.wa.reader.integration.service.service

import com.cwf.wa.reader.integration.service.model.exception.TokenNotValidException
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
@NoArgsConstructor
@AllArgsConstructor
class WaSecurityService(
  @Value("\${facebook.wa.verification-token}") val verificationToken: String,
  @Value("\${facebook.wa.secret}") val secret: String
) {

  companion object {
    val log: Logger = LogManager.getLogger()
  }

  // make sure the secret configured in this app is the same as the one configured on FB https://developers.facebook.com/apps settings/basics
  fun verifySHA(signature256: String, payload: String) {
    val expectedSignature = signature256.removePrefix("sha256=")
    val sha256hex = HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(payload)
    require(sha256hex == expectedSignature) { "Invalid signature" }
    log.info("Signature verified")
  }

  fun verifyToken(inputToken: String) {
    if (inputToken != verificationToken) {
      log.error("Invalid verification token - message will be discarded")
      throw TokenNotValidException()
    }
  }


}