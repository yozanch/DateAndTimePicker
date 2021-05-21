package com.example.myactivity

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION ) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "KegiatanDatabase"

        private val TABLE_CONTACTS = "KegiatanTable"

        private val KEY_ID      = "_id"
        private val KEY_KEGIATAN   = "kegiatan"
        private val KEY_TANGGAL   = "tanggal"
        private val KEY_WAKTU = "waktu"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE $TABLE_CONTACTS('_id' INTEGER PRIMARY KEY, 'kegiatan' TEXT, 'tanggal' TEXT, 'waktu' TEXT)")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    fun addKegiatan(emp: EmpModelClass): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_KEGIATAN, emp.kegiatan)
        contentValues.put(KEY_TANGGAL, emp.tanggal)
        contentValues.put(KEY_WAKTU, emp.waktu)

        val success = db.insert(TABLE_CONTACTS,null, contentValues)

        db.close()
        return success
    }

    fun deleteKegiatan(emp: EmpModelClass): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, emp.id)

        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + emp.id, null)

        db.close()
        return success
    }

    fun updateKegiatan(emp: EmpModelClass): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_KEGIATAN, emp.kegiatan)
        contentValues.put(KEY_TANGGAL, emp.tanggal)
        contentValues.put(KEY_WAKTU, emp.waktu)

        val success = db.update(TABLE_CONTACTS, contentValues, KEY_ID + "=" + emp.id, null )

        db.close()
        return success
    }

    fun viewEmployee(): ArrayList<EmpModelClass>{
        val empList: ArrayList<EmpModelClass> = ArrayList<EmpModelClass>()

        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase

        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery,null)
        }
        catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return  ArrayList()
        }
        var id: Int
        var kegiatan: String
        var tanggal: String
        var waktu: String

        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                kegiatan = cursor.getString(cursor.getColumnIndex(KEY_KEGIATAN))
                tanggal = cursor.getString(cursor.getColumnIndex(KEY_TANGGAL))
                waktu = cursor.getString(cursor.getColumnIndex(KEY_WAKTU))

                val emp = EmpModelClass(id = id, kegiatan = kegiatan, tanggal = tanggal, waktu = waktu)
                empList.add(emp)
            }while (cursor.moveToNext())
        }
        return empList
    }

}