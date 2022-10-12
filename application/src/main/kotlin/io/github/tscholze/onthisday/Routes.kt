package io.github.tscholze.onthisday

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.tscholze.onthisday.commands.supportedCommands
import io.github.tscholze.onthisday.db.saveRefreshTokenData
import io.github.tscholze.onthisday.network.runHelpCommand
import io.github.tscholze.onthisday.network.setUiExtensions
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.Space
import space.jetbrains.api.runtime.helpers.*
import space.jetbrains.api.runtime.types.InitPayload
import space.jetbrains.api.runtime.types.ListCommandsPayload
import space.jetbrains.api.runtime.types.MessagePayload
import space.jetbrains.api.runtime.types.RefreshTokenPayload

@OptIn(ExperimentalSpaceSdkApi::class)
fun Application.configureRouting() {
    routing {

        // MARK: - GET requests -

        get("/") {
            call.respondText("Hello, I'm your friendly 'on this day'-bot :). v0.5")
        }

        // MARK: - POST requests -

        post("api/space") {

            val ktorRequestAdapter = object : RequestAdapter {
                override suspend fun receiveText() =
                    call.receiveText()

                override fun getHeader(headerName: String) =
                    call.request.header(headerName)

                override suspend fun respond(httpStatusCode: Int, body: String) =
                    call.respond(HttpStatusCode.fromValue(httpStatusCode), body)
            }

            Space.processPayload(ktorRequestAdapter, spaceHttpClient, AppInstanceStorage) { payload ->

                val client = clientWithClientCredentials()

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
