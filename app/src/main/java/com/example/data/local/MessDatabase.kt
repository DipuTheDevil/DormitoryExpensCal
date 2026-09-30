package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CalculationSessionDao
import com.example.data.local.dao.MemberDao
import com.example.data.local.entity.CalculationSessionEntity
import com.example.data.local.entity.MemberEntity

/**
 * Room Database holder for Mess Expense Tracker and calculation session logging.
 */
@Database(
    entities = [
        MemberEntity::class,
        CalculationSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MessDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun calculationSessionDao(): CalculationSessionDao

    companion object {
        @Volatile
        private var INSTANCE: MessDatabase? = null

        fun getDatabase(context: Context): MessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MessDatabase::class.java,
                    "mess_expenses.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
