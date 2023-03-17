package com.checkpoint.rfid_raw_material.source.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.Tags

@Dao
interface TagsDao {
    @Query("SELECT * FROM Tags")
    fun getTagsList():List<Tags>

    @Query("SELECT * FROM Tags ORDER BY ID DESC LIMIT 1")
    fun getLastTag(): Tags

    @Insert
    fun  insertTag(tag: Tags)

}