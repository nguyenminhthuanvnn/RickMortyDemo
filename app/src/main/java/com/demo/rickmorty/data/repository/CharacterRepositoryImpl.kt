package com.demo.rickmorty.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.demo.rickmorty.data.paging.CharacterPagingSource
import com.demo.rickmorty.data.remote.CharacterApi
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Concrete implementation of the domain repository contract. This is the
 * ONLY class in the app that knows both about Retrofit (data) and about
 * the domain Character model - everything else depends on abstractions.
 */
class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApi
) : CharacterRepository {

    override fun getCharacters(query: String?): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = CharacterPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = { CharacterPagingSource(api, query) }
        ).flow
    }
}
