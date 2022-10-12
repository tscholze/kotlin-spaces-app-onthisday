package io.github.tscholze.onthisday

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import space.jetbrains.api.runtime.helpers.command
import space.jetbrains.api.runtime.helpers.readPayload
import space.jetbrains.api.runtime.helpers.verifyWithPublicKey
import space.jetbrains.api.runtime.types.ListCommandsPayload
import space.jetbrains.api.runtime.types.MessagePayload

fun Application.configureRouting() {
    routing {

        // MARK: - GET requests -

        get("/") {
            call.respondText("Hello, I'm your friendly 'on this day'-bot :). v0.4")
        }

        // MARK: - POST requests -

        post("api/space") {
            // Get required information from call
            val body = call.receiveText()
            val signature = call.request.header("X-Space-Public-Key-Signature")
            val timestamp = call.request.header("X-Space-Timestamp")?.toLongOrNull()

            // Ensure call is authorized
            if (signature.isNullOrBlank() || timestamp == null ||
                !spaceClient.verifyWithPublicKey(body, timestamp, signature)
            ) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            // Handle different kinds of payloads
            when (val payload = readPayload(body)) {
                // 1. ListCommandsPayload
                // aka Space requests the list of supported commands
                is ListCommandsPayload -> {
                    //
                    call.respondText(
                        ObjectMapper().writeValueAsString(getSupportedCommands()),
                        ContentType.Application.Json
                    )
                }
                // 2. MessagePayload
                // aka user sent a message to the application
                is MessagePayload -> {
                    val commandName = payload.command()
                    val command = supportedCommands.find { it.name == commandName }
                    if (command == null) {
                        runHelpCommand(payload)
                    } else {
                        launch { command.run(payload) }
                    }
                    call.respond(HttpStatusCode.OK, "")
                }
            }
        }
    }
}
