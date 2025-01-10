package com.example.firebase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.firebase.data.User

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT
            )
        """
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun insertUser(user: User): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, user.name)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun updateUser(user: User): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, user.name)
        }
        return db.update(TABLE_USERS, values, "$COLUMN_ID = ?", arrayOf(user.id.toString()))
    }

    fun deleteUser(userId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                users.add(User(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return users
    }
}