package com.demo.rickmorty.domain.usecase

import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.repository.CharacterRepository
import javax.inject.Inject

class GetCharacterUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int): Result<Character> = runCatching {
        repository.getCharacter(id)
    }
}
