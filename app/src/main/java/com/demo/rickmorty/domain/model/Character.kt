package com.demo.rickmorty.domain.model

/**
 * Pure domain model - no framework/DTO dependencies.
 * This is what the UI layer consumes.
 */
data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val gender: String,
    val imageUrl: String,
    val originName: String,
    val locationName: String
)

enum class CharacterStatus {
    ALIVE, DEAD, UNKNOWN;

    companion object {
        fun fromRaw(raw: String): CharacterStatus = when (raw.lowercase()) {
            "alive" -> ALIVE
            "dead" -> DEAD
            else -> UNKNOWN
        }
    }
}
