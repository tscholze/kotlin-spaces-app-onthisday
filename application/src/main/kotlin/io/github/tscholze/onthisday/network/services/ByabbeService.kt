package io.github.tscholze.onthisday.network.services

import io.github.tscholze.onthisday.models.OnThisDay
import io.github.tscholze.onthisday.network.dtos.WikipediaBirthsResponseContainer
import io.github.tscholze.onthisday.network.dtos.WikipediaDeathsResponseContainer
import io.github.tscholze.onthisday.network.dtos.WikipediaEventsResponseContainer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import java.time.LocalDate

class ByabbeService {
    companion object {

        // MARK: - Private properties -

        private val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }


        // MARK: - Helper -

        suspend fun requestData(topic: Topic, date: LocalDate): OnThisDay {
            // Create url from given parameters
            val url = String.format(topic.urlFormatString, date.monthValue, date.dayOfMonth)

            // Decide which endpoint has to be called.
            return when (topic) {
                Topic.EVENTS -> {
                    val container: WikipediaEventsResponseContainer = client.get(url).body()
                    OnThisDay.from(container)
                }

                Topic.BIRTH -> {
                    val container: WikipediaBirthsResponseContainer = client.get(url).body()
                    OnThisDay.from(container)
                }

                Topic.DEATHS -> {
                    val container: WikipediaDeathsResponseContainer = client.get(url).body()
                    OnThisDay.from(container)
                }
            }
        }

        // MARK: - Topics -

        /**
         * Contains all available [Happening] Topics
         */
        enum class Topic(val urlFormatString: String) {
            /// Events like "first plane flight"
            EVENTS("https://byabbe.se/on-this-day/%d/%d/events.json"),

            /// Deaths like "Steve Wozniak"
            DEATHS("https://byabbe.se/on-this-day/%d/%d/deaths.json"),

            /// Births like "Albert Einstein"
            BIRTH("https://byabbe.se/on-this-day/%d/%d/births.json")
        }
    }
}

