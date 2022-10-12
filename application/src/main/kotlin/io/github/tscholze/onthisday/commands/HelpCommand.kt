package io.github.tscholze.onthisday.commands

import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.types.*

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