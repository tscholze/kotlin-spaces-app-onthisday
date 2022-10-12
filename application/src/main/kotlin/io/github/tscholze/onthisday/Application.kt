package io.github.tscholze.onthisday


import io.github.tscholze.onthisday.db.configureDatabase
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.jetbrains.api.runtime.ktorClientForSpace

// MARK: - App-wide properties -

val spaceHttpClient = ktorClientForSpace()

// MARK: - Main -
fun main() {

    // Start webserver
    embeddedServer(Netty, port = 8080) {

        // 1.1 Init database connection
        configureDatabase()

        // 1.2 Setup ktor routing
        configureRouting()
    }.start(wait = true)
}


