package com.cwf.wa.reader.integration.service.model.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class ValidationErrorResponse : HashMap<String?, Any?>() {

  companion object {
    fun ofMessage(message: String?): ValidationErrorResponse {
      val response = ValidationErrorResponse()
      response["timestamp"] = LocalDateTime.now()
      response["status"] = HttpStatus.BAD_REQUEST
      response["error"] = HttpStatus.BAD_REQUEST.reasonPhrase
      response["message"] = message
      return response
    }
  }
}

data class ResponseStatusErrorResponse(
  val status: String,
  val message: String,
  val error: String,
  val path: String
)