package com.demo.rickmorty.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.rickmorty.data.local.entity.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE characterId = :characterId")
    suspend fun remoteKeysCharacterId(characterId: Int): RemoteKeys?

    @Query("SELECT lastUpdated FROM remote_keys ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getCreationTime(): Long?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}
