package com.demo.rickmorty.presentation.characterlist

/**
 * MVI contract for the character list screen.
 *
 * State  - single immutable snapshot the UI renders (MVVM: exposed as StateFlow).
 * Intent - every possible user action, funneled through one entry point.
 * Effect - one-off events (snackbars, navigation) that should not be replayed
 *          on configuration change, delivered via a Channel/SharedFlow.
 */
data class CharacterListState(
    val searchQuery: String = "",
    val isSearchBarVisible: Boolean = false
)

sealed interface CharacterListIntent {
    data class OnSearchQueryChanged(val query: String) : CharacterListIntent
    data object OnToggleSearchBar : CharacterListIntent
    data object OnClearSearch : CharacterListIntent
    data class OnCharacterClicked(val characterId: Int) : CharacterListIntent
}

sealed interface CharacterListEffect {
    data class ShowMessage(val message: String) : CharacterListEffect
    data class NavigateToDetail(val characterId: Int) : CharacterListEffect
}
