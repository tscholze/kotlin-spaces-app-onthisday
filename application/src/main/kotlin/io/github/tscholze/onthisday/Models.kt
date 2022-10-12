package io.github.tscholze.onthisday

import io.github.tscholze.onthisday.commands.OnThisDayCommand
import kotlinx.serialization.*


// MARK: - Business models -

/**
 * Topic agnostic happening
 *
 * @property year Year of the happening
 * @property description Description of the happening
 * @property wikipediaUrl Url to the corresponding Wikipedia web page
 */
data class Happening(
    val year: String,
    val description: String,
    val wikipediaUrl: String
)

/**
 * Topic agnostic container of what happened on this day
 *
 * @property topic The requested topic
 * @property date The date of all the happenings
 * @property happenings What happened on this day for the given topic
 */
class OnThisDay (
    val topic: OnThisDayCommand.Topic,
    val date: String,
    val happenings: List<Happening>,
) {
    companion object {
        /**
         * Gets an [OnThisDay] instance for given [WikipediaEventsResponseContainer].
         */
        fun from(eventsResponseContainer: WikipediaEventsResponseContainer): OnThisDay {
            return OnThisDay(
                OnThisDayCommand.Topic.EVENTS,
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

        /**
         * Gets an [OnThisDay] instance for given [WikipediaDeathsResponseContainer].
         */
        fun from(deathsResponseContainer: WikipediaDeathsResponseContainer): OnThisDay {
            return OnThisDay(
                OnThisDayCommand.Topic.DEATHS,
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
                OnThisDayCommand.Topic.BIRTH,
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

// MARK: - DTOs -

// All models are based on:
// https://byabbe.se/on-this-day/#/default/get__month___day__events_json

/**
 * DTO container of API "Events" endpoint
 *
 * @property wikipedia URL to Wikipedia summary page
 * @property date Date string with day and month of the request
 * @property events List of events that happened on this day in history
 */
@Serializable
data class WikipediaEventsResponseContainer (
    val wikipedia: String,
    val date: String,
    val events: List<Event>
)

/**
 * DTO container of API "Deaths" endpoint
 *
 * @property wikipedia URL to Wikipedia summary page
 * @property date Date string with day and month of the request
 * @property deaths List of deaths that happened on this day in history
 */
@Serializable
data class WikipediaDeathsResponseContainer (
    val wikipedia: String,
    val date: String,
    val deaths: List<Death>
)

/**
 * DTO container of API "Births" endpoint
 *
 * @property wikipedia URL to Wikipedia summary page
 * @property date Date string with day and month of the request
 * @property births List of births that happened on this day in history
 */
@Serializable
data class WikipediaBirthsResponseContainer (
    val wikipedia: String,
    val date: String,
    val births: List<Birth>
)

/**
 * DTO of an event
 *
 * @property year: Year of the event
 * @property description: Description about what happened in this year
 * @property wikipedia: List of Wikipedia entries for this event
 */
@Serializable
data class Event (
    val year: String,
    val description: String,
    val wikipedia: List<Wikipedia>
)

/**
 * DTO of a death
 *
 * @property year: Year of the death
 * @property description: Description about which death it was
 * @property wikipedia: List of Wikipedia entries for the died person.
 */
@Serializable
data class Death (
    val year: String,
    val description: String,
    val wikipedia: List<Wikipedia>
)

/**
 * DTO of a birth
 *
 * @property year: Year of the birth
 * @property description: Description about which birth it was
 * @property wikipedia: List of Wikipedia entries for the birthed person.
 */
@Serializable
data class Birth (
    val year: String,
    val description: String,
    val wikipedia: List<Wikipedia>
)

/**
 * DTO of a Wikipedia entry
 *
 * @property title: Title of the entry
 * @property wikipedia: Url string to the Wikipedia web page
 */
@Serializable
data class Wikipedia (
    val title: String,
    val wikipedia: String
)