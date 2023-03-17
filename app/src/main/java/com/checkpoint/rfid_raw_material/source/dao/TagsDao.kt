package com.checkpoint.rfid_raw_material.source.dao

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

    @Query("SELECT * FROM Tags ORDER BY ID DESC LIMIT 1")
    fun getLastTag(): Tags

    @Query("SELECT T.version,T.subversion,T.type,T.piece,P.name AS provider,T.epc,T.timestamp FROM Tags AS T JOIN Provider AS P ON T.idProvider= P.id")
    fun getTagsListForLogs():List<TagsLogs>

    @Insert
    fun  insertTag(tag: Tags)

}