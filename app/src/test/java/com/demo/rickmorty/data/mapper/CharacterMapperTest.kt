package com.demo.rickmorty.data.mapper

import com.demo.rickmorty.data.remote.dto.CharacterDto
import com.demo.rickmorty.data.remote.dto.LocationRefDto
import com.demo.rickmorty.domain.model.CharacterStatus
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CharacterMapperTest {

    @Test
    fun `toDomain maps every field correctly`() {
        val dto = CharacterDto(
            id = 42,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            gender = "Male",
            image = "https://example.com/rick.png",
            origin = LocationRefDto("Earth (C-137)"),
            location = LocationRefDto("Citadel of Ricks")
        )

        val domain = dto.toDomain()

        assertThat(domain.id).isEqualTo(42)
        assertThat(domain.name).isEqualTo("Rick Sanchez")
        assertThat(domain.status).isEqualTo(CharacterStatus.ALIVE)
        assertThat(domain.species).isEqualTo("Human")
        assertThat(domain.gender).isEqualTo("Male")
        assertThat(domain.imageUrl).isEqualTo("https://example.com/rick.png")
        assertThat(domain.originName).isEqualTo("Earth (C-137)")
        assertThat(domain.locationName).isEqualTo("Citadel of Ricks")
    }

    @Test
    fun `toDomain maps unrecognized status string to UNKNOWN`() {
        val dto = CharacterDto(
            id = 1,
            name = "Mystery",
            status = "Weird",
            species = "Alien",
            gender = "unknown",
            image = "",
            origin = LocationRefDto("unknown"),
            location = LocationRefDto("unknown")
        )

        assertThat(dto.toDomain().status).isEqualTo(CharacterStatus.UNKNOWN)
    }

    @Test
    fun `status parsing is case insensitive`() {
        assertThat(CharacterStatus.fromRaw("ALIVE")).isEqualTo(CharacterStatus.ALIVE)
        assertThat(CharacterStatus.fromRaw("Dead")).isEqualTo(CharacterStatus.DEAD)
        assertThat(CharacterStatus.fromRaw("dead")).isEqualTo(CharacterStatus.DEAD)
    }
}
