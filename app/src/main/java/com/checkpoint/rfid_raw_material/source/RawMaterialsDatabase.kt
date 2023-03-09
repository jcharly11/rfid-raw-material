package com.checkpoint.rfid_raw_material.source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.checkpoint.rfid_raw_material.source.dao.InventoryDao
import com.checkpoint.rfid_raw_material.source.db.tblItem
import com.checkpoint.rfid_raw_material.source.dao.ItemDao
import com.checkpoint.rfid_raw_material.source.dao.ProviderDao
import com.checkpoint.rfid_raw_material.source.dao.TagsDao
import com.checkpoint.rfid_raw_material.source.db.Inventory
import com.checkpoint.rfid_raw_material.source.db.Provider
import com.checkpoint.rfid_raw_material.source.db.Tags

@Database(
    entities = [
        tblItem::class,
        Provider::class,
        Tags::class,
        Inventory::class
    ], version = 2, exportSchema = false
)
abstract class RawMaterialsDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun providerDao(): ProviderDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun tagsDao(): TagsDao

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