package com.demo.rickmorty.presentation.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * MVVM: exposes StateFlow<CharacterListState> for Compose to collect.
 * MVI: single onIntent() entry point - the UI never mutates state directly,
 * it only dispatches intents, keeping data flow unidirectional.
 */
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CharacterListState())
    val state: StateFlow<CharacterListState> = _state.asStateFlow()

    private val _effect = Channel<CharacterListEffect>(Channel.BUFFERED)
    val effect: Flow<CharacterListEffect> = _effect.receiveAsFlow()

    private val searchQueryFlow = MutableStateFlow("")

    /**
     * Paging stream re-triggers a fresh Pager whenever the (debounced) search
     * query changes, and is cached across configuration changes via
     * cachedIn(viewModelScope) so rotating the device doesn't re-fetch.
     */
    val pagingDataFlow: Flow<PagingData<Character>> = searchQueryFlow
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            getCharactersUseCase(query.ifBlank { null })
        }
        .cachedIn(viewModelScope)

    fun onIntent(intent: CharacterListIntent) {
        when (intent) {
            is CharacterListIntent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
                searchQueryFlow.value = intent.query
            }

            CharacterListIntent.OnToggleSearchBar -> {
                _state.update { it.copy(isSearchBarVisible = !it.isSearchBarVisible) }
                if (!_state.value.isSearchBarVisible) {
                    clearSearch()
                }
            }

            CharacterListIntent.OnClearSearch -> clearSearch()

            is CharacterListIntent.OnCharacterClicked -> {
                _effect.trySend(CharacterListEffect.NavigateToDetail(intent.characterId))
            }
        }
    }

    private fun clearSearch() {
        _state.update { it.copy(searchQuery = "") }
        searchQueryFlow.value = ""
    }
}
