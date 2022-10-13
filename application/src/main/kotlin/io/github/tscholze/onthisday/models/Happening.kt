package io.github.tscholze.onthisday

/**
 * Topic agnostic happening
 *
 * @property year Year of the happening
 * @property description Description of the happening
 * @property wikipediaUrl Url to the corresponding Wikipedia web page
 */
data class Happening(
    val year: String,
    val description: String? = null,
    val wikipediaUrl: String
)