package com.checkpoint.rfid_raw_material.source.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.Tags
import com.checkpoint.rfid_raw_material.source.model.TagsLogs

@Dao
interface TagsDao {
    @Query("SELECT * FROM Tags")
    fun getTagsList():List<Tags>

    @Query("SELECT * FROM Tags WHERE readNumber=:readNumber")
    fun getTagsList(readNumber: Int):List<Tags>

    @Query("SELECT * FROM Tags WHERE readNumber=:readNumber")
    fun getTagsListLive(readNumber: Int):LiveData<List<Tags>>

    @Query("SELECT * FROM Tags ORDER BY ID DESC LIMIT 1")
    fun getLastTag(): Tags

    @Query("SELECT * FROM Tags ORDER BY ID DESC LIMIT 1")
    fun getReadNumber(): List<Tags>

    @Query("SELECT version,subversion,type,piece,idProvider AS provider,epc,timestamp FROM Tags WHERE readNumber=:readNumber")
    fun getTagsListForLogs(readNumber:Int):List<TagsLogs>

    @Query("Delete from Tags")
    fun deleteAllTags()

    @Insert
    fun  insertTag(tag: Tags)

}