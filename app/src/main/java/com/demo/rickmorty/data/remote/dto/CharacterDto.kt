package com.demo.rickmorty.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharacterResponseDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>
)

@JsonClass(generateAdapter = true)
data class PageInfoDto(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val prev: String? = null
)

@JsonClass(generateAdapter = true)
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val origin: LocationRefDto,
    val location: LocationRefDto
)

@JsonClass(generateAdapter = true)
data class LocationRefDto(
    val name: String,
    val url: String = ""
)
