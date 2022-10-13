package io.github.tscholze.onthisday.network.dtos

import kotlinx.serialization.Serializable

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