package com.fladenchef.rating

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class KebabRatingApiApplication{
    @Bean
    fun init() = ApplicationRunner {
        println("Hello World from Kebab API")
    }
}

fun main(args: Array<String>) {
    runApplication<KebabRatingApiApplication>(*args)
}


