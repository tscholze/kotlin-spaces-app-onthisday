package io.github.tscholze.onthisday.network.dtos

import kotlinx.serialization.Serializable

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
    val description: String? = null,
    val wikipedia: List<Wikipedia>
)