package io.github.tscholze.onthisday

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.tscholze.onthisday.commands.helpMessage
import io.github.tscholze.onthisday.commands.sendMessage
import io.github.tscholze.onthisday.commands.supportedCommands
import io.github.tscholze.onthisday.db.saveRefreshTokenData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.Space
import space.jetbrains.api.runtime.helpers.*
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.types.*

@OptIn(ExperimentalSpaceSdkApi::class)
fun Application.configureRouting() {
    routing {

        // MARK: - GET requests -

        // Helpful simple GET request to check if the service is running with which version.
        get("/") {
            call.respondText("Hello, I'm your friendly 'on this day'-bot :). v0.5")
        }

        // MARK: - POST requests -

        // Space API request.
        post("api/space") {

            // Custom adapter
            val ktorRequestAdapter = object : RequestAdapter {
                override suspend fun receiveText() =
                    call.receiveText()

                override fun getHeader(headerName: String) =
                    call.request.header(headerName)

                override suspend fun respond(httpStatusCode: Int, body: String) =
                    call.respond(HttpStatusCode.fromValue(httpStatusCode), body)
            }

            // Process payloads
            Space.processPayload(ktorRequestAdapter, spaceHttpClient, AppInstanceStorage) { payload ->

                // Get client
                val client = clientWithClientCredentials()

                // Handle different types of payload
                when (payload) {

                    // Handle InitPayload
                    is InitPayload -> {
                        setUiExtensions()
                        SpaceHttpResponse.RespondWithOk
                    }

                    // Handle RefreshTokenPayload
                    is RefreshTokenPayload -> {
                        saveRefreshTokenData(payload)
                        SpaceHttpResponse.RespondWithOk
                    }

                    // Handle ListCommandsPayload
                    is ListCommandsPayload -> {
                        call.respondText(
                            ObjectMapper().writeValueAsString(supportedCommands(client).map { it.toSpaceCommand() }),
                            ContentType.Application.Json
                        )
                        SpaceHttpResponse.RespondWithOk
                    }

                    // Handle MessagePayload
                    is MessagePayload -> {
                        val command = supportedCommands(client)
                            .find { it.name == payload.command() }

                        // Send "help command" if no command given
                        if (command == null) {
                            runHelpCommand(payload.userId)
                        } else {
                            launch { command.run(payload) }
                        }

                        call.respond(HttpStatusCode.OK, "")
                        SpaceHttpResponse.RespondWithOk
                    }

                    // Handle everything else
                    else -> {
                        call.respond(HttpStatusCode.OK)
                        SpaceHttpResponse.RespondWithOk
                    }
                }
            }
        }
    }
}

/**
 * Sets all required permissions of the app.
 */
@OptIn(ExperimentalSpaceSdkApi::class)
private suspend fun ProcessingScope.setUiExtensions() {
    clientWithClientCredentials().applications.setUiExtensions(
        contextIdentifier = GlobalPermissionContextIdentifier,
        extensions = listOf(
            ChatBotUiExtensionIn,
        )
    )
}

@OptIn(ExperimentalSpaceSdkApi::class)
private suspend fun ProcessingScope.runHelpCommand(userId: String) {
    val client = clientWithClientCredentials()
    sendMessage(client, userId, helpMessage(client))
}