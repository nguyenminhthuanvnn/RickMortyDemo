package com.demo.rickmorty.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.demo.rickmorty.data.local.RickMortyDatabase
import com.demo.rickmorty.data.paging.CharacterPagingSource
import com.demo.rickmorty.data.paging.CharacterRemoteMediator
import com.demo.rickmorty.data.remote.CharacterApi
import com.demo.rickmorty.data.mapper.toDomain
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Concrete implementation of the domain repository contract. This is the
 * ONLY class in the app that knows both about Retrofit (data) and about
 * the domain Character model - everything else depends on abstractions.
 */
class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApi,
    private val database: RickMortyDatabase
) : CharacterRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(query: String?): Flow<PagingData<Character>> {
        val pagingSourceFactory = { database.characterDao().getCharacters(query ?: "") }

        return Pager(
            config = PagingConfig(
                pageSize = CharacterPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            remoteMediator = CharacterRemoteMediator(
                query = query,
                api = api,
                database = database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getCharacter(id: Int): Character {
        // Try local first, then remote
        return database.characterDao().getCharacter(id)?.toDomain()
            ?: api.getCharacter(id).toDomain()
    }
}
