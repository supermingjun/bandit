package com.bandit.came.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bandit.came.data.local.dao.HistoryDao
import com.bandit.came.data.local.dao.RuleDao
import com.bandit.came.data.local.entity.HistoryEntity
import com.bandit.came.data.local.entity.RuleEntity

@Database(
    entities = [RuleEntity::class, HistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BanditDatabase : RoomDatabase() {

    abstract fun ruleDao(): RuleDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: BanditDatabase? = null

        fun getInstance(context: Context): BanditDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    BanditDatabase::class.java,
                    "bandit.db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
