package com.demo.rickmorty.presentation.characterdetail

import com.demo.rickmorty.domain.model.Character

data class CharacterDetailState(
    val isLoading: Boolean = false,
    val character: Character? = null,
    val error: String? = null
)

sealed interface CharacterDetailIntent {
    data object OnBackClicked : CharacterDetailIntent
    data object OnRetryClicked : CharacterDetailIntent
}

sealed interface CharacterDetailEffect {
    data object NavigateBack : CharacterDetailEffect
}
