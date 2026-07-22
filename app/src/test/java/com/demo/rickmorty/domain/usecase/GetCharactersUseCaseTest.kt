package com.demo.rickmorty.domain.usecase

import androidx.paging.PagingData
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.model.CharacterStatus
import com.demo.rickmorty.domain.repository.CharacterRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetCharactersUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = GetCharactersUseCase(repository)

    private val sampleCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.ALIVE,
        species = "Human",
        gender = "Male",
        imageUrl = "https://example.com/rick.png",
        originName = "Earth",
        locationName = "Citadel of Ricks"
    )

    @Test
    fun `invoke delegates to repository with given query`() = runTest {
        val expected = flowOf(PagingData.from(listOf(sampleCharacter)))
        every { repository.getCharacters("rick") } returns expected

        val result = useCase(query = "rick")

        assertThat(result).isEqualTo(expected)
        verify(exactly = 1) { repository.getCharacters("rick") }
    }

    @Test
    fun `invoke with no query passes null through to repository`() = runTest {
        val expected = flowOf(PagingData.from(listOf(sampleCharacter)))
        every { repository.getCharacters(null) } returns expected

        useCase()

        verify(exactly = 1) { repository.getCharacters(null) }
    }
}
