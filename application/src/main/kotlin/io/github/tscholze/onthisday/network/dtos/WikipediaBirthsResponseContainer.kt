package io.github.tscholze.onthisday.network.dtos

import kotlinx.serialization.Serializable

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