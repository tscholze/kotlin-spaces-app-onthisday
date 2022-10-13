package io.github.tscholze.onthisday.network.dtos

import kotlinx.serialization.Serializable

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