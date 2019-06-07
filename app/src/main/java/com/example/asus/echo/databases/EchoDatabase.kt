package com.example.asus.echo.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.asus.echo.Songs
import com.example.asus.echo.fragments.FavoriteFragment
import java.lang.Exception

class EchoDatabase : SQLiteOpenHelper {


    object staticated {
        val TABLE_NAME = "FavoriteTable"
        val COL_ID = "SongID"
        val COL_TITLE = "SongTitle"
        val COL_ARTIST = "SongArtist"
        val COL_PATH = "SongPath"
        var DB_VERSION = 1
        val DB_NAME = "FavoriteDatabase"

    }

    var _songList = ArrayList<Songs>()


    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        sqliteDatabase?.execSQL(
            "CREATE TABLE " + staticated.TABLE_NAME + "( " + staticated.COL_ID + " INTEGER," + staticated.COL_ARTIST + " STRING," +
                    staticated.COL_TITLE + " STRING," + staticated.COL_PATH + " STRING);"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )

    constructor(context: Context?) : super(
        context, staticated.DB_NAME, null, staticated.DB_VERSION
    )


    fun storeAsFavorite(Id: Int?, artist: String?, songTitle: String?, songPath: String?) {
        val db = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(staticated.COL_ID, Id)
        contentValues.put(staticated.COL_ARTIST, artist)
        contentValues.put(staticated.COL_TITLE, songTitle)
        contentValues.put(staticated.COL_PATH, songPath)
        db.insert(staticated.TABLE_NAME, null, contentValues)
        db.close()


    }

    fun queryDBList(): ArrayList<Songs>? {
        try {
            val db = this.readableDatabase
            val query_params = "SELECT * FROM " + staticated.TABLE_NAME
            val cSor = db.rawQuery(query_params, null)
            if (cSor.moveToFirst()) {
                do {
                    var _id = cSor.getInt(cSor.getColumnIndexOrThrow(staticated.COL_ID))
                    var _artist = cSor.getString(cSor.getColumnIndexOrThrow(staticated.COL_ARTIST))
                    var _title = cSor.getString(cSor.getColumnIndexOrThrow(staticated.COL_TITLE))
                    var _songPath = cSor.getString(cSor.getColumnIndexOrThrow(staticated.COL_PATH))
                    _songList.add(Songs(_id, _title, _artist, _songPath, 0))


                } while (cSor.moveToNext())
            } else
                return null
            cSor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return _songList
    }

    fun checkifIdExist(_id: Int): Boolean {
        var storeId = -1000
        val db = this.readableDatabase
        val query_params = "SELECT * FROM " + staticated.TABLE_NAME + " WHERE SongID = '$_id'"
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(staticated.COL_ID))
            } while (cSor.moveToNext())
        } else {
            cSor.close()
            return false

        }
        cSor.close()
        return storeId != -1000
    }

    fun deleteFavorite(_id: Int) {
        val db = this.writableDatabase
        db.delete(staticated.TABLE_NAME, staticated.COL_ID + "=" + _id, null)
        db.close()
    }

    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        var queryParams = "SELECT * FROM " + staticated.TABLE_NAME
        val cSor = db.rawQuery(queryParams, null)
        if (cSor.moveToFirst()) {
            do {
                counter = counter + 1
            } while (cSor.moveToNext())
        } else {
            cSor.close()
            return 0
        }
        cSor.close()
        return counter
    }
}

