package social.admire.admire.places.data

import org.json.JSONArray
import org.json.JSONObject
import social.admire.admire.data.ContentData
import social.admire.admire.data.Server

class PlaceObject(
    val id: Long,
    val title: String,
    val description: String,
    val rating: Int,
    val tags: Array<String>,
    val images: Array<String>,
    val avatar: String,
    val avatar_small: String,
    val status: Int,
    val latitude: Double,
    val longitude: Double
) {

    companion object {
        fun fromJSON(json: JSONObject): PlaceObject {
            return  PlaceObject(
                json.getLong(PlacesDBHelper.KEY_ID),
                json.getString(PlacesDBHelper.KEY_TITLE),
                json.getString(PlacesDBHelper.KEY_DESCRIPTION),
                json.getInt(PlacesDBHelper.KEY_RATING),
                ContentData.jsonToArray(
                    JSONArray(
                        json.getString(PlacesDBHelper.KEY_TAGS).replace(
                            "u0",
                            "\\u0"
                        )
                    )
                ),
                ContentData.jsonToArray(
                    JSONArray(json.getString(PlacesDBHelper.KEY_IMAGES)),
                    true
                ),
                run {
                    if (json.getString(PlacesDBHelper.KEY_AVATAR).startsWith("http")) json.getString(
                        PlacesDBHelper.KEY_AVATAR
                    )
                    else Server.url + "/images/" + json.getString(PlacesDBHelper.KEY_AVATAR).substringAfterLast(
                        "/"
                    )
                },
                json.getString(PlacesDBHelper.KEY_AVATAR_SMALL),
                json.getInt(PlacesDBHelper.KEY_STATUS),
                json.getDouble(PlacesDBHelper.KEY_LATITUDE),
                json.getDouble(PlacesDBHelper.KEY_LONGITUDE)
            )
        }
    }
}