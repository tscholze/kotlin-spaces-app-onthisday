package io.github.tscholze.onthisday

import kotlinx.serialization.*

// All models are based on:
// https://byabbe.se/on-this-day/#/default/get__month___day__events_json

@Serializable
data class WikipediaEventsResponseContainer (
    val wikipedia: String,
    val date: String,
    val events: List<Event>
)

@Serializable
data class WikipediaDeathsResponseContainer (
    val wikipedia: String,
    val date: String,
    val deaths: List<Death>
)

@Serializable
data class WikipediaBirthsResponseContainer (
    val wikipedia: String,
    val date: String,
    val births: List<Birth>
)

@Serializable
data class Event (
    val year: String,
    val description: String,
    val wikipedia: List<Wikipedia>
)

@Serializable
data class Death (
    val year: String,
    val description: String,
    val wikipedia: List<Wikipedia>
)

@Serializable
data class Birth (
    val year: String,
    val description: String,
    val wikipedia: List<Wikipedia>
)

@Serializable
data class Wikipedia (
    val title: String,
    val wikipedia: String
)

class OnThisDay (
    val topic: Command.Topic,
    val date: String,
    val happenings: List<Happening>,
) {
    companion object {
        fun from(eventsResponseContainer: WikipediaEventsResponseContainer): OnThisDay {
            return OnThisDay(
                Command.Topic.EVENTS,
                eventsResponseContainer.date,
                eventsResponseContainer.events.map {
                    Happening(
                        it.year,
                        it.description,
                        it.wikipedia.first().wikipedia
                    )
                }
            )
        }

        fun from(deathsResponseContainer: WikipediaDeathsResponseContainer): OnThisDay {
            return OnThisDay(
                Command.Topic.DEATHS,
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

        fun from(birthsResponseContainer: WikipediaBirthsResponseContainer): OnThisDay {
            return OnThisDay(
                Command.Topic.BIRTH,
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

data class Happening(
    val year: String,
    val description: String,
    val wikipediaUrl: String
)