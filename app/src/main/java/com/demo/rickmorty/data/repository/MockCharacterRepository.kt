package com.demo.rickmorty.data.repository

import androidx.paging.PagingData
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.model.CharacterStatus
import com.demo.rickmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * A Mock implementation used for Unit Testing and UI Demos.
 * Demonstrates LSP (Liskov Substitution Principle): 
 * This class can replace CharacterRepositoryImpl anywhere in the app 
 * without breaking the business logic or UI expectations.
 */
class MockCharacterRepository @Inject constructor() : CharacterRepository {

    private val mockCharacters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez (Mock)",
            status = CharacterStatus.ALIVE,
            species = "Human",
            gender = "Male",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            originName = "Earth (C-137)",
            locationName = "Citadel of Ricks"
        ),
        Character(
            id = 2,
            name = "Morty Smith (Mock)",
            status = CharacterStatus.ALIVE,
            species = "Human",
            gender = "Male",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
            originName = "unknown",
            locationName = "Citadel of Ricks"
        ),
        Character(
            id = 3,
            name = "Summer Smith (Mock)",
            status = CharacterStatus.ALIVE,
            species = "Human",
            gender = "Female",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/3.jpeg",
            originName = "Earth (Replacement Dimension)",
            locationName = "Earth"
        )
    )

    override fun getCharacters(query: String?): Flow<PagingData<Character>> {
        val filteredList = if (query.isNullOrBlank()) {
            mockCharacters
        } else {
            mockCharacters.filter { it.name.contains(query, ignoreCase = true) }
        }
        return flowOf(PagingData.from(filteredList))
    }

    override suspend fun getCharacter(id: Int): Character {
        return mockCharacters.find { it.id == id }
            ?: throw NoSuchElementException("Character with ID $id not found in Mock")
    }
}
