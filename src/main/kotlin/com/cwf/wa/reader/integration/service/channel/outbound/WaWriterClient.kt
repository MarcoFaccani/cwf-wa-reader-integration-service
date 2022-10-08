package com.cwf.wa.reader.integration.service.channel.outbound

import com.cwf.commonlibrary.model.wa.MarkMessageAsReadRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient("wa-writer", url = "\${http.wa-writer.url}")
interface WaWriterClient {

  @RequestMapping(method = [RequestMethod.POST], value = ["/message/status:read"])
  fun markMessageAsRead(request: MarkMessageAsReadRequest)
}