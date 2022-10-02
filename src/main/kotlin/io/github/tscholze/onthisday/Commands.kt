package io.github.tscholze.onthisday

import space.jetbrains.api.runtime.types.CommandDetail
import space.jetbrains.api.runtime.types.Commands
import space.jetbrains.api.runtime.types.ListCommandsPayload
import space.jetbrains.api.runtime.types.MessagePayload

/**
 * A command that the application can execute.
 */
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

val supportedCommands = listOf(
    // otd command.
    ApplicationCommand(
        "otd",
        "To get 'on this day' information.\nYou can specify the date (dd.MM) or date and topic (events, birth, deaths)",
    ) { payload -> Command.runWithPayload(payload) },

    // Help command.
    ApplicationCommand(
        "help",
        "Show this help",
    ) { payload -> runHelpCommand(payload) },
)

/**
 * Response to the [ListCommandsPayload]. Space will display the returned commands as commands supported
 * by your application.
 */
fun getSupportedCommands() = Commands(
    supportedCommands.map {
        it.toSpaceCommand()
    }
)
