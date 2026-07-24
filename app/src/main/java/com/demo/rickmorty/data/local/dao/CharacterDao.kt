package com.demo.rickmorty.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.rickmorty.data.local.entity.CharacterEntity

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>)

    @Query("SELECT * FROM characters WHERE name LIKE '%' || :query || '%'")
    fun getCharacters(query: String): PagingSource<Int, CharacterEntity>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacter(id: Int): CharacterEntity?

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}
