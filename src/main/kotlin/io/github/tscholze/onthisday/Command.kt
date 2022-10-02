package io.github.tscholze.onthisday

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import space.jetbrains.api.runtime.helpers.MessageControlGroupBuilder
import space.jetbrains.api.runtime.helpers.commandArguments
import space.jetbrains.api.runtime.helpers.message
import space.jetbrains.api.runtime.types.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Command {
    companion object {

        // MARK: - Private constants -

        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        private val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        // MARK: - Public helper -

        suspend fun runWithPayload(payload: MessagePayload) {
            val args = getArgs(payload)

            // Handle as error if arg parsing failed.
            if (args == null) {
                sendMessage(payload.userId, helpMessage())
                return
            }

            // Request data from remote.
            val onThisDay = requestData(args.topic, args.date)

            // Send populated message to the user
            sendMessage(payload.userId, makeMessage(onThisDay))
        }

        // MARK: - UI Builder -

        private fun makeMessage(container: OnThisDay): ChatMessage {
            return message {
                outline(
                    MessageOutline(
                        icon = ApiIcon("calendar"),
                        text = "Following ${container.topic.name.lowercase()} found on Wikipedia:"
                    )
                )
                section {
                    text("On this day (${container.date}) happened:")

                    container.happenings.reversed().forEach {
                        text(
                            size = MessageTextSize.SMALL,
                            content = "${it.year}:\n${it.description}",
                        )

                        controls {
                            wikipediaButton(it.wikipediaUrl)
                        }

                        divider()
                    }
                }
            }
        }

        private fun MessageControlGroupBuilder.wikipediaButton(urlString: String) {
            val action =  NavigateUrlAction(
                urlString,
                withBackUrl = true,
                openInNewTab = false
            )

            button("Open Wikipedia", action, MessageButtonStyle.SECONDARY)
        }

        // MARK: - Private helper -

        private suspend fun requestData(topic: Topic, date: LocalDate): OnThisDay {
            val url = String.format(topic.urlFormatString, date.monthValue, date.dayOfMonth)

            return when (topic) {
                Topic.EVENTS -> {
                    val container: WikipediaEventsResponseContainer = client.get(url).body()
                    OnThisDay.from(container)
                }

                Topic.BIRTH -> {
                    val container: WikipediaBirthsResponseContainer = client.get(url).body()
                    OnThisDay.from(container)
                }

                else -> {
                    val container: WikipediaDeathsResponseContainer = client.get(url).body()
                    OnThisDay.from(container)
                }
            }
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
                return OnThisDayArgs(LocalDate.now(), Topic.EVENTS)
            }

            // If 1 arg is given, interpret it as:
            //  - Given date
            //  - EVENTS
            else if (rawArgs.count() == 1) {
                return try {
                    val date = LocalDate.parse("${rawArgs.first()}.2022", DATE_FORMATTER)
                    return OnThisDayArgs(date, Topic.EVENTS)
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
                    val topic = Topic.valueOf(rawArgs.elementAt(1).uppercase())
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

    private class OnThisDayArgs(
        val date: LocalDate,
        val topic: Topic
    )

    // MARK: - Topics -

    enum class Topic(val urlFormatString: String) {
        EVENTS("https://byabbe.se/on-this-day/%d/%d/events.json"),
        DEATHS("https://byabbe.se/on-this-day/%d/%d/deaths.json"),
        BIRTH("https://byabbe.se/on-this-day/%d/%d/births.json")
    }
}