package com.demo.rickmorty.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.demo.rickmorty.data.mapper.toDomain
import com.demo.rickmorty.data.remote.CharacterApi
import com.demo.rickmorty.domain.model.Character
import retrofit2.HttpException
import java.io.IOException

/**
 * Drives infinite scroll: Paging 3 calls load() with the next key whenever
 * the UI scrolls near the end of the currently loaded list. We derive the
 * next page key from the API's own "next" pagination link, and stop
 * (return null) once the server reports no further pages.
 */
class CharacterPagingSource(
    private val api: CharacterApi,
    private val query: String? = null
) : PagingSource<Int, Character>() {

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        // Try to keep the user near their current position on refresh.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: STARTING_PAGE
        return try {
            val response = api.getCharacters(page = page, name = query)
            val characters = response.results.map { it.toDomain() }

            LoadResult.Page(
                data = characters,
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (response.info.next != null) page + 1 else null
            )
        } catch (e: IOException) {
            // No network / timeout
            LoadResult.Error(e)
        } catch (e: HttpException) {
            // Non-2xx response
            LoadResult.Error(e)
        } catch (e: Exception) {
            // Includes the "no results for this page" 404 the API returns
            // for an empty search - treat it as an empty (final) page rather
            // than a hard error so the UI doesn't spuriously show retry.
            if (page == STARTING_PAGE) {
                LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            } else {
                LoadResult.Error(e)
            }
        }
    }

    companion object {
        const val STARTING_PAGE = 1
        const val PAGE_SIZE = 20
    }
}
