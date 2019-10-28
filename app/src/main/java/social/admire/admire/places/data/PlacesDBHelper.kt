package social.admire.admire.places.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import social.admire.admire.data.ContentData

class PlacesDBHelper(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun setPlaces(data: ArrayList<PlaceObject>) {
        writableDatabase.execSQL("drop table if exists $TABLE_NAME")
        writableDatabase.execSQL(
            "create table $TABLE_NAME (" +
                    "$KEY_ID integer, " +
                    "$KEY_TITLE text, " +
                    "$KEY_DESCRIPTION text, " +
                    "$KEY_RATING integer, " +
                    "$KEY_TAGS text, " +
                    "$KEY_IMAGES text, " +
                    "$KEY_AVATAR text, " +
                    "$KEY_AVATAR_SMALL text, " +
                    "$KEY_STATUS integer, " +
                    "$KEY_LATITUDE real, " +
                    "$KEY_LONGITUDE real" +
                    ");"
        )
        val contentValues = ContentValues()

        for(i in 0 until data.size) {
            contentValues.put(KEY_ID, data[i].id)
            contentValues.put(KEY_TITLE, data[i].title)
            contentValues.put(KEY_DESCRIPTION, data[i].description)
            contentValues.put(KEY_RATING, data[i].rating)
            contentValues.put(KEY_TAGS, JSONArray(data[i].tags).toString())
            contentValues.put(KEY_IMAGES, JSONArray(data[i].images).toString())
            contentValues.put(KEY_AVATAR, data[i].avatar)
            contentValues.put(KEY_AVATAR_SMALL, data[i].avatar_small)
            contentValues.put(KEY_STATUS, data[i].status)
            contentValues.put(KEY_LATITUDE, data[i].latitude)
            contentValues.put(KEY_LONGITUDE, data[i].longitude)
            writableDatabase.insert(TABLE_NAME, null, contentValues)
            contentValues.clear()
        }
    }

    fun getPlaces(): ArrayList<PlaceObject> {
        val cursor = writableDatabase.query(TABLE_NAME, null, null, null, null, null, null)
        val answer = ArrayList<PlaceObject>()

        if(cursor.moveToFirst()) {
            do {
                val place = PlaceObject(
                    cursor.getLong(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(KEY_RATING)),
                    ContentData.jsonToArray(JSONArray(cursor.getString(cursor.getColumnIndex(KEY_TAGS)))),
                    ContentData.jsonToArray(JSONArray(cursor.getString(cursor.getColumnIndex(KEY_IMAGES)))),
                    cursor.getString(cursor.getColumnIndex(KEY_AVATAR)),
                    cursor.getString(cursor.getColumnIndex(KEY_AVATAR_SMALL)),
                    cursor.getInt(cursor.getColumnIndex(KEY_STATUS)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))

                )
                answer.add(place)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return answer
    }

    companion object {
        const val DATABASE_NAME = "places_database"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "table_places"

        const val KEY_ID = "id"
        const val KEY_TITLE = "title"
        const val KEY_DESCRIPTION = "description"
        const val KEY_RATING = "rating"
        const val KEY_TAGS = "tags"
        const val KEY_IMAGES = "images"
        const val KEY_AVATAR = "avatar"
        const val KEY_AVATAR_SMALL = "avatar_small"
        const val KEY_STATUS = "status"
        const val KEY_LATITUDE = "latitude"
        const val KEY_LONGITUDE = "longitude"
    }
}