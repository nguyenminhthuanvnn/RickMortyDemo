package com.demo.rickmorty.di

import com.demo.rickmorty.data.repository.CharacterRepositoryImpl
import com.demo.rickmorty.domain.repository.CharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the interface (domain) to its implementation (data) so anything
 * that injects CharacterRepository gets CharacterRepositoryImpl at runtime,
 * without ever referencing the impl class directly.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        impl: CharacterRepositoryImpl
    ): CharacterRepository
}
