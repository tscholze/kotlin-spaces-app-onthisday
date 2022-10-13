package io.github.tscholze.onthisday.models

import io.github.tscholze.onthisday.Happening
import io.github.tscholze.onthisday.network.dtos.WikipediaBirthsResponseContainer
import io.github.tscholze.onthisday.network.dtos.WikipediaDeathsResponseContainer
import io.github.tscholze.onthisday.network.dtos.WikipediaEventsResponseContainer
import io.github.tscholze.onthisday.network.services.ByabbeService

/**
 * Topic agnostic container of what happened on this day
 *
 * @property topic The requested topic
 * @property date The date of all the happenings
 * @property happenings What happened on this day for the given topic
 */
class OnThisDay (
    val topic: ByabbeService.Companion.Topic,
    val date: String,
    val happenings: List<Happening>,
) {
    companion object {
        /**
         * Gets an [OnThisDay] instance for given [WikipediaEventsResponseContainer].
         */
        fun from(eventsResponseContainer: WikipediaEventsResponseContainer): OnThisDay {
            return OnThisDay(
                ByabbeService.Companion.Topic.EVENTS,
                eventsResponseContainer.date,
                eventsResponseContainer.events
                    .reversed()
                    .map {
                        Happening(
                            it.year,
                            it.description,
                            it.wikipedia.first().wikipedia
                        )
                    }
            )
        }

        /**
         * Gets an [OnThisDay] instance for given [WikipediaDeathsResponseContainer].
         */
        fun from(deathsResponseContainer: WikipediaDeathsResponseContainer): OnThisDay {
            return OnThisDay(
                ByabbeService.Companion.Topic.DEATHS,
                deathsResponseContainer.date,
                deathsResponseContainer.deaths.map {
                    Happening(
                        it.year,
                        it.description,
                        it.wikipedia.first().wikipedia
                    )
                }
            )
        }

        /**
         * Gets an [OnThisDay] instance for given [WikipediaBirthsResponseContainer].
         */
        fun from(birthsResponseContainer: WikipediaBirthsResponseContainer): OnThisDay {
            return OnThisDay(
                ByabbeService.Companion.Topic.BIRTH,
                birthsResponseContainer.date,
                birthsResponseContainer.births.map {
                    Happening(
                        it.year,
                        it.description,
                        it.wikipedia.first().wikipedia
                    )
                }
            )
        }
    }
}