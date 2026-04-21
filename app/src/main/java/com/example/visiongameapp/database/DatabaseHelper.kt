package com.example.visiongameapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vision_game.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_RESULTS = "game_results"
        const val COLUMN_ID = "id"
        const val COLUMN_TEST_TYPE = "test_type"
        const val COLUMN_SCORE = "score"
        const val COLUMN_MAX_SCORE = "max_score"
        const val COLUMN_DATE = "date"
        const val COLUMN_DISEASE_INDICATION = "disease_indication"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_RESULTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TEST_TYPE + " TEXT,"
                + COLUMN_SCORE + " INTEGER,"
                + COLUMN_MAX_SCORE + " INTEGER,"
                + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_DISEASE_INDICATION + " TEXT" + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RESULTS")
        onCreate(db)
    }

    fun insertResult(testType: String, score: Int, maxScore: Int, disease: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TEST_TYPE, testType)
        values.put(COLUMN_SCORE, score)
        values.put(COLUMN_MAX_SCORE, maxScore)
        values.put(COLUMN_DISEASE_INDICATION, disease)
        val id = db.insert(TABLE_RESULTS, null, values)
        db.close()
        return id
    }

    fun deleteResult(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_RESULTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun getAllResults(): List<GameResultData> {
        val results = mutableListOf<GameResultData>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_RESULTS ORDER BY $COLUMN_DATE DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val data = GameResultData(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEST_TYPE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MAX_SCORE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISEASE_INDICATION))
                )
                results.add(data)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return results
    }

    fun getStats(): UserStats {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT score, max_score, date FROM $TABLE_RESULTS", null)
        
        var totalGames = 0
        var totalScore = 0f
        var totalMaxScore = 0f
        val dates = mutableSetOf<String>()
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        if (cursor.moveToFirst()) {
            do {
                totalGames++
                totalScore += cursor.getFloat(0)
                totalMaxScore += cursor.getFloat(1)
                
                val dateStr = cursor.getString(2)
                if (dateStr != null) {
                    val dateOnly = dateStr.split(" ")[0]
                    dates.add(dateOnly)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        val avgPercentage = if (totalMaxScore > 0) (totalScore / totalMaxScore) * 100 else 0f
        val streak = calculateStreak(dates)

        return UserStats(totalGames, streak, avgPercentage.toInt())
    }

    private fun calculateStreak(dates: Set<String>): Int {
        if (dates.isEmpty()) return 0
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        var streak = 0
        
        // Check today
        var currentSearch = sdf.format(calendar.time)
        if (dates.contains(currentSearch)) {
            streak++
        } else {
            // Check yesterday
            calendar.add(Calendar.DATE, -1)
            currentSearch = sdf.format(calendar.time)
            if (!dates.contains(currentSearch)) return 0
            streak++
        }
        
        // Count backwards
        while (true) {
            calendar.add(Calendar.DATE, -1)
            currentSearch = sdf.format(calendar.time)
            if (dates.contains(currentSearch)) {
                streak++
            } else {
                break
            }
        }
        
        return streak
    }
}

data class GameResultData(
    val id: Int,
    val testType: String,
    val score: Int,
    val maxScore: Int,
    val date: String,
    val diseaseIndication: String
)

data class UserStats(
    val totalGames: Int,
    val streak: Int,
    val avgPercentage: Int
)
