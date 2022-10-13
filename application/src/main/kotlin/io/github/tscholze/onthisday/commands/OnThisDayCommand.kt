package io.github.tscholze.onthisday.commands

import io.github.tscholze.onthisday.models.OnThisDay
import io.github.tscholze.onthisday.network.services.ByabbeService
import space.jetbrains.api.runtime.SpaceClient
import space.jetbrains.api.runtime.helpers.MessageControlGroupBuilder
import space.jetbrains.api.runtime.helpers.commandArguments
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.types.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Command manager to evaluate user's input and
 * perform respective tasks to handle the request.
 */
class OnThisDayCommand {
    companion object {

        // MARK: - Private constants -

        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        // MARK: - Public helper -

        /**
         * Runs the command with given payload
         *
         * @param payload Payload to handle.
         */
        suspend fun runWithPayload(client: SpaceClient, payload: MessagePayload) {
            val args = getArgs(payload)

            // Handle as error if arg parsing failed.
            if (args == null) {
                sendMessage(client, payload.userId, helpMessage(client))
                return
            }

            // Request data from remote.
            val onThisDay = ByabbeService.requestData(args.topic, args.date)

            // Send populated message to the user
            sendMessage(client, payload.userId, makeMessage(onThisDay))
        }

        // MARK: - UI Builder -

        private fun makeMessage(container: OnThisDay): ChatMessage {
            // Create reply message
            return message {
                // Outline (scope) the message
                outline(
                    MessageOutline(
                        icon = ApiIcon("calendar"),
                        text = "Following ${container.topic.name.lowercase()} found on Wikipedia:"
                    )
                )

                // Actual content
                section {

                    // Title
                    text("On this day (${container.date}) happened:")

                    // Loop throw all happenings
                    container.happenings.reversed().forEach {

                        // Happening description
                        text(
                            size = MessageTextSize.SMALL,
                            content = "${it.year}:\n${it.description ?: "No information found."}",
                        )

                        // Wikipedia button
                        controls {
                            wikipediaButton(it.wikipediaUrl)
                        }

                        // Line / divider between all the happenings.
                        divider()
                    }
                }
            }
        }

        private fun MessageControlGroupBuilder.wikipediaButton(urlString: String) {
            // Create "open browser" action
            val action =  NavigateUrlAction(
                urlString,
                withBackUrl = true,
                openInNewTab = false
            )

            // Return configurated button
            button("Open Wikipedia", action, MessageButtonStyle.SECONDARY)
        }

        private fun getArgs(payload: MessagePayload): OnThisDayArgs? {
            // Get raw args
            val rawArgs = payload.commandArguments()
                ?.trim()
                ?.splitToSequence(" ")
                ?.filter { it.isNotEmpty() }
                ?: emptySequence()

            // If no args given, interpret it as:
            //  - today
            //  - EVENTS
            if (rawArgs.count() == 0) {
                return OnThisDayArgs(LocalDate.now(), ByabbeService.Companion.Topic.EVENTS)
            }

            // If 1 arg is given, interpret it as:
            //  - Given date
            //  - EVENTS
            else if (rawArgs.count() == 1) {
                return try {
                    val date = LocalDate.parse("${rawArgs.first()}.2022", DATE_FORMATTER)
                    return OnThisDayArgs(date, ByabbeService.Companion.Topic.EVENTS)
                } catch (error: DateTimeParseException) {
                    null
                }
            }

            // If 2 arg is given, interpret it as:
            //  - Given date
            //  - Given topic
            else if (rawArgs.count() == 2) {
                return try {
                    val date = LocalDate.parse("${rawArgs.first()}.2022", DATE_FORMATTER)
                    val topic = ByabbeService.Companion.Topic.valueOf(rawArgs.elementAt(1).uppercase())
                    return OnThisDayArgs(date, topic)
                } catch (error: Exception) {
                    null
                }
            }

            // If more args are given, interpret it as:
            //  - Error
            else {
                return null
            }
        }
    }

    // MARK: - Args -

    /**
     * Argument class which contains all OTD parameters
     *
     * @property date The date (day, month) the user picked
     * @property topic The topic of happenings the user picked
     */
    private class OnThisDayArgs(
        val date: LocalDate,
        val topic: ByabbeService.Companion.Topic
    )
}