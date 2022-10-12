package io.github.tscholze.onthisday.network

import io.github.tscholze.onthisday.commands.helpMessage
import io.github.tscholze.onthisday.commands.sendMessage
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import space.jetbrains.api.ExperimentalSpaceSdkApi
import space.jetbrains.api.runtime.helpers.ProcessingScope
import space.jetbrains.api.runtime.resources.applications
import space.jetbrains.api.runtime.types.*

@OptIn(ExperimentalSpaceSdkApi::class)
suspend fun ProcessingScope.setUiExtensions() {
    clientWithClientCredentials().applications.setUiExtensions(
        contextIdentifier = GlobalPermissionContextIdentifier,
        extensions = listOf(
            ChatBotUiExtensionIn,
        )
    )
}

@OptIn(ExperimentalSpaceSdkApi::class)
suspend fun ProcessingScope.runHelpCommand(userId: String) {
    val client = clientWithClientCredentials()
    sendMessage(client, userId, helpMessage(client))
}