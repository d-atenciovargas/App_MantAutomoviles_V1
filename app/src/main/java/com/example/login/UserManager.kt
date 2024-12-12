package com.example.login

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserManager.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LASTNAME = "lastname"
        private const val COLUMN_BIRTHDATE = "birthdate"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_ROLE = "role" // Nuevo campo para el rol
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_EMAIL TEXT UNIQUE," +
                "$COLUMN_PASSWORD TEXT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_LASTNAME TEXT," +
                "$COLUMN_BIRTHDATE TEXT," +
                "$COLUMN_PHONE TEXT," +
                "$COLUMN_ROLE TEXT)") // Añadir columna de rol
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(email: String, password: String, name: String, lastname: String, birthdate: String, phone: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NAME, name)
            put(COLUMN_LASTNAME, lastname)
            put(COLUMN_BIRTHDATE, birthdate)
            put(COLUMN_PHONE, phone)
            put(COLUMN_ROLE, "usuario estandar") // Asignar rol al usuario
        }
        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    fun emailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USERS, null, "$COLUMN_EMAIL=? AND $COLUMN_PASSWORD=?",
            arrayOf(email, password), null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}