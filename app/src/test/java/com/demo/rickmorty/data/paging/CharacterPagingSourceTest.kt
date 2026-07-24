package com.demo.rickmorty.data.paging

import androidx.paging.PagingSource
import com.demo.rickmorty.data.remote.CharacterApi
import com.demo.rickmorty.data.remote.dto.CharacterDto
import com.demo.rickmorty.data.remote.dto.CharacterResponseDto
import com.demo.rickmorty.data.remote.dto.LocationRefDto
import com.demo.rickmorty.data.remote.dto.PageInfoDto
import com.demo.rickmorty.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class CharacterPagingSourceTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api: CharacterApi = mockk()

    private fun dto(id: Int) = CharacterDto(
        id = id,
        name = "Character $id",
        status = "Alive",
        species = "Human",
        gender = "Male",
        image = "https://example.com/$id.png",
        origin = LocationRefDto("Earth"),
        location = LocationRefDto("Citadel")
    )

    @Test
    fun `load returns page with correct prev and next keys`() = runTest {
        coEvery { api.getCharacters(page = 1, name = null) } returns CharacterResponseDto(
            info = PageInfoDto(count = 40, pages = 2, next = "https://api/character?page=2", prev = null),
            results = listOf(dto(1), dto(2))
        )

        val pagingSource = CharacterPagingSource(api)
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).hasSize(2)
        assertThat(page.data.first().name).isEqualTo("Character 1")
        assertThat(page.prevKey).isNull()
        assertThat(page.nextKey).isEqualTo(2)
    }

    @Test
    fun `load returns null nextKey on last page`() = runTest {
        coEvery { api.getCharacters(page = 2, name = null) } returns CharacterResponseDto(
            info = PageInfoDto(count = 40, pages = 2, next = null, prev = "https://api/character?page=1"),
            results = listOf(dto(3))
        )

        val pagingSource = CharacterPagingSource(api)
        val result = pagingSource.load(
            PagingSource.LoadParams.Append(key = 2, loadSize = 20, placeholdersEnabled = false)
        )

        val page = result as PagingSource.LoadResult.Page
        assertThat(page.nextKey).isNull()
        assertThat(page.prevKey).isEqualTo(1)
    }

    @Test
    fun `load returns Error result on IOException`() = runTest {
        coEvery { api.getCharacters(page = 1, name = null) } throws IOException("no network")

        val pagingSource = CharacterPagingSource(api)
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }

    @Test
    fun `load passes search query through to the api`() = runTest {
        coEvery { api.getCharacters(page = 1, name = "rick") } returns CharacterResponseDto(
            info = PageInfoDto(count = 1, pages = 1, next = null, prev = null),
            results = listOf(dto(1))
        )

        val pagingSource = CharacterPagingSource(api, query = "rick")
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).hasSize(1)
    }
}
