package com.checkpoint.rfid_raw_material.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.checkpoint.rfid_raw_material.source.dao.*
import com.checkpoint.rfid_raw_material.source.db.*

@Database(
    entities = [
        tblItem::class,
        Provider::class,
        Tags::class,
        Language::class
    ], version = 2, exportSchema = false
)
abstract class RawMaterialsDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun providerDao(): ProviderDao
    abstract fun tagsDao(): TagsDao
    abstract fun languageDao(): LanguageDao

    companion object {
        @Volatile
        private var INSTANCE: RawMaterialsDatabase? = null

        fun getDatabase(context: Context): RawMaterialsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RawMaterialsDatabase::class.java,
                    "rawMaterials"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}