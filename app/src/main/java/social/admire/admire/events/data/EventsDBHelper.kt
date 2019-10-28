package social.admire.admire.events.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EventsDBHelper(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun setEvents(data: ArrayList<EventObject>) {
        writableDatabase.execSQL("drop table if exists $TABLE_NAME")
        writableDatabase.execSQL(
            "create table $TABLE_NAME (" +
                    "$KEY_ID integer, " +
                    "$KEY_NAME text, " +
                    "$KEY_DESCRIPTION text, " +
                    "$KEY_SCREEN_NAME text, " +
                    "$KEY_SITE text, " +
                    "$KEY_START_DATE integer, " +
                    "$KEY_FINISH_DATE integer, " +
                    "$KEY_LATITUDE real, " +
                    "$KEY_LONGITUDE real, " +
                    "$KEY_PHOTO_50 text, " +
                    "$KEY_PHOTO_200 text" +
                    ");"
        )
        val contentValues = ContentValues()

        for(i in 0 until data.size) {
            contentValues.put(KEY_ID, data[i].id)
            contentValues.put(KEY_NAME, data[i].name)
            contentValues.put(KEY_DESCRIPTION, data[i].description)
            contentValues.put(KEY_SCREEN_NAME, data[i].screen_name)
            contentValues.put(KEY_SITE, data[i].site)
            contentValues.put(KEY_START_DATE, data[i].start_date)
            contentValues.put(KEY_FINISH_DATE, data[i].finish_date)
            contentValues.put(KEY_LATITUDE, data[i].latitude)
            contentValues.put(KEY_LONGITUDE, data[i].longitude)
            contentValues.put(KEY_PHOTO_50, data[i].photo_50)
            contentValues.put(KEY_PHOTO_200, data[i].photo_200)
            writableDatabase.insert(TABLE_NAME, null, contentValues)
            contentValues.clear()
        }
    }

    fun getEvents(): ArrayList<EventObject> {
        val cursor = writableDatabase.query(TABLE_NAME, null, null, null, null, null, null)
        val answer = ArrayList<EventObject>()

        if(cursor.moveToFirst()) {
            do {
                val event = EventObject(
                    cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(KEY_SCREEN_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_SITE)),
                    cursor.getLong(cursor.getColumnIndex(KEY_START_DATE)),
                    cursor.getLong(cursor.getColumnIndex(KEY_FINISH_DATE)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                    cursor.getString(cursor.getColumnIndex(KEY_PHOTO_50)),
                    cursor.getString(cursor.getColumnIndex(KEY_PHOTO_200))

                )
                answer.add(event)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return answer
    }

    companion object {
        const val DATABASE_NAME = "events_database"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "table_events"

        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_DESCRIPTION = "description"
        const val KEY_SCREEN_NAME = "screen_name"
        const val KEY_SITE = "site"
        const val KEY_START_DATE = "start_date"
        const val KEY_FINISH_DATE = "finish_date"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
        const val KEY_PHOTO_50 = "photo_50"
        const val KEY_PHOTO_200 = "photo_200"

        const val KEY_PLACE = "place"
    }
}