package com.checkpoint.rfid_raw_material.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.source.db.Language
import com.checkpoint.rfid_raw_material.source.db.tblItem

@Dao
interface LanguageDao {
    @Query("SELECT * FROM Language")
    fun getLanguageList(): List<Language>


    @Query("SELECT * FROM Language ORDER BY idLanguage DESC LIMIT 1")
    fun getLastLang(): Language

    @Insert
    fun  insertLanguage(lang: Language)
}