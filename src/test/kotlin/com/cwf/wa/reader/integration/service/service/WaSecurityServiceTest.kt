package com.cwf.wa.reader.integration.service.service

import com.cwf.wa.reader.integration.service.model.exception.TokenNotValidException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class WaSecurityServiceTest {

  val verificationToken = "verificationToken"

  val waSecurityService = WaSecurityService(verificationToken, "waSecret")


  @Nested
  inner class VerifyShaTest() {

    @Test
    fun `happy path - should not throw exception`() {
      //TODO: do a local test to retrieve the SHA and save it in a file for local unit testing
    }
  }


  @Nested
  inner class VerifyTokenTest {

    @Test
    fun `happy path - should not throw exception`() {
      assertDoesNotThrow { waSecurityService.verifyToken(verificationToken) }
    }

    @Test
    fun `should throw exception when input token doesn't match the one from properties`() {
      val ex = assertThrows<TokenNotValidException> { waSecurityService.verifyToken("wrong token") }
      //TODO: the message of the ex is null, maybe because @ResponseStatus works only with the full context up. To do an IT test.
      //assertEquals("Invalid verification token - message will be discarded", ex.message)
    }

  }


}