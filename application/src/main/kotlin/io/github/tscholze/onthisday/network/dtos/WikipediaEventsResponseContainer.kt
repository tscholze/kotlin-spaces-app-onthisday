package io.github.tscholze.onthisday.network.dtos

import kotlinx.serialization.Serializable

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