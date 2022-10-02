package io.github.tscholze.onthisday


import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// MARK: - App-wide properties -

val config: Config by lazy { ConfigFactory.load() }
val log: Logger = LoggerFactory.getLogger("ApplicationKt")

// MARK: - Main -
fun main() {
    // Ensure client id and secret are set
    if (spaceAppInstance.clientId.isEmpty() || spaceAppInstance.clientSecret.isEmpty()) {
        log.error("Please specify application credentials in src/main/resources/application.conf")
        return
    }

    // Start webserver
    embeddedServer(Netty, port = 8080) {
        configureRouting()
    }.start(wait = true)
}


