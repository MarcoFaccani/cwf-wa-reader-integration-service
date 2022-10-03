package com.cwf.wa.reader.integration.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files

fun readFileAsString(fileName: String): String = Files.readString(ClassPathResource(fileName).file.toPath())

inline fun <reified T> readFileAsObject(fileName: String): T {
  val om = jacksonObjectMapper()
  om.registerModule(JavaTimeModule())
  return jacksonObjectMapper().readValue<T>(readFileAsString(fileName))
}


fun setFieldToEmpty(obj: Any, fieldToUpdate: String) {
  val field = obj.javaClass.getDeclaredField(fieldToUpdate)
  field.isAccessible = true
  field.set(obj, "")
  field.isAccessible = false
}

fun setFieldToValue(obj: Any, fieldToUpdate: String, value: Any) {
  val field = obj.javaClass.getDeclaredField(fieldToUpdate)
  field.isAccessible = true
  field.set(obj, value)
  field.isAccessible = false
}

inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

fun asJsonString(obj: Any) = jacksonObjectMapper().writeValueAsString(obj)
