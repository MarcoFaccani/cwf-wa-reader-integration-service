package com.cwf.wa.reader.integration.service.service

import com.cwf.wa.reader.integration.service.model.inbound.WaMessageRequest
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service


@Service
class WaReaderService {

  companion object {
    val log = LogManager.getLogger()
  }

  fun handleMessage(request: WaMessageRequest) {

  }

}

/*

  fun handleMessage(request: InboundMessageRequest) {
  markMessageAsReadOnWa(request)

  val fullPhoneNumber = helperService.extractCustomerPhoneNumber(request)
  val customer = getCustomerElseUserIsNotRegistered(fullPhoneNumber)

  getAssignedOngoingGame(customer).ifPresentOrElse({ game -> gameService.verifyAnswer(request, game, customer) },
    {
      log.info("No ongoing game found for customer: ${customer.fullPhoneNumber}")
      helperService.extractInteractiveReply(request)
        .ifPresentOrElse({ reply -> gameService.assignNewGameToCustomerAndBeginGame(request, reply, customer) },
          {
            gameService.checkIfCustomerHasAvailablePurchasedGame(request, customer)
            gameService.chooseGame(fullPhoneNumber)
          }
        )
    }
  )

}



private fun markMessageAsReadOnWa(request: InboundMessageRequest) {
  val messageId = helperService.extractMessageId(request)
  waWebClient.sendMessage(msgBuilderService.buildNotifyMessageHasBeenReadMessage(messageId))
}

private fun getAssignedOngoingGame(customer: Customer): Optional<AssignedGame> {
  return customer.assignedGames
      .stream()
      .filter { game -> game.gameStatus!! == AssignedGame.GameStatus.ONGOING }
      .findFirst()
}

private fun getCustomerElseUserIsNotRegistered(phoneNumber: String): Customer {
  val optionalCustomer = customerService.findByFullPhoneNumber(phoneNumber)

  if (optionalCustomer.isPresent) return optionalCustomer.get()
  else {
    log.info("Customer with phoneNumber $phoneNumber not found")
    waWebClient.sendMessage(msgBuilderService.buildUserNotRegisteredMessage(phoneNumber))
    throw CustomerNotFoundException()
  }
}

}

*/