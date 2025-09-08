// File: com/example/topmovers/Room/AppDatabase.kt

package com.example.topmovers.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.topmovers.data.model.CompanyInfo
import com.example.topmovers.data.model.TopMover
import com.example.topmovers.data.model.WatchList

@Database(
    entities = [
        WatchList::class,
        TopMover::class,
        WatchlistStockCrossRef::class,
        CompanyInfo::class // NEW: Add CompanyInfo entity
    ],
    version = 2, // MODIFICATION: Bump version from 1 to 2
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun watchlistDao(): WatchlistDao

    companion object {
        // NEW: This is the migration logic from version 1 to 2.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create the new company_info table
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `company_info` (`symbol` TEXT NOT NULL, `name` TEXT, `description` TEXT, `assetType` TEXT, `sector` TEXT, `industry` TEXT, `marketCap` TEXT, `peRatio` TEXT, `beta` TEXT, `dividendYield` TEXT, `profitMargin` TEXT, `week52High` TEXT, `week52Low` TEXT, `exchange` TEXT, `lastFetched` INTEGER NOT NULL, PRIMARY KEY(`symbol`))"
                )

                // 2. Rebuild the 'stocks' table to support the new schema (composite primary key and new columns)
                db.execSQL(
                    "CREATE TABLE `stocks_new` (`ticker` TEXT NOT NULL, `price` TEXT NOT NULL, `changeAmount` TEXT NOT NULL, `changePercentage` TEXT NOT NULL, `volume` TEXT NOT NULL, `cacheType` TEXT NOT NULL, `lastFetched` INTEGER NOT NULL, PRIMARY KEY(`ticker`, `cacheType`))"
                )
                db.execSQL(
                    "INSERT INTO `stocks_new` (ticker, price, changeAmount, changePercentage, volume, cacheType, lastFetched) SELECT ticker, price, changeAmount, changePercentage, volume, '', 0 FROM `stocks`"
                )
                db.execSQL("DROP TABLE `stocks`")
                db.execSQL("ALTER TABLE `stocks_new` RENAME TO `stocks`")
            }
        }
    }
}