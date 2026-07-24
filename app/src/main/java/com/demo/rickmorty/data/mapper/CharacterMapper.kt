package com.demo.rickmorty.data.mapper

import com.demo.rickmorty.data.local.entity.CharacterEntity
import com.demo.rickmorty.data.remote.dto.CharacterDto
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.model.CharacterStatus

/**
 * Isolates the DTO -> Domain conversion so nothing outside the data layer
 * ever needs to know the wire format.
 */
fun CharacterDto.toDomain(): Character = Character(
    id = id,
    name = name,
    status = CharacterStatus.fromRaw(status),
    species = species,
    gender = gender,
    imageUrl = image,
    originName = origin.name,
    locationName = location.name
)

fun Character.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status.name,
    species = species,
    gender = gender,
    imageUrl = imageUrl,
    originName = originName,
    locationName = locationName
)

fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = CharacterStatus.valueOf(status),
    species = species,
    gender = gender,
    imageUrl = imageUrl,
    originName = originName,
    locationName = locationName
)
