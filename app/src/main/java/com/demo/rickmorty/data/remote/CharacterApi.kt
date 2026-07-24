package com.demo.rickmorty.data.remote

import com.demo.rickmorty.data.remote.dto.CharacterDto
import com.demo.rickmorty.data.remote.dto.CharacterResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Thin Retrofit interface - talks to https://rickandmortyapi.com/api/character.
 * The API paginates results server-side (20 items/page), which is exactly
 * what our PagingSource needs.
 */
interface CharacterApi {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null
    ): CharacterResponseDto

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id") id: Int
    ): CharacterDto
}
