package com.demo.rickmorty.domain.usecase

import androidx.paging.PagingData
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Single-responsibility use case, invoked via operator fun so it reads like
 * a function call from the ViewModel: getCharactersUseCase(query).
 */
class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(query: String? = null): Flow<PagingData<Character>> {
        return repository.getCharacters(query)
    }
}
