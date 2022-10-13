package io.github.tscholze.onthisday.network.dtos

import kotlinx.serialization.Serializable

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
    val description: String? = null,
    val wikipedia: List<Wikipedia>
)