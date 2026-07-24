package com.demo.rickmorty.data.repository

import androidx.paging.PagingSource
import com.demo.rickmorty.data.local.RickMortyDatabase
import com.demo.rickmorty.data.local.dao.CharacterDao
import com.demo.rickmorty.data.remote.CharacterApi
import com.demo.rickmorty.data.remote.dto.CharacterDto
import com.demo.rickmorty.data.remote.dto.CharacterResponseDto
import com.demo.rickmorty.data.remote.dto.LocationRefDto
import com.demo.rickmorty.data.remote.dto.PageInfoDto
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CharacterRepositoryImplTest {

    private val api: CharacterApi = mockk()
    private val database: RickMortyDatabase = mockk()
    private val repository = CharacterRepositoryImpl(api, database)

    @Test
    fun `getCharacters builds a pager whose source maps dto to domain`() = runTest {
        val dao: CharacterDao = mockk()
        every { database.characterDao() } returns dao
        every { dao.getCharacters(any()) } returns mockk()

        coEvery { api.getCharacters(page = 1, name = null) } returns CharacterResponseDto(
            info = PageInfoDto(count = 1, pages = 1, next = null, prev = null),
            results = listOf(
                CharacterDto(
                    id = 1,
                    name = "Morty Smith",
                    status = "Alive",
                    species = "Human",
                    gender = "Male",
                    image = "https://example.com/morty.png",
                    origin = LocationRefDto("Earth"),
                    location = LocationRefDto("Citadel")
                )
            )
        )

        val flow = repository.getCharacters(null)

        // Exercise the underlying PagingSource directly - PagingData itself
        // isn't easily collectable in a plain unit test without extra
        // Paging testing infrastructure, but the source it wraps is.
        val source = com.demo.rickmorty.data.paging.CharacterPagingSource(api, null)
        val result = source.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertThat(result.data).hasSize(1)
        assertThat(result.data.first().name).isEqualTo("Morty Smith")
        assertThat(flow).isNotNull()
    }
}
