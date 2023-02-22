package com.checkpoint.rfid_raw_material.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.checkpoint.rfid_raw_material.db.tblItem
import com.checkpoint.rfid_raw_material.source.dao.ItemDao

@Database(
    entities = [
        tblItem::class
    ], version = 2, exportSchema = false
)
abstract class ItemsDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemsDatabase? = null

        fun getDatabase(context: Context): ItemsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemsDatabase::class.java,
                    "items"
                ).build()
                INSTANCE = instance
                instance
            }
        }


    }
}