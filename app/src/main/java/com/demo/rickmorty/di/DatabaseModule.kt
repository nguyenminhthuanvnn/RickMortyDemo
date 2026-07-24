package com.demo.rickmorty.di

import android.content.Context
import androidx.room.Room
import com.demo.rickmorty.data.local.RickMortyDatabase
import com.demo.rickmorty.data.local.dao.CharacterDao
import com.demo.rickmorty.data.local.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RickMortyDatabase {
        return Room.databaseBuilder(
            context,
            RickMortyDatabase::class.java,
            RickMortyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideCharacterDao(database: RickMortyDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    fun provideRemoteKeysDao(database: RickMortyDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }
}
