package io.github.tscholze.onthisday.commands

import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.types.CommandDetail
import space.jetbrains.api.runtime.types.MessagePayload

class ApplicationCommand(
    val name: String,
    val info: String,
    val run: suspend (payload: MessagePayload) -> Unit
) {
    /**
     * [CommandDetail] is returned to Space with an information about the command. List of commands
     * is shown to the user.
     */
    fun toSpaceCommand() = CommandDetail(name, info)
}

/**
 * Gets all supported commands which the user can pick from.
 */
fun supportedCommands(client: SpaceClient) = listOf(
    // otd command.
    ApplicationCommand(
        "otd",
        "To get 'on this day' information.\nYou can specify the date (dd.MM) or date and topic (events, birth, deaths)",
    ) { payload -> OnThisDayCommand.runWithPayload(client, payload) },

    // Help command.
    ApplicationCommand(
        "help",
        "Show this help",
    ) { payload -> sendMessage(client, payload.userId, helpMessage(client)) },
)