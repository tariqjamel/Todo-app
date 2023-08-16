package com.example.tododemo

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "MergedDatabase.db"
        const val DATABASE_VERSION = 1
        const val USER_TABLE = "user"
        const val TASK_TABLE = "task"
        const val USER_ID = "idUser"
        const val TASK_ID = "_id"
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val MAIL = "username"
        const val PASSWORD = "password"
        const val TASK_TITLE = "title"
        const val TASK_PRIORITY = "priority"
        const val TASK_TIME = "time"
        const val TASK_SCHEDULE_DATE = "scheduleDate"
        const val TASK_SCHEDULE_TIME = "scheduleTime"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CreateUserTable = "CREATE TABLE $USER_TABLE ( $USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$FIRST_NAME TEXT, $LAST_NAME TEXT, $MAIL TEXT, $PASSWORD TEXT)"
        db?.execSQL(CreateUserTable)

        val CreateTaskTable = "CREATE TABLE $TASK_TABLE ($TASK_ID INTEGER PRIMARY KEY," +
                " $TASK_TITLE TEXT, $TASK_PRIORITY TEXT, $TASK_TIME TEXT, $TASK_SCHEDULE_DATE TEXT, " +
                "$TASK_SCHEDULE_TIME TEXT, $USER_ID INTEGER)"
        db?.execSQL(CreateTaskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USER_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $TASK_TABLE")
        onCreate(db)
    }

    fun setUser(username: String, password: String, firstName: String, lastName: String): Long {
        val query = "INSERT INTO $USER_TABLE (${FIRST_NAME}, ${LAST_NAME}, ${MAIL}, ${PASSWORD}) " +
                "VALUES ('$firstName','$lastName','$username', '$password')"
        val db = writableDatabase
        if (isEmailExist(username, db)) {
            return -1L
        }

        db.execSQL(query)
        val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
        val insertedRowId = if (cursor.moveToFirst()) cursor.getLong(0) else -1L
        cursor.close()

        return insertedRowId
    }

    fun isEmailExist(username: String, db: SQLiteDatabase): Boolean {
        val query = "SELECT $USER_ID FROM $USER_TABLE WHERE $MAIL = ?"
        val selectionArg = arrayOf(username)
        val cursor = db.rawQuery(query, selectionArg)
        val usernameExists = cursor.count > 0
        cursor.close()
        return usernameExists
    }

    fun getUser(username: String, password: String): Cursor? {
        val db = readableDatabase
        val query = "SELECT * FROM $USER_TABLE WHERE $MAIL = ? AND $PASSWORD = ?"
        val selectionArg = arrayOf(username, password)
        val cursor = db.rawQuery(query, selectionArg)

        if (cursor.moveToFirst()) {
            return cursor
        }
        return null
    }

    fun getName(userId: Int): Cursor? {
        val db = readableDatabase
        val query = "SELECT $FIRST_NAME , $LAST_NAME FROM $USER_TABLE WHERE $USER_ID = ?"
        val selectionArg = arrayOf(userId.toString())
        return db.rawQuery(query, selectionArg)
    }

    fun addTask(task: Task) {
        val db = this.writableDatabase
        val query = "INSERT INTO $TASK_TABLE ($TASK_TITLE, $TASK_PRIORITY, $TASK_TIME, $TASK_SCHEDULE_DATE, $TASK_SCHEDULE_TIME, $USER_ID) " +
                "VALUES ('${task.title}', '${task.priority}', '${task.time}', '${task.scheduleDate}', '${task.scheduleTime}', ${task.userId})"
        db.execSQL(query)
        db.close()
    }

    fun getTask(userId: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TASK_TABLE WHERE $USER_ID = ${userId}", null)
    }

    fun updateTask(task: Task) {
        val db = this.writableDatabase
        val query = "UPDATE $TASK_TABLE SET $TASK_TITLE = '${task.title}', " + "$TASK_PRIORITY = '${task.priority}'," +
                "$TASK_SCHEDULE_DATE = '${task.scheduleDate}', $TASK_SCHEDULE_TIME = '${task.scheduleTime}' WHERE $TASK_ID = ${task.taskId}"
        db.execSQL(query)
        db.close()
    }

    fun deleteTask(id: Int) {
        val db = this.writableDatabase
        db.delete(TASK_TABLE, "$TASK_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}