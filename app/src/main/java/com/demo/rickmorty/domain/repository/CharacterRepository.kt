package com.demo.rickmorty.domain.repository

import androidx.paging.PagingData
import com.demo.rickmorty.domain.model.Character
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level contract. The presentation layer depends only on this
 * abstraction, never on the concrete data-layer implementation
 * (Dependency Inversion - Clean Architecture).
 */
interface CharacterRepository {
    fun getCharacters(query: String? = null): Flow<PagingData<Character>>
    suspend fun getCharacter(id: Int): Character
}
