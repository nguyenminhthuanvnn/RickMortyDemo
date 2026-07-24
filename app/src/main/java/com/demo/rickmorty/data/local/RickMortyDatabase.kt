package com.demo.rickmorty.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demo.rickmorty.data.local.dao.CharacterDao
import com.demo.rickmorty.data.local.dao.RemoteKeysDao
import com.demo.rickmorty.data.local.entity.CharacterEntity
import com.demo.rickmorty.data.local.entity.RemoteKeys

@Database(
    entities = [CharacterEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class RickMortyDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "rick_morty_db"
    }
}
