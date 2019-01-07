package pl.dominikoso.host

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HostApplication

fun main(args: Array<String>) {
	runApplication<HostApplication>(*args)
}

