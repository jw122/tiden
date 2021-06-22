package com.redenvelope.flagship

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlagshipApplication

fun main(args: Array<String>) {
	runApplication<FlagshipApplication>(*args)
}
