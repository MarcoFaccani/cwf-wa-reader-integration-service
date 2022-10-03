package com.cwf.wa.reader.integration.service.model.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid verification token - message will be discarded")
class TokenNotValidException : RuntimeException() {
}