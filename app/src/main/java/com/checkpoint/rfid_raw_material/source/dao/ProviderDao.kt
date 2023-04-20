package com.checkpoint.rfid_raw_material.source.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.checkpoint.rfid_raw_material.source.db.Provider

@Dao
interface ProviderDao {
    @Query("SELECT * FROM Provider ORDER BY Name ASC")
    fun getProviders(): List<Provider>

    @Query("SELECT * FROM Provider WHERE id=:id")
    fun getProvider(id:Int): List<Provider>

    @Query("SELECT * FROM Provider ORDER BY ID DESC LIMIT 1")
    fun getLastProvider(): Provider

    @Insert
    fun insertProvider(provider: Provider)

    @Query("DELETE FROM provider")
    fun deleteAll()

    @Query("DELETE FROM Provider WHERE id=:idProvider")
    fun deleteProvider(idProvider: Int)
}