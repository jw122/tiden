package com.redenvelope.flagship

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class FlagshipApplication

fun main(args: Array<String>) {
	runApplication<FlagshipApplication>(*args)
}

@RestController
class MessageResource {
	@GetMapping
	fun index(): List<Message> = listOf(
		Message("1", "Hello"),
		Message("2", "Ni hao"),
		Message("3", "Bonjour")
	)
}

data class Message(val id: String?, val text: String)