package com.demo.rickmorty.presentation.characterdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.rickmorty.domain.usecase.GetCharacterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterUseCase: GetCharacterUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterId: Int = checkNotNull(savedStateHandle["characterId"])

    private val _state = MutableStateFlow(CharacterDetailState())
    val state: StateFlow<CharacterDetailState> = _state.asStateFlow()

    private val _effect = Channel<CharacterDetailEffect>(Channel.BUFFERED)
    val effect: Flow<CharacterDetailEffect> = _effect.receiveAsFlow()

    init {
        loadCharacter()
    }

    fun onIntent(intent: CharacterDetailIntent) {
        when (intent) {
            CharacterDetailIntent.OnBackClicked -> {
                _effect.trySend(CharacterDetailEffect.NavigateBack)
            }
            CharacterDetailIntent.OnRetryClicked -> loadCharacter()
        }
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getCharacterUseCase(characterId)
                .onSuccess { character ->
                    _state.update { it.copy(isLoading = false, character = character) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}
