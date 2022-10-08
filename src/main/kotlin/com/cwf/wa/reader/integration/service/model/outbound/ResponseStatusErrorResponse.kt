package com.cwf.wa.reader.integration.service.model.outbound

data class ResponseStatusErrorResponse(
  val status: String,
  val message: String,
  val error: String,
  val path: String
)