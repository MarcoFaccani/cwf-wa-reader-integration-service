package com.cwf.wa.reader.integration.service.channel.outbound

import com.cwf.commonlibrary.request.GameServiceRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


@FeignClient("games", url = "\${http.game-service.url}")
interface GameClient {

  @RequestMapping(method = [RequestMethod.POST], value = ["/message"])
  fun forwardMessage(request: GameServiceRequest): ResponseEntity<HttpStatus>
}