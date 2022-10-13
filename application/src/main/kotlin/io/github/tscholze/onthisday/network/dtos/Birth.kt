package io.github.tscholze.onthisday.network.dtos

import io.github.tscholze.onthisday.network.dtos.Wikipedia
import kotlinx.serialization.Serializable

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
    val description: String? = null,
    val wikipedia: List<Wikipedia>
)