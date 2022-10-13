package io.github.tscholze.onthisday.commands

import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.resources.chats
import space.jetbrains.api.runtime.types.*

/**
 * Creates a help (command list) chat message
 *
 * @param client Space client of the requesting instance
 * */
fun helpMessage(client: SpaceClient): ChatMessage {
    return message {
        MessageOutline(
            icon = ApiIcon("calendar"),
            text = "On This Day bot help"
        )
        section {
            text("List of available commands", MessageStyle.PRIMARY)
            fields {
                supportedCommands(client).forEach {
                    field(it.name, it.info)
                }
            }
        }
    }
}

/**
 * Sends a message to the user's chat.
 *
 * @param client Space client of the requesting instance
 * @param userId ID of the user in the instance
 * @param message to send
 */
suspend fun sendMessage(client: SpaceClient, userId: String, message: ChatMessage) {
    client.chats.messages.sendMessage(
        channel = ChannelIdentifier.Profile(ProfileIdentifier.Id(userId)),
        content = message
    )
}
